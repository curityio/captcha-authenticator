package io.curity.identityserver.plugin.captcha.config;

import se.curity.identityserver.sdk.config.Configuration;
import se.curity.identityserver.sdk.config.annotation.DefaultString;
import se.curity.identityserver.sdk.config.annotation.DefaultURI;
import se.curity.identityserver.sdk.config.annotation.Description;
import se.curity.identityserver.sdk.service.HttpClient;
import se.curity.identityserver.sdk.service.Json;

import java.net.URI;

@SuppressWarnings("InterfaceNeverImplemented")
public interface CaptchaAuthenticatorPluginConfig extends Configuration
{
    @Description("Key to be used in the HTML templates for the CAPTCHA authenticator")
    String getSiteKey();

    @Description("Secret key used for communication between the CAPTCHA authenticator and Google")
    String getSecretKey();

    @Description(
            "The URL pointing to where the CAPTCHA verification request will be sent. Should normally not be changed.")
    @DefaultURI("https://www.google.com/recaptcha/api/siteverify")
    URI getVerificationURL();

    @Description("The HTTP client to be used when sending the CAPTCHA verification request")
    HttpClient getHttpClient();

    @Description("Get a configured JSON parser for parsing the CAPTCHA verification response")
    Json getJson();

    @Description("The Content-Security Policy to use for child-src. Should normally not be changed.")
    @DefaultString("child-src 'self' https://www.google.com/recaptcha/ https://www.gstatic.com/recaptcha/;")
    String getCspChildSrcSetting();

    @Description("The Content-Security Policy to use for style-src. Should normally not be changed.")
    @DefaultString(
            "style-src 'self' 'unsafe-inline' https://www.google.com/recaptcha/ https://www.gstatic.com/recaptcha/;")
    String getCspStyleSrcSetting();
}
