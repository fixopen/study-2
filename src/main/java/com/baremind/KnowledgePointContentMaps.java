package com.baremind;

import com.baremind.data.KnowledgePointContentMap;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 2016/9/18.
 */
@Path("knowledge-point-content-maps")
public class KnowledgePointContentMaps {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProblem(@CookieParam("sessionId") String sessionId, KnowledgePointContentMap knowledgePointContentMap) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            knowledgePointContentMap.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(knowledgePointContentMap);
            result = Response.ok(new Gson().toJson(knowledgePointContentMap)).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProblems(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<KnowledgePointContentMap> knowledgePointContentMapse = JPAEntry.getList(KnowledgePointContentMap.class, filterObject);
            if (!knowledgePointContentMapse.isEmpty()) {
                result = Response.ok(new Gson().toJson(knowledgePointContentMapse)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProblemById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            KnowledgePointContentMap knowledgePointContentMapse = JPAEntry.getObject(KnowledgePointContentMap.class, "id", id);
            if (knowledgePointContentMapse != null) {
                result = Response.ok(new Gson().toJson(knowledgePointContentMapse)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProblem(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, KnowledgePointContentMap knowledgePointContentMapses) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            KnowledgePointContentMap existproblem = JPAEntry.getObject(KnowledgePointContentMap.class, "id", id);
            if (existproblem != null) {
                Long knowledgePointId = knowledgePointContentMapses.getKnowledgePointId();
                if (knowledgePointId != null) {
                    existproblem.setKnowledgePointId(knowledgePointId);
                }

                String type = knowledgePointContentMapses.getObjectType();
                if (type != null) {
                    existproblem.setObjectType(type);
                }

                Long objectId = knowledgePointContentMapses.getObjectId();
                if (objectId != null) {
                    existproblem.setObjectId(objectId);
                }

                int order = knowledgePointContentMapses.getOrder();
                if (order != 0) {
                    existproblem.setOrder(order);
                }

                JPAEntry.genericPut(existproblem);
                result = Response.ok(existproblem).build();
            }
        }
        return result;
    }

    @DELETE
    @Path("{id}")
    public Response deleteLike(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            KnowledgePointContentMap knowledgePointContentMapse = JPAEntry.getObject(KnowledgePointContentMap.class, "id", id);
            /*KnowledgePointContentMap po = JPAEntry.getObject(KnowledgePointContentMap.class, "object_id", object_id);*/
            if (knowledgePointContentMapse != null) {
                JPAEntry.genericDelete(knowledgePointContentMapse);
                result = Response.ok(200).build();
            }
        }
        return result;
    }

}
