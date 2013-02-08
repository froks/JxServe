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
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

public class IndexPageHttpHandler implements HttpHandler {
    private JxServiceRegistry serviceRegistry;

    public IndexPageHttpHandler(JxServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        httpExchange.getResponseBody().write("<html><head><title>JxServe-Webservices</title></head><body>".getBytes());
        httpExchange.getResponseBody().write("<h2>Webservices:</h2><hr />".getBytes());
        httpExchange.getResponseBody().write("<ul>".getBytes());
        Map<Endpoint, HttpContext> services = serviceRegistry.getServices();
        for (Map.Entry<Endpoint, HttpContext> entry : services.entrySet()) {
            Endpoint endpoint = entry.getKey();
            HttpContext httpContext = entry.getValue();
            Object obj = endpoint.getImplementor();
            WebService webService = obj.getClass().getAnnotation(WebService.class);
            String url = httpContext.getPath() + "?wsdl";
            String name = webService.name() + "." + webService.portName();
            httpExchange.getResponseBody().write(String.format("<li><a href=\"%s\">%s</a></li>", url, name).getBytes());
        }
        httpExchange.getResponseBody().write("</ul>".getBytes());
        httpExchange.getResponseBody().write("<hr />".getBytes());
        httpExchange.getResponseBody().write("<h6>provided by JxServe</h6>".getBytes());
        httpExchange.getResponseBody().write("</body></html>".getBytes());
        httpExchange.close();
    }
}
