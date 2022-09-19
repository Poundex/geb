/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.gradle.saucelabs

import geb.gradle.cloud.ExternalTunnel
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.process.ExecOperations

import javax.inject.Inject

abstract class SauceConnect extends ExternalTunnel {

    public static final String TUNNEL_ID_ENV_VAR = "GEB_SAUCE_LABS_TUNNEL_ID"

    final String outputPrefix = 'sauce-connect'
    final String tunnelReadyMessage = 'Sauce Connect is up, you may start your tests'

    @Internal
    int port = 4445

    @Internal
    List<String> additionalOptions = []

    @Inject
    SauceConnect(ExecOperations execOperations) {
        super(execOperations)
        identifier.convention("")
    }

    @Internal
    abstract Property<String> getUsername()

    @Internal
    abstract Property<String> getAccessKey()

    @Internal
    abstract Property<String> getIdentifier()

    @Override
    List<String> assembleCommandLine() {
        def options = [executablePath, '--user', username.get(), '--api-key', accessKey.get(), '--se-port', port.toString()]
        options << '--tunnel-identifier' << identifier.get()
        options + additionalOptions
    }
}
