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
package org.hawkular.alerts.api.model.dampening;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jay shaughnessy
 * @author lucas ponce 
 */
public class Timeout {

    private String triggerId;
    private boolean canceled;
    private long time;
    private long eventTime;

    public Timeout(String triggerId, long fromNowInMillis) {
        this.triggerId = triggerId;
        this.canceled = false;
        this.eventTime = System.currentTimeMillis();
        this.time = eventTime + fromNowInMillis;
    }

    public String getTriggerId() {
        return triggerId;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public long getTime() {
        return time;
    }

    public long getEventTime() {
        return eventTime;
    }

    @Override
    public String toString() {
        return "Timeout [triggerId=" + triggerId + ", canceled=" + canceled + ", time="
                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(time)) + "]";
        // + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(time) + "]";
    }

}
