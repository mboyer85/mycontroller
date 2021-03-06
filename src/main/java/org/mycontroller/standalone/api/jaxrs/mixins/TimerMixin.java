/**
 * Copyright (C) 2015-2016 Jeeva Kandasamy (jkandasa@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mycontroller.standalone.api.jaxrs.mixins;

import org.mycontroller.standalone.AppProperties.RESOURCE_TYPE;

import org.mycontroller.standalone.api.jaxrs.mixins.deserializers.FrequencyTypeDeserializer;
import org.mycontroller.standalone.api.jaxrs.mixins.deserializers.ResourceTypeDeserializer;
import org.mycontroller.standalone.api.jaxrs.mixins.deserializers.TimerTypeDeserializer;
import org.mycontroller.standalone.api.jaxrs.mixins.serializers.FrequencyTypeSerializer;
import org.mycontroller.standalone.api.jaxrs.mixins.serializers.ResourceTypeSerializer;
import org.mycontroller.standalone.api.jaxrs.mixins.serializers.TimerTypeSerializer;
import org.mycontroller.standalone.timer.TimerUtils.FREQUENCY_TYPE;
import org.mycontroller.standalone.timer.TimerUtils.TIMER_TYPE;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.2
 */
@JsonTypeName("timer")
@JsonIgnoreProperties({ "internalVariable1" })
abstract class TimerMixin {

    @JsonSerialize(using = ResourceTypeSerializer.class)
    abstract public String getResourceType();

    @JsonSerialize(using = TimerTypeSerializer.class)
    abstract public String getTimerType();

    @JsonSerialize(using = FrequencyTypeSerializer.class)
    abstract public String getFrequencyType();

    @JsonDeserialize(using = ResourceTypeDeserializer.class)
    abstract public void setResourceType(RESOURCE_TYPE resourceType);

    @JsonDeserialize(using = TimerTypeDeserializer.class)
    abstract public void setTimerType(TIMER_TYPE timerType);

    @JsonDeserialize(using = FrequencyTypeDeserializer.class)
    abstract public void setFrequencyType(FREQUENCY_TYPE frequencyType);

}
