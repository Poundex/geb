/*
 * Copyright 2014 the original author or authors.
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

package geb.driver

import org.openqa.selenium.Capabilities
import org.openqa.selenium.Platform
import org.openqa.selenium.WebDriver

class BrowserStackDriverFactory {

	WebDriver create(String specification, String user, String accessKey, Map<String, Object> capabilities = [:]) {
		def remoteDriverOperations = new RemoteDriverOperations(getClass().classLoader)
		Class<? extends WebDriver> remoteWebDriverClass = remoteDriverOperations.remoteWebDriverClass
		if (!remoteWebDriverClass) {
			throw new ClassNotFoundException('org.openqa.selenium.remote.RemoteWebDriver needs to be on the classpath to create BrowserStack RemoteWebDriverInstances')
		}

		def url = new URL("http://$user:$accessKey@hub.browserstack.com/wd/hub")

		def parts = specification.split(":", 3)
		def name = parts[0]
		def platform = parts.size() > 1 ? parts[1] : null
		def version = parts.size() > 2 ? parts[2] : null

		def browser = remoteDriverOperations.softLoadRemoteDriverClass('DesiredCapabilities')."$name"();
		capabilities.each { key, value ->
			browser.setCapability(key, value)
		}
		if (platform) {
			try {
				platform = Platform."${platform.toUpperCase()}"
			} catch (MissingPropertyException ignore) {
			}
			browser.setCapability("platform", platform)
		}
		if (version != null) {
			browser.setCapability("version", version.toString())
		}
		browser.setCapability("browserstack.local", "true")

		remoteWebDriverClass.getConstructor(URL, Capabilities).newInstance(url, browser)
	}
}
