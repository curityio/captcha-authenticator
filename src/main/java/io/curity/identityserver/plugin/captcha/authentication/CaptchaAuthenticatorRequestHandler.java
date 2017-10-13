package io.curity.identityserver.plugin.captcha.authentication;

import io.curity.identityserver.plugin.captcha.config.CaptchaAuthenticatorPluginConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.attribute.Attributes;
import se.curity.identityserver.sdk.attribute.AuthenticationAttributes;
import se.curity.identityserver.sdk.attribute.ContextAttributes;
import se.curity.identityserver.sdk.authentication.AuthenticationResult;
import se.curity.identityserver.sdk.authentication.AuthenticatorRequestHandler;
import se.curity.identityserver.sdk.http.HttpRequest;
import se.curity.identityserver.sdk.http.HttpResponse;
import se.curity.identityserver.sdk.http.HttpStatus;
import se.curity.identityserver.sdk.service.HttpClient;
import se.curity.identityserver.sdk.service.Json;
import se.curity.identityserver.sdk.web.Request;
import se.curity.identityserver.sdk.web.Response;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static se.curity.identityserver.sdk.web.Response.ResponseModelScope.NOT_FAILURE;
import static se.curity.identityserver.sdk.web.ResponseModel.templateResponseModel;

public class CaptchaAuthenticatorRequestHandler implements AuthenticatorRequestHandler<RequestModel>
{
    private static final Logger _logger = LoggerFactory.getLogger(CaptchaAuthenticatorRequestHandler.class);

    private final String _siteKey;
    private final String _secretKey;
    private final URI _verificationUrl;
    private final HttpClient _httpClient;
    private final Json _json;
    private final String _cspChildSrc;
    private final String _cspStyleSrc;

    public CaptchaAuthenticatorRequestHandler(CaptchaAuthenticatorPluginConfig config)
    {
        _siteKey = config.getSiteKey();
        _secretKey = config.getSecretKey();
        _verificationUrl = config.getVerificationURL();
        _httpClient = config.getHttpClient();
        _json = config.getJson();
        _cspChildSrc = config.getCspChildSrcSetting();
        _cspStyleSrc = config.getCspStyleSrcSetting();
    }

    @Override
    public Optional<AuthenticationResult> get(RequestModel requestModel, Response response)
    {
        _logger.trace("GET request received, POST needed for authentication");

        return Optional.empty();
    }

    @Override
    public Optional<AuthenticationResult> post(RequestModel requestModel, Response response)
    {
        RequestModel.Post request = requestModel.getPostRequestModel();
        HttpResponse verificationResponse = sendVerificationRequest(request);

        if (verificationResponse.statusCode() != 200)
        {
            _logger.debug("Unexpected status code {} from verification URL {}, not authenticating",
                    verificationResponse.statusCode(), _verificationUrl);

            return Optional.empty();
        }

        String responseBody = verificationResponse.body(HttpResponse.asString());

        _logger.trace("Received re-CAPTCHA verification response: {}", responseBody);

        Attributes verificationAttributes = _json.toAttributes(responseBody);

        boolean success = verificationAttributes.getMandatoryValue("success", Boolean.class);

        if (!success)
        {
            _logger.debug("Authentication denied as response came back negative");
            _logger.trace("Error code(s) provided with response: {}",
                    verificationAttributes.getOptionalValue("error-codes"));

            return Optional.empty();
        }

        // We don't need the "success" value for context as success is given at this stage
        ContextAttributes contextAttributes = ContextAttributes.of(verificationAttributes.removeAttribute("success"));
        AuthenticationAttributes authenticationAttributes = AuthenticationAttributes.of(
                request.getClientIpAddress(), contextAttributes);

        return Optional.of(new AuthenticationResult(authenticationAttributes));
    }

    @Override
    public RequestModel preProcess(Request request, Response response)
    {
        // set the template and model for responses on the NOT_FAILURE scope
        response.setResponseModel(templateResponseModel(
                singletonMap("_siteKey", _siteKey),
                "authenticate/get"), NOT_FAILURE);

        // on request validation failure, we should use the same template as for NOT_FAILURE
        response.setResponseModel(templateResponseModel(emptyMap(), "authenticate/get"), HttpStatus.BAD_REQUEST);

        response.putViewData("_cspChildSrc", _cspChildSrc, Response.ResponseModelScope.ANY);
        response.putViewData("_cspStyleSrc", _cspStyleSrc, Response.ResponseModelScope.ANY);

        return new RequestModel(request);
    }

    private HttpResponse sendVerificationRequest(RequestModel.Post request)
    {
        Map<String, String> parameters = new HashMap<>(3);
        parameters.put("secret", _secretKey);
        parameters.put("response", request.getCaptchaResponseParam());
        parameters.put("remoteip", request.getClientIpAddress());

        return _httpClient.request(_verificationUrl)
                .contentType("application/x-www-form-urlencoded")
                .body(HttpRequest.fromString(formBodyStringFromMap(parameters)))
                .post()
                .response();
    }

    private static String formBodyStringFromMap(Map<String, String> params)
    {
        return params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + urlEncode(entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    private static String urlEncode(String text)
    {
        try
        {
            return URLEncoder.encode(text, "UTF-8");
        }
        catch (UnsupportedEncodingException ignored)
        {
            // This can never happen
            throw new RuntimeException("UTF-8 encoding missing");
        }
    }
}
