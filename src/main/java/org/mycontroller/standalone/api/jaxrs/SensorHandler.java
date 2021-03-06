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
package org.mycontroller.standalone.api.jaxrs;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.HashMap;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.mycontroller.standalone.MYCMessages.MESSAGE_TYPE_PRESENTATION;
import org.mycontroller.standalone.MYCMessages.MESSAGE_TYPE_SET_REQ;
import org.mycontroller.standalone.NumericUtils;
import org.mycontroller.standalone.ObjectFactory;
import org.mycontroller.standalone.api.jaxrs.exception.mappers.VariableStatusModel;
import org.mycontroller.standalone.api.jaxrs.mapper.KeyValueJson;
import org.mycontroller.standalone.api.jaxrs.mapper.Query;
import org.mycontroller.standalone.api.jaxrs.mapper.QueryResponse;
import org.mycontroller.standalone.api.jaxrs.mapper.PayloadJson;
import org.mycontroller.standalone.api.jaxrs.utils.RestUtils;
import org.mycontroller.standalone.db.DaoUtils;
import org.mycontroller.standalone.db.DeleteResourceUtils;
import org.mycontroller.standalone.db.SensorUtils;
import org.mycontroller.standalone.db.tables.Node;
import org.mycontroller.standalone.db.tables.Sensor;
import org.mycontroller.standalone.db.tables.SensorVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeeva Kandasamy (jkandasa)
 * @since 0.0.1
 */

@Path("/rest/sensors")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RolesAllowed({ "user" })
public class SensorHandler {
    private static final Logger _logger = LoggerFactory.getLogger(SensorHandler.class);

    @GET
    @Path("/")
    public Response getAllSensors(
            @QueryParam(Sensor.KEY_NODE_ID) Integer nodeId,
            @QueryParam(Sensor.KEY_NODE_NAME) String nodeName,
            @QueryParam(Sensor.KEY_TYPE) String type,
            @QueryParam(Sensor.KEY_SENSOR_ID) Integer sensorId,
            @QueryParam(Sensor.KEY_NAME) List<String> name,
            @QueryParam(Query.PAGE_LIMIT) Long pageLimit,
            @QueryParam(Query.PAGE) Long page,
            @QueryParam(Query.ORDER_BY) String orderBy,
            @QueryParam(Query.ORDER) String order) {

        HashMap<String, Object> filters = new HashMap<String, Object>();

        filters.put(Sensor.KEY_NODE_ID, nodeId);
        filters.put(Sensor.KEY_TYPE, MESSAGE_TYPE_PRESENTATION.fromString(type));
        filters.put(Sensor.KEY_SENSOR_ID, sensorId);
        filters.put(Sensor.KEY_NAME, name);

        QueryResponse queryResponse = DaoUtils.getSensorDao().getAll(
                Query.builder()
                        .order(order != null ? order : Query.ORDER_ASC)
                        .orderBy(orderBy != null ? orderBy : Sensor.KEY_ID)
                        .filters(filters)
                        .pageLimit(pageLimit != null ? pageLimit : Query.MAX_ITEMS_PER_PAGE)
                        .page(page != null ? page : 1l)
                        .build());
        return RestUtils.getResponse(Status.OK, queryResponse);
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Integer id) {
        Sensor sensor = DaoUtils.getSensorDao().getById(id);
        return RestUtils.getResponse(Status.OK, sensor);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Integer id) {
        DeleteResourceUtils.deleteSensor(id);
        return RestUtils.getResponse(Status.NO_CONTENT);
    }

    @POST
    @Path("/deleteIds")
    public Response deleteIds(List<Integer> ids) {
        DeleteResourceUtils.deleteSensors(ids);
        return RestUtils.getResponse(Status.NO_CONTENT);
    }

    @PUT
    @Path("/")
    public Response update(Sensor sensor) {
        //Add Update sensor will be handled by Network type interface
        Node node = DaoUtils.getNodeDao().get(sensor.getNode().getId());
        ObjectFactory.getIActionEngine(node.getGateway().getNetworkType()).updateSensor(sensor);
        // Update Variable Types
        SensorUtils.updateSensorVariables(sensor);
        return RestUtils.getResponse(Status.NO_CONTENT);

    }

    @POST
    @Path("/")
    public Response add(Sensor sensor) {
        List<String> variableTypes = sensor.getVariableTypes();
        //Add Update sensor will be handled by Network type interface
        Node node = DaoUtils.getNodeDao().get(sensor.getNode().getId());
        ObjectFactory.getIActionEngine(node.getGateway().getNetworkType()).addSensor(sensor);

        sensor = DaoUtils.getSensorDao().get(sensor.getNode().getId(), sensor.getSensorId());
        for (String variableType : variableTypes) {
            DaoUtils.getSensorVariableDao()
                    .create(SensorVariable.builder().sensor(sensor)
                            .variableType(MESSAGE_TYPE_SET_REQ.fromString(variableType)).build()
                            .updateUnitAndMetricType());
        }
        return RestUtils.getResponse(Status.CREATED);

    }

