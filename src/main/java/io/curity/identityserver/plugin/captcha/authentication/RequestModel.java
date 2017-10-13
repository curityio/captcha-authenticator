package io.curity.identityserver.plugin.captcha.authentication;

import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.web.Request;

import java.util.Optional;

public final class RequestModel
{
    @Nullable
    private final Post _postRequestModel;

    RequestModel(Request request)
    {
        _postRequestModel = request.isPostRequest() ? new Post(request) : null;
    }

    Post getPostRequestModel()
    {
        return Optional.ofNullable(_postRequestModel)
                .orElseThrow(() -> new RuntimeException("Post RequestModel does not exist"));
    }

    static class Post
    {
        private static final String CAPTCHA_RESPONSE_PARAM = "g-recaptcha-response";

        private final String _captchaResponseParam;
        private final String _clientIpAddress;

        Post(Request request)
        {
            _captchaResponseParam = request.getFormParameterValueOrError(CAPTCHA_RESPONSE_PARAM);
            _clientIpAddress = request.getClientIpAddress();
        }

        String getCaptchaResponseParam()
        {
            return _captchaResponseParam;
        }

        String getClientIpAddress()
        {
            return _clientIpAddress;
        }
    }
}
