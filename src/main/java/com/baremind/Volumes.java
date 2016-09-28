package com.baremind;

import com.baremind.data.KnowledgePoint;
import com.baremind.data.Volume;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("volumes")
public class Volumes {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createVolume(@CookieParam("sessionId") String sessionId, /*byte[] volumeInfo*/ Volume volume) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            volume.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(volume);
            result = Response.ok(volume).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVolumes(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            Map<String, String> orders = new HashMap<>();
            orders.put("order", "ASC");
            List<Volume> volumes = JPAEntry.getList(Volume.class, filterObject, orders);
            if (!volumes.isEmpty()) {
                result = Response.ok(new Gson().toJson(volumes)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVolumeById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Volume volume = JPAEntry.getObject(Volume.class, "id", id);
            if (volume != null) {
                result = Response.ok(new Gson().toJson(volume)).build();
            }
        }
        return result;
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
            Map<String, String> orders = new HashMap<>();
            orders.put("order", "ASC");
            List<KnowledgePoint> knowledgePoints = JPAEntry.getList(KnowledgePoint.class, conditions, orders);

            if (!knowledgePoints.isEmpty()) {
                List<Map<String, Object>> kpsm = new ArrayList<>(knowledgePoints.size());
                for (KnowledgePoint kp : knowledgePoints) {

                    EntityManager em = JPAEntry.getEntityManager();
                    String statsLikes = "SELECT COUNT(l) FROM Log l WHERE l.action = 'like' and l.objectType = 'knowledge-point' AND l.objectId = " + kp.getId().toString();
                    Query lq = em.createQuery(statsLikes, Long.class);
                    Long likeCount = (Long) lq.getSingleResult();

                    String statsReads = "SELECT COUNT(l) FROM Log l WHERE l.action = 'read' and l.objectType = 'knowledge-point' AND l.objectId = " + kp.getId().toString();
                    Query rq = em.createQuery(statsReads, Long.class);
                    Long readCount = (Long) rq.getSingleResult();

                    Map<String, Object> kpm = new HashMap<>();
                    kpm.put("id", kp.getId());
                    kpm.put("volumeId", kp.getVolumeId());
                    kpm.put("name", kp.getTitle());
                    kpm.put("likeCount", likeCount);
                    kpm.put("readCount", readCount);
                    kpsm.add(kpm);
                }
                //SELECT count(l), object_id FROM likes WHERE object_id IN (...) GROUP BY object_id
                result = Response.ok(new Gson().toJson(kpsm)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateVolume(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Volume volume) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Volume existvolume = JPAEntry.getObject(Volume.class, "id", id);
            if (existvolume != null) {
                String title = volume.getTitle();
                if (title != null) {
                    existvolume.setTitle(title);
                }
                int grade = volume.getGrade();
                if (grade != 0) {
                    existvolume.setGrade(grade);
                }
                Long subjectId = volume.getSubjectId();
                if (sessionId != null) {
                    existvolume.setSubjectId(subjectId);
                }
                JPAEntry.genericPut(existvolume);
                result = Response.ok(existvolume).build();
            }
        }
        return result;
    }
}
