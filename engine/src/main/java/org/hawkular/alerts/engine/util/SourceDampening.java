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

import static org.hawkular.alerts.api.util.Util.isEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hawkular.alerts.api.model.condition.ConditionEval;
import org.hawkular.alerts.api.model.dampening.Dampening;
import org.hawkular.alerts.api.model.dampening.Dampening.Type;
import org.hawkular.alerts.api.model.trigger.Match;
import org.hawkular.alerts.api.model.trigger.Mode;

/**
 * @author Jay Shaughnessy
 * @author Lucas Ponce
 */
public class SourceDampening {

    /** The tenant of the root Trigger */
    private String tenantId;

    /** The id of the root Trigger */
    private String triggerId;

    /** The data source being applied to the root trigger. */
    private String source;

    /** The root dampening mode */
    private Mode triggerMode;

    /** The root dampening type */
    private Type type;

    /** The root Dampening */
    private transient Dampening dampening;

    private transient int numTrueEvals;

    private transient int numEvals;

    private transient long trueEvalsStartTime;

    // This Map<conditionSetIndex,ConditionEval> holds the most recent eval for each member of the condition set
    private transient Map<Integer, ConditionEval> currentEvals = new HashMap<>(5);

    private transient boolean satisfied;

    private transient List<Set<ConditionEval>> satisfyingEvals = new ArrayList<Set<ConditionEval>>();

    public SourceDampening(String source, Dampening dampening) {
        super();
        this.tenantId = dampening.getTenantId();
        this.triggerId = dampening.getTriggerId();
        this.source = source;
        this.triggerMode = dampening.getTriggerMode();
        this.type = dampening.getType();
        this.dampening = dampening;

        reset();
    }

