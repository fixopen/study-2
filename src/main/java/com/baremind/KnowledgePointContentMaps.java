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
                int order = knowledgePointContentMapses.getOrder();
                if (order != 0) {
                    existproblem.setOrder(order);
                }

                Long knowledgePointId = knowledgePointContentMapses.getKnowledgePointId();
                if (knowledgePointId != null) {
                    existproblem.setKnowledgePointId(knowledgePointId);
                }

                Long volumeId = knowledgePointContentMapses.getVolumeId();
                if (volumeId != null) {
                    existproblem.setVolumeId(volumeId);
                }
                Long grade = knowledgePointContentMapses.getGrade();
                if (grade != null) {
                    existproblem.setGrade(grade);
                }
                Long subjectId = knowledgePointContentMapses.getSubjectId();
                if (subjectId != null) {
                    existproblem.setSubjectId(subjectId);
                }

                Long objectId = knowledgePointContentMapses.getObjectId();
                if (objectId != null) {
                    existproblem.setObjectId(objectId);
                }

                String type = knowledgePointContentMapses.getType();
                if (type != null) {
                    existproblem.setType(type);
                }

                JPAEntry.genericPut(existproblem);
                result = Response.ok(existproblem).build();
            }
        }
        return result;
    }

    @DELETE
    @Path("{id}")
    public Response deleteKnowledgePoint(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            long count = JPAEntry.genericDelete(KnowledgePointContentMap.class, "id", id);
            if (count > 0) {
                result = Response.ok(200).build();
            }
        }
        return result;
    }

}
