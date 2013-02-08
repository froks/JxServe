/*
 * Copyright 2013 Florian Roks
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.debugco.jxserve;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration {
    private final static Logger LOGGER = Logger.getLogger(Configuration.class.getName());

    private Properties properties;
    private PublishUrl publishUrl;

    public Configuration(String fileName) {
        properties = new Properties();
        if (fileName != null && !"".equals(fileName)) {
            File file = new File(fileName);
            try {
                if (!file.exists()) {
                    throw new RuntimeException("The configuration-file " + fileName + " couldn't be found");
                }
                properties.load(new FileInputStream(file));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            publishUrl = new PublishUrl(properties.getProperty("publish.url"));
        }
    }

    private int getIntProperty(String keyName, int defaultValue) {
        String property = null;
        try {
            property = properties.getProperty(keyName, String.valueOf(defaultValue));
            return Integer.parseInt(property);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, String.format("The configured backlog isn't a number (%s)", property), e);
            return defaultValue;
        }
    }

    public boolean isServerHttps() {
        return publishUrl != null && publishUrl.isHttpsProtocol();
    }

    public int getServerPort() {
        int port = publishUrl != null ? publishUrl.getPort() : 8080;
        if (port <= 0) {
            if (publishUrl != null && publishUrl.isHttpsProtocol) {
                port = 8443;
            } else {
                port = 8080;
            }
        }
        return port;
    }

    public String getServerInterfaceHost() {
        return publishUrl != null ? publishUrl.getHost() : "0.0.0.0";
    }

    public int getServerBacklog() {
        return getIntProperty("httpserver.backlog", 0); // 0 means System-default is used
    }

    public String getServerPrefix() {
        return publishUrl != null ? publishUrl.getPrefix() : "/services";
    }

    private static final class PublishUrl {
        private final static Logger LOGGER = Logger.getLogger(PublishUrl.class.getName());
        private String protocol;
        private boolean isHttpsProtocol;
        private int port;
        private String prefix;
        private String host;

        private PublishUrl(String publishUrl) {
            try {
                URL url = new URL(publishUrl);
                protocol = url.getProtocol();
                isHttpsProtocol = "https".equalsIgnoreCase(protocol);
                port = url.getPort();
                host = url.getHost();
                prefix = url.getPath();
            } catch (MalformedURLException e) {
                LOGGER.log(Level.WARNING, String.format("The publishing url \"%s\" is not a valid url", publishUrl), e);
            }
        }

        public boolean isHttpsProtocol() {
            return isHttpsProtocol;
        }

        public int getPort() {
            return port;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getHost() {
            return host;
        }
    }
}
