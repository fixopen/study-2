package com.baremind;

import com.baremind.data.*;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("knowledge-points")
public class KnowledgePoints {
    @POST//添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createKnowledgePoint(@CookieParam("sessionId") String sessionId, KnowledgePoint knowledgePoint) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            knowledgePoint.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(knowledgePoint);
            result = Response.ok(knowledgePoint).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKnowledgePoints(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<KnowledgePoint> knowledgePoints = JPAEntry.getList(KnowledgePoint.class, filterObject);
            if (!knowledgePoints.isEmpty()) {
                result = Response.ok(new Gson().toJson(knowledgePoints)).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKnowledgePointById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            KnowledgePoint knowledgePoint = JPAEntry.getObject(KnowledgePoint.class, "id", id);
            if (knowledgePoint != null) {
                result = Response.ok(new Gson().toJson(knowledgePoint)).build();
            }
        }
        return result;
    }

    private int getOrder(List<KnowledgePointContentMap> maps, long id) {
        int order = 0;
        for (int j = 0; j < maps.size(); ++j) {
            if (maps[j].getObjectId() == id) {
                order = j;
                break;
            }
        }
        return order;
    }

    @GET
    @Path("{id}/contents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKnowledgePointsByVolumeId(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("knowledgePointId", id);

            List<Text> texts = JPAEntry.getList(Text.class, conditions);
            List<Image> problemsimages = JPAEntry.getList(Image.class, conditions);

            Video video = JPAEntry.getObject(Video.class, "knowledgePointId", id);

            List<Problem> problems = JPAEntry.getList(Problem.class, conditions);

            List<Comment> comments = JPAEntry.getList(Comment.class, conditions);

            int count = texts.size() + images.size();

            List<Object> result = new ArrayList<>(count);

            List<KnowledgePointContentMap> maps = JPAEntry.getList(KnowledgePointContentMap.class, conditions);

            for (int i = 0; i < texts.size(); ++i) {
                int order = getOrder(maps, texts[i].getId());
                result.add(order, texts[i]);
            }
            for (int i = 0; i < images.size(); ++i) {
                int order = getOrder(maps, images[i].getId());
                result.add(order, images[i]);
            }
            result.add(video);
            for (int i = 0; i < problems.size(); ++i) {
                int order = getOrder(maps, problems[i].getId());
                result.add(order, problems[i]);
            }
            for (int i = 0; i < comments.size(); ++i) {
                int order = getOrder(maps, comments[i].getId());
                result.add(order, comments[i]);
            }

            if (!result.isEmpty()) {
                result = Response.ok(new Gson().toJson(result)).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateKnowledgePoint(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, KnowledgePoint knowledgePoint) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            KnowledgePoint existknowledgePoint = JPAEntry.getObject(KnowledgePoint.class, "id", id);
            if (existknowledgePoint != null) {
                int grade = knowledgePoint.getGrade();
                if (grade != 0) {
                    existknowledgePoint.getGrade();
                }
                String storePath = knowledgePoint.getStorePath();
                if (storePath != null) {
                    existknowledgePoint.setStorePath(storePath);
                }
                int order = knowledgePoint.getOrder();
                if (order != 0) {
                    existknowledgePoint.setOrder(order);
                }
                Long subjectId = knowledgePoint.getSubjectId();
                if (sessionId != null) {
                    existknowledgePoint.setSubjectId(subjectId);
                }
                String title = knowledgePoint.getTitle();
                if (title != null) {
                    existknowledgePoint.setTitle(title);
                }
                String videoUrl = knowledgePoint.getVideoUrl();
                if (videoUrl != null) {
                    existknowledgePoint.setVideoUrl(videoUrl);
                }
                Long volumeId = knowledgePoint.getVolumeId();
                if (volumeId != null) {
                    existknowledgePoint.setVolumeId(volumeId);
                }
                JPAEntry.genericPut(existknowledgePoint);
                result = Response.ok(existknowledgePoint).build();
            }
        }
        return result;
    }
}
