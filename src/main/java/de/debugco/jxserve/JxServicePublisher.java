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

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JxServicePublisher {
    private final static Logger LOGGER = Logger.getLogger(JxServicePublisher.class.getName());
    private Configuration configuration;

    private HttpServer httpServer;

    private JxServiceRegistry serviceRegistry = new JxServiceRegistry();

    public JxServicePublisher(Configuration configuration) {
        this.configuration = configuration;
        initHttpServer();
    }

    public void initHttpServer() {
        try {
            if (configuration.isServerHttps()) {
                httpServer = HttpsServer.create(new InetSocketAddress(configuration.getServerInterfaceHost(), configuration.getServerPort()), configuration.getServerBacklog());
            } else  {
                httpServer = HttpServer.create(new InetSocketAddress(configuration.getServerInterfaceHost(), configuration.getServerPort()), configuration.getServerBacklog());
            }

            httpServer.createContext("/", new IndexPageHttpHandler(serviceRegistry));
            httpServer.setExecutor(new DefaultInfiniteExecutor());
            httpServer.start();
            LOGGER.log(Level.INFO, String.format("JxServe was started on \"%s:%d\"", httpServer.getAddress().getHostString(), httpServer.getAddress().getPort()));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "The Http-Server could not be instantiated (port already bound?)", e);
        }
    }

    public boolean publish(Class<?> clazz) {
        WebService webService = clazz.getAnnotation(WebService.class);
        if (webService == null) {
            LOGGER.log(Level.WARNING, String.format("Class \"%s\" is not a valid WebService-Class", clazz.getName()));
            return false;
        }

        HttpContext httpContext = httpServer.createContext(configuration.getServerPrefix() + "/" + webService.name() + "." + webService.portName());

        try {
            Endpoint endpoint = Endpoint.create(clazz.newInstance());
            endpoint.publish(httpContext);

            serviceRegistry.addEndpoint(endpoint, httpContext);
            LOGGER.log(Level.INFO, String.format("Service \"%s\" was successfully published under %s", clazz.getName(), httpContext.getPath()));

            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Endpoint could not be published", e);
            return false;
        }
    }

    private static class DefaultInfiniteExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            Thread t = new Thread(command);
            t.start();
        }
    }
}
