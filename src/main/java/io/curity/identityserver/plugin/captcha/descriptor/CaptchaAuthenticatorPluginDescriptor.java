package io.curity.identityserver.plugin.captcha.descriptor;

import io.curity.identityserver.plugin.captcha.authentication.CaptchaAuthenticatorRequestHandler;
import io.curity.identityserver.plugin.captcha.config.CaptchaAuthenticatorPluginConfig;
import se.curity.identityserver.sdk.authentication.AuthenticatorRequestHandler;
import se.curity.identityserver.sdk.plugin.descriptor.AuthenticatorPluginDescriptor;

import java.util.Map;

import static java.util.Collections.singletonMap;

public final class CaptchaAuthenticatorPluginDescriptor
        implements AuthenticatorPluginDescriptor<CaptchaAuthenticatorPluginConfig>
{
    @Override
    public String getPluginImplementationType()
    {
        return "captcha";
    }

    @Override
    public Class<? extends CaptchaAuthenticatorPluginConfig> getConfigurationType()
    {
        return CaptchaAuthenticatorPluginConfig.class;
    }

    @Override
    public Map<String, Class<? extends AuthenticatorRequestHandler<?>>> getAuthenticationRequestHandlerTypes()
    {
        return singletonMap("index", CaptchaAuthenticatorRequestHandler.class);
    }

}
