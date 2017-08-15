/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
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
package org.hawkular.alerts.engine.util;

import org.hawkular.alerts.api.model.trigger.Match;
import org.hawkular.alerts.api.model.trigger.Mode;
import org.hawkular.alerts.api.model.trigger.Trigger;

/**
 * @author Jay Shaughnessy
 * @author Lucas Ponce
 */
public class SourceTrigger {

    /** The tenant of the root Trigger */
    private String tenantId;

    /** The id of the root Trigger */
    private String id;

    /** The data source being applied to the root trigger. */
    private String source;

    /** The root Trigger */
    private transient Trigger trigger;

    /** Used internally by the rules engine. Indicates current mode of a trigger: FIRING or AUTORESOLVE. */
    private transient Mode mode;

    /** Used internally by the rules engine. Indicates current match of a trigger: ALL or ANY. */
    private transient Match match;

    /** Used internally by the rules engine. Indicated whether this SourceTrigger is enabled or disabled. */
    private transient boolean enabled;

    public SourceTrigger(Trigger trigger, String source) {
        super();
        this.tenantId = trigger.getTenantId();
        this.id = trigger.getId();
        this.source = source;
        this.trigger = trigger;
        this.mode = Mode.FIRING;
        this.match = trigger.getFiringMatch();
        this.enabled = true;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    public void setTrigger(Trigger trigger) {
        this.trigger = trigger;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SourceTrigger other = (SourceTrigger) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (tenantId == null) {
            if (other.tenantId != null)
                return false;
        } else if (!tenantId.equals(other.tenantId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SourceTrigger [tenantId=" + tenantId + ", id=" + id + ", source=" + source + ", mode=" + mode
                + ", match=" + match + "]";
    }

}
