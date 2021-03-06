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
package org.hawkular.alerts.api.model.trigger;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * A trigger definition.
 *
 * @author Jay Shaughnessy
 * @author Lucas Ponce
 */
public class Trigger extends TriggerTemplate {

    public enum Mode {
        FIRE, SAFETY
    };

    @JsonInclude
    private String id;

    @JsonInclude
    private boolean enabled;

    @JsonInclude
    private boolean safetyEnabled;

    @JsonIgnore
    private Mode mode;

    @JsonIgnore
    private transient Match match;

    public Trigger() {
        /*
            Default constructor is needed for JSON libraries in JAX-RS context.
         */
        this("defaultName");
    }

    public Trigger(String name) {
        this(generateId(), name);
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

    public Trigger(String id, String name) {
        super(name);

        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Trigger id must be non-empty");
        }
        this.id = id;

        this.enabled = false;
        this.safetyEnabled = false;
        this.mode = Mode.FIRE;
        this.match = getFiringMatch();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        setMatch(this.mode == Mode.FIRE ? getFiringMatch() : getSafetyMatch());
    }

    /**
     * This tells you whether the Trigger defines safety conditions and whether safety mode is enabled.
     * This does NOT return the current <code>mode</code> of the Trigger.
     * @return true if this Trigger supports safety mode and is it enabled.
     * @see {@link #getMode()} to see the current <code>mode</code>.
     */
    public boolean isSafetyEnabled() {
        return safetyEnabled;
    }

    /**
     * Set true if safety conditions and dampening are fully defined and should be activated on a Trigger firing. Set
     * false otherwise.
     * @param safetyEnabled
     */
    public void setSafetyEnabled(boolean safetyEnabled) {
        this.safetyEnabled = safetyEnabled;
    }

    @JsonIgnore
    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        Trigger other = (Trigger) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Trigger [id=" + id + ", " +
                "name=" + getName() + ", " +
                "description=" + getDescription() + ", " +
                "firingMatch=" + getFiringMatch() + ", " +
                "safetyMatch=" + getSafetyMatch() + ", " +
                "enabled=" + enabled + ", " +
                "mode=" + mode + ", " +
                "match=" + getMatch() + "]";
    }

}
