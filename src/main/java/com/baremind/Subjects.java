package com.baremind;

import com.baremind.data.Log;
import com.baremind.data.Subject;
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
import java.util.*;

@Path("subjects")
public class Subjects {
    @POST //添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSubject(@CookieParam("userId") String userId, Subject subject) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            subject.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(subject);
            result = Response.ok(subject).build();
        }
        return result;
    }

    @POST
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subjectPopup(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Log log = Logs.insert(Long.parseLong(userId), "subject", id, "popup");
            result = Response.ok(new Gson().toJson(log)).build();
        }
        return result;
    }

    @GET
    @Path("{id}/popup")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPopup(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Long popCount = Logs.getUserStatsCount(Long.parseLong(userId), "subject", id, "popup");
            Boolean r = false;
            if (popCount > 0) {
                r = true;
            }
            result = Response.ok("{\"popup\":" + r + "}").build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubjects(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<Subject> subjects = JPAEntry.getList(Subject.class, filterObject);
            if (!subjects.isEmpty()) {
                result = Response.ok(new Gson().toJson(subjects)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubjectById(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Subject subject = JPAEntry.getObject(Subject.class, "id", id);
            if (subject != null) {
                result = Response.ok(new Gson().toJson(subject)).build();
            }
        }
        return result;
    }

    @GET
    @Path("{id}/low/volumes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLowVolumesBySubjectId(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("subjectId", id);
            conditions.put("grade", 20);
            Map<String, String> orders = new HashMap<>();
            orders.put("order", "ASC");
            List<Volume> volumes = JPAEntry.getList(Volume.class, conditions, orders);
            if (!volumes.isEmpty()) {
                List<Map<String, Object>> r = new ArrayList<>();
                Date now = new Date();
                Date yesterday = Date.from(now.toInstant().plusSeconds(-24 * 3600));
                for (Volume volume : volumes) {
                    Map<String, Object> vm = new HashMap<>();
                    vm.put("id", volume.getId());
                    vm.put("grade", volume.getGrade());
                    vm.put("order", volume.getOrder());
                    vm.put("subjectId", volume.getSubjectId());
                    vm.put("title", volume.getTitle());
                    vm.put("type", "old");
                    EntityManager em = JPAEntry.getEntityManager();
                    String stats = "SELECT COUNT(l) FROM KnowledgePoint l WHERE l.volumeId = :volumeId AND l.showTime > :yesterday AND l.showTime < :now";
                    Query q = em.createQuery(stats, Long.class);
                    q.setParameter("volumeId", volume.getId());
                    q.setParameter("yesterday", yesterday);
                    q.setParameter("now", now);
                    Long count = (Long) q.getSingleResult();
                    if (count > 0) {
                        vm.put("type", "new");
                    }
                    r.add(vm);
                }
                result = Response.ok(new Gson().toJson(r)).build();
            }
        }
        return result;
    }

    @GET
    @Path("{id}/high/volumes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHighVolumesBySubjectId(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("subjectId", id);
            conditions.put("grade", 21);
            Map<String, String> orders = new HashMap<>();
            orders.put("order", "ASC");
            List<Volume> volumes = JPAEntry.getList(Volume.class, conditions, orders);
            if (!volumes.isEmpty()) {
                result = Response.ok(new Gson().toJson(volumes)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSubject(@CookieParam("userId") String userId, @PathParam("id") Long id, Subject subject) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Subject existsubject = JPAEntry.getObject(Subject.class, "id", id);
            if (existsubject != null) {
                String name = subject.getName();
                if (name != null) {
                    existsubject.setName(name);
                }
                JPAEntry.genericPut(existsubject);
                result = Response.ok(existsubject).build();
            }
        }
        return result;
    }
}