    public void reset() {
        this.numTrueEvals = 0;
        this.numEvals = 0;
        this.trueEvalsStartTime = 0L;
        this.satisfied = false;
        this.satisfyingEvals.clear();
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(String triggerId) {
        this.triggerId = triggerId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Mode getTriggerMode() {
        return triggerMode;
    }

    public void setTriggerMode(Mode triggerMode) {
        this.triggerMode = triggerMode;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Dampening getDampening() {
        return dampening;
    }

    public void setDampening(Dampening dampening) {
        this.dampening = dampening;
    }

    public void setSatisfied(boolean satisfied) {
        this.satisfied = satisfied;
    }

    public void setSatisfyingEvals(List<Set<ConditionEval>> satisfyingEvals) {
        this.satisfyingEvals = satisfyingEvals;
    }

    public int getNumTrueEvals() {
        return numTrueEvals;
    }

    public void setNumTrueEvals(int numTrueEvals) {
        this.numTrueEvals = numTrueEvals;
    }

    public long getTrueEvalsStartTime() {
        return trueEvalsStartTime;
    }

    public void setTrueEvalsStartTime(long trueEvalsStartTime) {
        this.trueEvalsStartTime = trueEvalsStartTime;
    }

    public int getNumEvals() {
        return numEvals;
    }

    public void setNumEvals(int numEvals) {
        this.numEvals = numEvals;
    }

    public Map<Integer, ConditionEval> getCurrentEvals() {
        return currentEvals;
    }

    public boolean isSatisfied() {
        return satisfied;
    }

    /**
     * @return a safe, but not deep, copy of the satisfying evals List
     */
    public List<Set<ConditionEval>> getSatisfyingEvals() {
        return new ArrayList<Set<ConditionEval>>(satisfyingEvals);
    }

    public void addSatisfyingEvals(Set<ConditionEval> satisfyingEvals) {
        this.satisfyingEvals.add(satisfyingEvals);
    }

    public void addSatisfyingEvals(ConditionEval... satisfyingEvals) {
        this.satisfyingEvals.add(new HashSet<ConditionEval>(Arrays.asList(satisfyingEvals)));
    }

    public void perform(Match match, Set<ConditionEval> conditionEvalSet) {
        if (null == match) {
            throw new IllegalArgumentException("Match can not be null");
        }
        if (null == conditionEvalSet || isEmpty(conditionEvalSet)) {
            throw new IllegalArgumentException("ConditionEval Set can not be null or empty");
        }

        // The currentEvals map holds the most recent eval for each condition in the condition set.
        conditionEvalSet.stream()
                .forEach(conditionEval -> currentEvals.put(conditionEval.getConditionSetIndex(), conditionEval));

        // The conditionEvals for the same trigger will all have the same condition set size, so just use the first
        int conditionSetSize = conditionEvalSet.iterator().next().getConditionSetSize();
        boolean trueEval = false;
        switch (match) {
            case ALL:
                // Don't perform a dampening eval until we have a conditionEval for each member of the ConditionSet.
                if (currentEvals.size() < conditionSetSize) {
                    return;
                }
                // Otherwise, all condition evals must be true for the condition set eval to be true
                trueEval = true;
                for (ConditionEval ce : currentEvals.values()) {
                    if (!ce.isMatch()) {
                        trueEval = false;
                        break;
                    }
                }
                break;
            case ANY:
                // we only need one true condition eval for the condition set eval to be true
                trueEval = false;
                for (ConditionEval ce : currentEvals.values()) {
                    if (ce.isMatch()) {
                        trueEval = true;
                        break;
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unexpected Match type: " + match.name());
        }

        // If we had previously started our time and now have exceeded our time limit then we must start over
        long now = System.currentTimeMillis();
        if (dampening.getType() == Type.RELAXED_TIME && trueEvalsStartTime != 0L) {
            if ((now - trueEvalsStartTime) > dampening.getEvalTimeSetting()) {
                reset();
            }
        }

        numEvals += 1;
        if (trueEval) {
            numTrueEvals += 1;
            addSatisfyingEvals(new HashSet<>(currentEvals.values()));

            switch (dampening.getType()) {
                case STRICT:
                case RELAXED_COUNT:
                    if (numTrueEvals == dampening.getEvalTrueSetting()) {
                        satisfied = true;
                    }
                    break;

                case RELAXED_TIME:
                    if (trueEvalsStartTime == 0L) {
                        trueEvalsStartTime = now;
                    }
                    if ((numTrueEvals == dampening.getEvalTrueSetting())
                            && ((now - trueEvalsStartTime) < dampening.getEvalTimeSetting())) {
                        satisfied = true;
                    }
                    break;
                case STRICT_TIME:
                case STRICT_TIMEOUT:
                    if (trueEvalsStartTime == 0L) {
                        trueEvalsStartTime = now;

                    } else if ((now - trueEvalsStartTime) >= dampening.getEvalTimeSetting()) {
                        satisfied = true;
                    }
                    break;
            }
        } else {
            switch (dampening.getType()) {
                case STRICT:
                case STRICT_TIME:
                case STRICT_TIMEOUT:
                    reset();
                    break;
                case RELAXED_COUNT:
                    int numNeeded = dampening.getEvalTrueSetting() - numTrueEvals;
                    int chancesLeft = dampening.getEvalTotalSetting() - numEvals;
                    if (numNeeded > chancesLeft) {
                        reset();
                    }
                    break;
                case RELAXED_TIME:
                    break;
            }
        }
    }

    public String log() {
        StringBuilder sb = new StringBuilder(
                "[" + triggerId + ", triggerMode=" + triggerMode.name() + ", source=" + source + ", numTrueEvals="
                        + numTrueEvals + ", numEvals=" + numEvals + ", trueEvalsStartTime=" + trueEvalsStartTime
                        + ", satisfied=" + satisfied);
        if (satisfied) {
            for (Set<ConditionEval> ces : satisfyingEvals) {
                sb.append("\n\t[");
                String space = "";
                for (ConditionEval ce : ces) {
                    sb.append(space);
                    sb.append("[");
                    sb.append(ce.getDisplayString());
                    sb.append("]");
                    space = " ";
                }
                sb.append("]");

            }
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
        result = prime * result + ((triggerId == null) ? 0 : triggerId.hashCode());
        result = prime * result + ((triggerMode == null) ? 0 : triggerMode.hashCode());
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
        SourceDampening other = (SourceDampening) obj;
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
        if (triggerId == null) {
            if (other.triggerId != null)
                return false;
        } else if (!triggerId.equals(other.triggerId))
            return false;
        if (triggerMode != other.triggerMode)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SourceDampening [tenantId=" + tenantId + ", triggerId=" + triggerId
                + ", source=" + source + ", triggerMode=" + triggerMode.name() + ", type=" + type.name()
                + ", numTrueEvals=" + numTrueEvals + ", numEvals=" + numEvals
                + ", trueEvalsStartTime=" + trueEvalsStartTime + ", currentEvals=" + currentEvals + ", satisfied="
                + satisfied + "]";
    }

}
