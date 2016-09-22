package com.baremind;

import com.baremind.data.*;
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
import java.util.function.Predicate;

@Path("knowledgePoints")
public class KnowledgePoints {
    private <T> T findItem(List<T> container, Predicate<T> p) {
        T result = null;
        for (T textItem : container) {
            if (p.test(textItem)) {
                result = textItem;
                break;
            }
        }
        return result;
    }

    private <T> List<T> findItems(List<T> container, Predicate<T> p) {
        List<T> result = new ArrayList<>();
        for (T textItem : container) {
            if (p.test(textItem)) {
                result.add(textItem);
            }
        }
        return result;
    }

    private String join(List<String> ids) {
        String result = "";
        boolean isFirst = true;
        for (String id : ids) {
            if (!isFirst) {
                result += ", ";
            }
            result += id;
            isFirst = false;
        }
        return result;
    }

    @GET
    @Path("{id}/contents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKnowledgePointsByVolumeId(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            KnowledgePoint p = JPAEntry.getObject(KnowledgePoint.class, "id", id);
            if (p != null) {
                Map<String, Object> conditions = new HashMap<>();
                conditions.put("KnowledgePointId", id);

                Map<String, String> orders = new HashMap<>();
                orders.put("\"order\"", "ASC");
                List<KnowledgePointContentMap> maps = JPAEntry.getList(KnowledgePointContentMap.class, conditions, orders);

                List<String> textIds = new ArrayList<>();
                List<String> imageIds = new ArrayList<>();
                List<String> videoIds = new ArrayList<>();
                List<String> problemIds = new ArrayList<>();
                List<String> imageTextIds = new ArrayList<>();
                List<String> quoteIds = new ArrayList<>();


                for (KnowledgePointContentMap item : maps) {
                    switch (item.getType()) {
                        case "text":
                            textIds.add(item.getObjectId().toString());
                            break;
                        case "image":
                            imageIds.add(item.getObjectId().toString());
                            break;
                        case "video":
                            videoIds.add(item.getObjectId().toString());
                            break;
                        case "problem":
                            problemIds.add(item.getObjectId().toString());
                            break;
                        case "imageText":
                            imageTextIds.add(item.getObjectId().toString());
                            break;
                        case "quote":
                            quoteIds.add(item.getObjectId().toString());
                            break;
                    }
                }

                EntityManager em = JPAEntry.getEntityManager();
                List<Text> textObjects = null;
                if (!textIds.isEmpty()) {
                    String textquery = "SELECT id, content FROM texts WHERE id IN ( " + join(textIds) + " )";
                    Query tq = em.createNativeQuery(textquery, Text.class);
                    textObjects = tq.getResultList();
                }
                List<Image> imageObjects = null;
                if (!imageIds.isEmpty()) {
                    String imagequery = "SELECT * FROM images WHERE id IN ( " + join(imageIds) + " )";
                    Query iq = em.createNativeQuery(imagequery, Image.class);
                    imageObjects = iq.getResultList();
                }
                List<Video> videoObjects = null;
                if (!videoIds.isEmpty()) {
                    String videoquery = "SELECT * FROM videos WHERE id IN ( " + join(videoIds) + " )";
                    Query vq = em.createNativeQuery(videoquery, Video.class);
                    videoObjects = vq.getResultList();
                }
                List<Problem> problemObjects = null;
                List<ProblemOption> problemoptionObjects = null;
                List<ProblemStandardAnswer> problemstandardanswersObjects = null;
                if (!problemIds.isEmpty()) {
                    String problemquery = "SELECT * FROM problems WHERE id IN ( " + join(problemIds) + " )";
                    Query pq = em.createNativeQuery(problemquery, Problem.class);
                    problemObjects = pq.getResultList();

                    String problemoptionsquery = "SELECT * FROM problem_options WHERE problem_id IN ( " + join(problemIds) + " )";
                    Query pqoption = em.createNativeQuery(problemoptionsquery, ProblemOption.class);
                    problemoptionObjects = pqoption.getResultList();

                    String problemsstandardanswersquery = "SELECT * FROM problem_standard_answers WHERE problem_id IN ( " + join(problemIds) + " )";
                    Query pqsan = em.createNativeQuery(problemsstandardanswersquery, ProblemStandardAnswer.class);
                    problemstandardanswersObjects = pqsan.getResultList();
                }
                List<ImageText> imageTextObject = null;
                if (!imageTextIds.isEmpty()) {
                    String imageTextquery = "SELECT * FROM image_texts WHERE id IN ( " + join(imageTextIds) + " )";
                    Query itq = em.createNativeQuery(imageTextquery, ImageText.class);
                    imageTextObject = itq.getResultList();
                }
                List<Quote> quoteObject = null;
                if (!quoteIds.isEmpty()) {
                    String quotequery = "SELECT * FROM quotes WHERE id IN ( " + join(quoteIds) + " )";
                    Query qq = em.createNativeQuery(quotequery, Quote.class);
                    quoteObject = qq.getResultList();
                }

                List<Object> r = new ArrayList<>();
                List<Object> problemr3 = new ArrayList<>();
                List<Object> quoter4 = new ArrayList<>();
                for (final KnowledgePointContentMap item : maps) {
                    switch (item.getType()) {
                        case "text":
                            if (textObjects != null) {
                                Text t = findItem(textObjects, (Text text) -> text.getId().longValue() == item.getObjectId().longValue());
                                Map<String, Object> tm = new HashMap<>();
                                tm.put("id", t.getId());
                                tm.put("content", t.getContent());
                                tm.put("type", "text");
                                r.add(tm);
                            }
                            break;
                        case "image":
                            if (imageObjects != null) {
                                Image im = findItem(imageObjects, (image) -> image.getId().longValue() == item.getObjectId().longValue());
                                Map<String, Object> itm = new HashMap<>();
                                itm.put("id", im.getId());
                                itm.put("type", "image");
                                itm.put("description", "");
                                itm.put("href", im.getStorePath());
                                r.add(itm);
                            }
                            break;

                        case "imageText":
                            if (imageTextObject != null) {
                                ImageText ITe = findItem(imageTextObject, (imageText) -> imageText.getId().longValue() == item.getObjectId().longValue());
                                Map<String, Object> items = new HashMap<>();
                                items.put("id", ITe.getId());
                                items.put("type", "imageText");
                                items.put("content", ITe.getContent());
                                items.put("href", ITe.getStorePath());
                                r.add(items);
                            }
                            break;
                        case "problem":
                            if (problemObjects != null || problemoptionObjects != null || problemstandardanswersObjects != null) {
                                Problem pie = findItem(problemObjects, (problem) -> problem.getId().longValue() == item.getObjectId().longValue());
                                List<ProblemOption> pieo = findItems(problemoptionObjects, (ProblemOption problemoption) -> problemoption.getProblemId().longValue() == item.getObjectId().longValue());
                                List<ProblemStandardAnswer> dfs = findItems(problemstandardanswersObjects, (problemstandardanswers) -> problemstandardanswers.getProblemId().longValue() == item.getObjectId().longValue());

                                Map<String, Object> piems = new HashMap<>();
                                piems.put("id", pie.getId());
                                if (dfs.size() > 1) {
                                    piems.put("type", "多选题");
                                } else {
                                    piems.put("type", "单选题");
                                }
                                piems.put("options", pieo);
                                piems.put("title", pie.getTitle());
                                problemr3.add(piems);
                                break;
                            }
                        case "quote":
                            if (quoteObject != null) {
                                Quote ique = findItem(quoteObject, (quote) -> quote.getId().longValue() == item.getObjectId().longValue());
                                Map<String, Object> iquems = new HashMap<>();
                                iquems.put("id", ique.getId());
                                iquems.put("content", ique.getContent());
                                iquems.put("source", ique.getSource());
                                quoter4.add(iquems);
                            }
                            break;

                    }
                }
                Map<String, Object> r2 = new HashMap<>();
                r2.put("title", p.getTitle());
                r2.put("quotes", quoter4);
                r2.put("contents", r);
                if (!videoObjects.isEmpty()) {
                    r2.put("video", videoObjects.get(0));
                }
                //===============================================================
                Map<String, Object> interaction = new HashMap<>();
                int readCount = 0;
                interaction.put("readCount", readCount);
                int likeCount = 0;
                /*String statsLikes = "SELECT COUNT(*) AS count FROM likes WHERE object_type = 'knowledge-point' AND object_id = " + id.toString();
                Query lq = em.createNativeQuery(statsLikes, Video.class);
                List likeCountList = lq.getResultList(); //->Object[]->count*/
                interaction.put("likeCount", likeCount);
                r2.put("interaction", interaction);
                r2.put("problems", problemr3);
                //em.getTransaction().commit();
                //==================================================
                conditions = new HashMap<>();
                conditions.put("objectType", "knowledge-point");
                conditions.put("objectId", id);
                //评论
                List<Comment> comments = JPAEntry.getList(Comment.class, conditions);
                r2.put("comments", comments);
                //==================================================
                if (!r.isEmpty()) {
                    String v = new Gson().toJson(r2);
                    result = Response.ok(v, "application/json; charset=utf-8").build();
                }
            }
        }

        return result;
    }


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

