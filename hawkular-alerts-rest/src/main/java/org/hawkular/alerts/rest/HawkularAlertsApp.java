/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
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
package org.hawkular.alerts.rest;

import org.jboss.logging.Logger;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Base class for REST module.
 *
 * @author Jay Shaughnessy
 * @author Lucas Ponce
 */
@ApplicationPath("/")
public class HawkularAlertsApp extends Application {
    private final Logger log = Logger.getLogger(HawkularAlertsApp.class);

    public HawkularAlertsApp() {
        log.debugf("Hawkular Alerts REST starting...");
    }
}
