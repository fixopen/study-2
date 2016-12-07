package com.baremind;

import com.baremind.data.KnowledgePoint;
import com.baremind.data.Volume;
import com.baremind.utils.*;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("volumes")
public class Volumes {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Map<String, String> orders = new HashMap<>();
        orders.put("order", "ASC");
        final Date now = new Date();
        final Date yesterday = Date.from(now.toInstant().plusSeconds(-24 * 3600));
        return Impl.get(sessionId, filter, orders, Volume.class, (volume) -> Volume.convertToMap(volume, now, yesterday), null);
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, Volume.class, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, Volume entity) {
        return Impl.create(sessionId, entity, null);
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Volume newData) {
        return Impl.updateById(sessionId, id, newData, Volume.class, (exist, volume) -> {
            String title = volume.getName();
            if (title != null) {
                exist.setName(title);
            }
            int grade = volume.getGrade();
            if (grade != 0) {
                exist.setGrade(grade);
            }
            Long subjectId = volume.getSubjectId();
            if (subjectId != null) {
                exist.setSubjectId(subjectId);
            }
            Long coverId = volume.getCoverId();
            if (coverId != null) {
                exist.setCoverId(coverId);
            }
        }, null);
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, Volume.class);
    }

    @GET
    @Path("{id}/knowledge-points")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKnowledgePointsByVolumeId(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("volumeId", id);
            Condition ltNow = new Condition("<", new Date());
            conditions.put("showTime", ltNow);
            Map<String, String> orders = new HashMap<>();
            orders.put("order", "ASC");
            List<KnowledgePoint> knowledgePoints = JPAEntry.getList(KnowledgePoint.class, conditions, orders);
            if (!knowledgePoints.isEmpty()) {
                //  List<Map<String, Object>> kpsm = KnowledgePoints.toMaps(knowledgePoints);
                //SELECT count(l), object_id FROM likes WHERE object_id IN (...) GROUP BY object_id
                result = Response.ok(new Gson().toJson(knowledgePoints)).build();
            }
        }
        return result;
    }
}
