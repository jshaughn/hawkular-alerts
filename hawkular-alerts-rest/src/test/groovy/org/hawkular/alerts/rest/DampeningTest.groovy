/**
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
package org.hawkular.alerts.rest

import org.hawkular.alerts.api.model.dampening.Dampening
import org.jboss.logging.Logger
import org.junit.Test

import org.hawkular.alerts.api.model.trigger.Trigger
import org.hawkular.alerts.api.model.dampening.Dampening.Type
import org.hawkular.alerts.api.model.trigger.Trigger.Mode
import static org.junit.Assert.assertEquals

/**
 * Dampening REST tests.
 *
 * @author Lucas Ponce
 */
class DampeningTest extends AbstractTestBase {
    private static final Logger log = Logger.getLogger(DampeningTest.class);

    @Test
    void createDampening() {
       Trigger testTrigger = new Trigger("test-trigger-6", "No-Metric");

        // make sure clean test trigger exists
        client.delete(path: "triggers/test-trigger-6")
        def resp = client.post(path: "triggers", body: testTrigger)
        assertEquals(200, resp.status)

        Dampening d = new Dampening("test-trigger-6", Mode.FIRE, Type.RELAXED_COUNT, 1, 1, 1);

        resp = client.post(path: "triggers/test-trigger-6/dampenings", body: d)
        assertEquals(200, resp.status)

        resp = client.get(path: "triggers/test-trigger-6/dampenings/" + d.getDampeningId());
        assertEquals(200, resp.status)
        assertEquals("RELAXED_COUNT", resp.data.type)

        d.setType(Type.STRICT)
        resp = client.put(path: "triggers/test-trigger-6/dampenings/" + d.getDampeningId(), body: d)
        assertEquals(200, resp.status)

        resp = client.get(path: "triggers/test-trigger-6/dampenings/" + d.getDampeningId())
        assertEquals(200, resp.status)
        assertEquals("STRICT", resp.data.type)

        resp = client.get(path: "triggers/test-trigger-6/dampenings")
        assertEquals(200, resp.status)
        assertEquals(1, resp.data.size())

        resp = client.get(path: "triggers/test-trigger-6/dampenings/mode/FIRE")
        assertEquals(200, resp.status)
        assertEquals("test-trigger-6", resp.data.triggerId)

        resp = client.delete(path: "triggers/test-trigger-6/dampenings/" + d.getDampeningId())
        assertEquals(200, resp.status)
    }

}
