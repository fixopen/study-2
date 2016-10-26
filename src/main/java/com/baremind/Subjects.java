package com.baremind;

import com.baremind.data.Subject;
import com.baremind.data.Volume;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                result = Response.ok(new Gson().toJson(volumes)).build();
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
