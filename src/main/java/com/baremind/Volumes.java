package com.baremind;

import com.baremind.data.KnowledgePoint;
import com.baremind.data.Volume;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.Condition;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("volumes")
public class Volumes {
    public static List<Map<String, Object>> convertVolumes(List<Volume> volumes) {
        List<Map<String, Object>> r = new ArrayList<>();
        Date now = new Date();
        Date yesterday = Date.from(now.toInstant().plusSeconds(-24 * 3600));
        for (Volume volume : volumes) {
            Map<String, Object> vm = Volume.convertToMap(volume, now, yesterday);
            r.add(vm);
        }
        return r;
    }

    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createVolume(@CookieParam("userId") String userId, /*byte[] volumeInfo*/ Volume volume) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            volume.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(volume);
            result = Response.ok(volume).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVolumes(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            Map<String, String> orders = new HashMap<>();
            orders.put("order", "ASC");
            List<Volume> volumes = JPAEntry.getList(Volume.class, filterObject, orders);
            if (!volumes.isEmpty()) {
                List<Map<String, Object>> r = Volumes.convertVolumes(volumes);
                result = Response.ok(new Gson().toJson(r)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVolumeById(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Volume volume = JPAEntry.getObject(Volume.class, "id", id);
            if (volume != null) {
                result = Response.ok(new Gson().toJson(volume)).build();
            }
        }
        return result;
    }

    public static class StatsInfo {
        private Long count;
        private String objectType;

        public StatsInfo(Long count, String objectType) {
            this.count = count;
            this.objectType = objectType;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }

        public String getObjectType() {
            return objectType;
        }

        public void setObjectType(String objectType) {
            this.objectType = objectType;
        }
    }

    @GET
    @Path("{id}/knowledge-points")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKnowledgePointsByVolumeId(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("volumeId", id);
            Condition ltNow = new Condition("<", new Date());
            conditions.put("showTime", ltNow);
            Map<String, String> orders = new HashMap<>();
            orders.put("order", "ASC");
            List<KnowledgePoint> knowledgePoints = JPAEntry.getList(KnowledgePoint.class, conditions, orders);
            if (!knowledgePoints.isEmpty()) {
                List<Map<String, Object>> kpsm = new ArrayList<>(knowledgePoints.size());
                Date now = new Date();
                Date yesterday = Date.from(now.toInstant().plusSeconds(-24 * 3600));
                for (KnowledgePoint kp : knowledgePoints) {
                    String statsContent = "SELECT count(m) FROM KnowledgePointContentMap m WHERE m.knowledgePointId = " + kp.getId().toString();
                    EntityManager em = JPAEntry.getEntityManager();
                    TypedQuery<Long> cq = em.createQuery(statsContent, Long.class);
                    List<Long> qc = cq.getResultList();
                    if (qc.size() == 1) {
                        Long c = qc.get(0);
                        if (c == 0) {
                            continue;
                        }
                    }
                    Map<String, Object> kpm = KnowledgePoint.convertToMap(kp, now, yesterday);
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
    public Response updateVolume(@CookieParam("userId") String userId, @PathParam("id") Long id, Volume volume) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Volume existvolume = JPAEntry.getObject(Volume.class, "id", id);
            if (existvolume != null) {
                String title = volume.getName();
                if (title != null) {
                    existvolume.setName(title);
                }
                int grade = volume.getGrade();
                if (grade != 0) {
                    existvolume.setGrade(grade);
                }
                Long subjectId = volume.getSubjectId();
                if (subjectId != null) {
                    existvolume.setSubjectId(subjectId);
                }
                JPAEntry.genericPut(existvolume);
                result = Response.ok(existvolume).build();
            }
        }
        return result;
    }
}
