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

import org.mycontroller.standalone.api.jaxrs.mixins.serializers.SensorVariableInfoSerializer;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.2
 */
@JsonTypeName("forwardpayload")
abstract class ForwardPayloadMixin {

    @JsonGetter("source")
    @JsonSerialize(using = SensorVariableInfoSerializer.class)
    abstract public String getSource();

    @JsonGetter("destination")
    @JsonSerialize(using = SensorVariableInfoSerializer.class)
    abstract public String getDestination();

}
