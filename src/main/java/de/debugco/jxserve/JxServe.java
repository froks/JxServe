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

import de.debugco.jxserve.shutdownservice.JxShutdownServiceImpl;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JxServe {
    private final static Logger LOGGER = Logger.getLogger(JxServe.class.getName());

    private static boolean quit = false;

    public static void main(String[] args) {
        Configuration configuration = new FileConfiguration(System.getProperty("config")); // TODO configuration file / argument handling
        JxServicePublisher jxServicePublisher = new JxServicePublisher(configuration);
        jxServicePublisher.publish(JxShutdownServiceImpl.class);
        if (args != null) {
            for (String clazzName : args) {
                try {
                    Class clazz = Class.forName(clazzName);
                    jxServicePublisher.publish(clazz);
                } catch (ClassNotFoundException e) {
                    LOGGER.log(Level.WARNING, String.format("The class \"%s\" couldn't be found", clazzName), e);
                }
            }
        }
        while(!quit) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                quit = true;
            }
        }
    }

    public static void setQuit(boolean quit) {
        JxServe.quit = quit;
    }
}
