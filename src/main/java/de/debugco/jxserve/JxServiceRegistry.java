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

import javax.xml.ws.Endpoint;
import java.util.HashMap;
import java.util.Map;

public class JxServiceRegistry {
    private Map<Endpoint, HttpContext> services = new HashMap<Endpoint, HttpContext>();

    public JxServiceRegistry() {
    }

    public void addEndpoint(Endpoint endpoint, HttpContext httpContext) {
        services.put(endpoint, httpContext);
    }

    public Map<Endpoint, HttpContext> getServices() {
        return services;
    }
}
