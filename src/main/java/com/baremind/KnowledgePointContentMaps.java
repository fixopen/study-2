package com.baremind;

import com.baremind.data.KnowledgePointContentMap;
import com.baremind.utils.Impl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by User on 2016/9/18.
 */
@Path("knowledge-point-content-maps")
public class KnowledgePointContentMaps {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        return Impl.get(sessionId, filter, null, KnowledgePointContentMap.class, null, null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, KnowledgePointContentMap.class, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, KnowledgePointContentMap entity) {
        return Impl.create(sessionId, entity, null, null);
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, KnowledgePointContentMap newData) {
        return Impl.updateById(sessionId, id, newData, KnowledgePointContentMap.class, (exist, knowledgePointContentMap) -> {
            Long knowledgePointId = knowledgePointContentMap.getKnowledgePointId();
            if (knowledgePointId != null) {
                exist.setKnowledgePointId(knowledgePointId);
            }

            String type = knowledgePointContentMap.getObjectType();
            if (type != null) {
                exist.setObjectType(type);
            }

            Long objectId = knowledgePointContentMap.getObjectId();
            if (objectId != null) {
                exist.setObjectId(objectId);
            }

            int order = knowledgePointContentMap.getOrder();
            if (order != 0) {
                exist.setOrder(order);
            }
        }, null);
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, KnowledgePointContentMap.class);
    }
}
