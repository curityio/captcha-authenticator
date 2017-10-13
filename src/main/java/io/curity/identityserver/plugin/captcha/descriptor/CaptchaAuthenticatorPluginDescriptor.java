/*
 *  Copyright 2017 Curity AB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