    @PUT
    @Path("/updateVariable")
    public Response sendpayload(VariableStatusModel variableStatusModel) {
        SensorVariable sensorVariable = DaoUtils.getSensorVariableDao().get(variableStatusModel.getId());
        if (sensorVariable != null) {
            sensorVariable.setValue(String.valueOf(variableStatusModel.getValue()));
            ObjectFactory.getIActionEngine(sensorVariable.getSensor().getNode().getGateway().getNetworkType())
                    .sendPayload(sensorVariable);

        } else {
            return RestUtils.getResponse(Status.BAD_REQUEST);
        }
        return RestUtils.getResponse(Status.OK);
    }

    //TODO: review

    @PUT
    @Path("/updateOthers/{sensorRefId}")
    public Response update(@PathParam("sensorRefId") int sensorRefId, List<KeyValueJson> keyValues) {
        SensorUtils.updateOthers(sensorRefId, keyValues);
        return RestUtils.getResponse(Status.NO_CONTENT);
    }

    @GET
    @Path("/getOthers/{sensorRefId}")
    public Response get(@PathParam("sensorRefId") int sensorRefId) {
        return RestUtils.getResponse(Status.OK, SensorUtils.getOthers(sensorRefId));
    }

    @POST
    @Path("/sendPayload")
    public Response sendPayload(PayloadJson payload) {
        _logger.debug("PayloadJson:{}", payload);
        Sensor sensor = null;

        if (payload.getButtonType() != null) {
            sensor = DaoUtils.getSensorDao().getById(payload.getSensorRefId());
            switch (PayloadJson.BUTTON_TYPE.valueOf(payload.getButtonType().toUpperCase())) {
                case ON_OFF:
                    payload.setVariableType(MESSAGE_TYPE_SET_REQ.V_STATUS);
                    break;
                case LOCK_UNLOCK:
                    payload.setVariableType(MESSAGE_TYPE_SET_REQ.V_LOCK_STATUS);
                    break;
                case ARMED:
                    payload.setVariableType(MESSAGE_TYPE_SET_REQ.V_ARMED);
                    break;
                case TRIPPED:
                    payload.setVariableType(MESSAGE_TYPE_SET_REQ.V_TRIPPED);
                    break;
                case INCREASE:
                    SensorVariable sensorVariable = DaoUtils.getSensorVariableDao().get(sensor.getId(),
                            payload.getVariableType());
                    if (sensorVariable != null && sensorVariable.getValue() != null) {
                        payload.setPayload(String.valueOf(NumericUtils.getDouble(sensorVariable.getValue()) + 1));
                    } else {
                        payload.setPayload("0");
                    }
                    break;
                case DECREASE:
                    sensorVariable = DaoUtils.getSensorVariableDao().get(sensor.getId(), payload.getVariableType());
                    if (sensorVariable != null && sensorVariable.getValue() != null) {
                        payload.setPayload(String.valueOf(NumericUtils.getDouble(sensorVariable.getValue()) - 1));
                    } else {
                        payload.setPayload("0");
                    }
                    break;
                case UP:
                    payload.setVariableType(MESSAGE_TYPE_SET_REQ.V_UP);
                    break;
                case DOWN:
                    payload.setVariableType(MESSAGE_TYPE_SET_REQ.V_DOWN);
                    break;
                case STOP:
                    payload.setVariableType(MESSAGE_TYPE_SET_REQ.V_STOP);
                    break;
                case RGB:
                    payload.setVariableType(MESSAGE_TYPE_SET_REQ.V_RGB);
                    payload.setPayload(payload.getPayload().replace("#", ""));
                    break;
                case RGBW:
                    payload.setVariableType(MESSAGE_TYPE_SET_REQ.V_RGBW);
                    payload.setPayload(SensorUtils.getHexFromRgba(payload.getPayload()));
                    break;
                default:
                    break;
            }
        } else {
            sensor = DaoUtils.getSensorDao().get(payload.getNodeId(), payload.getSensorId());
        }
        ObjectFactory.getIActionEngine(sensor.getNode().getGateway().getNetworkType()).sendPayload(sensor, payload);
        return RestUtils.getResponse(Status.OK);
    }

    @GET
    @Path("sensorVariable/{id}")
    public Response getSensorValue(@PathParam("id") int id) {
        return RestUtils.getResponse(Status.OK, DaoUtils.getSensorVariableDao().get(id));
    }
}
