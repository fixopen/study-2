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
        for (int j = 0; j < container.size(); ++j) {
            T textItem = container.get(j);
            if (p.test(textItem)) {
                result = textItem;
                break;
            }
        }
        return result;
    }

    private String join(List<String> ids) {
        String result = "";
        boolean isFirst = true;
        for (int i = 0; i < ids.size(); ++i) {
            if (!isFirst) {
                result += ", ";
            }
            result += ids.get(i);
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
                List<KnowledgePointContentMap> maps = JPAEntry.getList(KnowledgePointContentMap.class, conditions);


                List<String> textIds = new ArrayList<>();
                List<String> imageIds = new ArrayList<>();
                List<String> videoIds = new ArrayList<>();
                List<String> problemIds = new ArrayList<>();
                List<String> imageTextIds = new ArrayList<>();
                List<String> quoteIds = new ArrayList<>();


                for (int i = 0; i < maps.size(); ++i) {
                    KnowledgePointContentMap item = maps.get(i);
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
                //em.getTransaction().begin();

                String textquery = "SELECT id, content FROM texts WHERE id IN ( " + join(textIds) + " )";
                Query tq = em.createNativeQuery(textquery, Text.class);
                List<Text> textObjects = tq.getResultList();

                String imagequery = "SELECT * FROM images WHERE id IN ( " + join(imageIds) + " )";
                Query iq = em.createNativeQuery(imagequery, Image.class);
                List<Image> imageObjects = iq.getResultList();

                String videoquery = "SELECT * FROM videos WHERE id IN ( " + join(videoIds) + " )";
                Query vq = em.createNativeQuery(videoquery, Video.class);
                List<Video> videoObjects = vq.getResultList();

                String problemquery = "SELECT * FROM problems WHERE id IN ( " + join(problemIds) + " )";
                Query pq = em.createNativeQuery(problemquery, Problem.class);
                List<Problem> problemObjects = pq.getResultList();

                String imageTextquery = "SELECT * FROM image_texts WHERE id IN ( " + join(imageTextIds) + " )";
                Query itq = em.createNativeQuery(imageTextquery, ImageText.class);
                List<ImageText> imageTextObject = itq.getResultList();

                String quotequery = "SELECT * FROM quotes WHERE id IN ( " + join(quoteIds) + " )";
                Query qq = em.createNativeQuery(quotequery, Quote.class);
                List<Quote> quoteObject = qq.getResultList();

                List<Object> r = new ArrayList<Object>();
                for (int i = 0; i < maps.size(); ++i) {
                    final KnowledgePointContentMap item = maps.get(i);
                    switch (item.getType()) {
                        case "text":
                            Text t = findItem(textObjects, (Text text) -> text.getId().longValue() == item.getObjectId().longValue());
                            Map<String, Object> tm = new HashMap<>();
                            tm.put("id", t.getId());
                            tm.put("content", t.getContent());
                            tm.put("type", "text");
                            r.add(tm);
                            break;
                        case "image":
                            Image im = findItem(imageObjects, (image) -> image.getId().longValue() == item.getObjectId().longValue());
                            Map<String, Object> itm = new HashMap<>();
                            itm.put("id", im.getId());
                            itm.put("description", "");
                            itm.put("href", im.getStorePath());
                            r.add(itm);

                            break;
//
//                        {"comments":[],
//                         "contents":[{"id":96623475621888,"type":"text","content":"闃挎柉钂傝姮"},
//                                    {"id":96623476277248,"type":"text","content":"鏆楀閫㈢伅"},
//                         {"description":"","id":96623476146176,"href":"d:/1474357241063.png"}],
//                         "interaction":{"likeCount":0,"readCount":0},
//                         "video":{"id":96623476736000,"storePath":"鍙戝灏�"},
//                         "title":"璇枃浣庡勾绾х浜屽唽鐭ヨ瘑鐐逛竴",
//                         "quotes":[{"id":96623478964224,"content":"闃挎柉钂傝姮","source":"澶ф硶甯�"}],
//                         "problems":[{"id":96623477850112,"subjectId":1,"volumeId":1,"knowledgePointId":5,"title":"鍙戝灏�","videoUrl":" 闃挎柉钂傝姮"}]}
//                    case "video":
//                        r.add(findItem(videoObjects, (video) -> video.getId() == item.getObjectId()));
//                        break;
//                    case "problem":
//                        r.add(findItem(problemObjects, (problem) -> problem.getId() == item.getObjectId()));
//                        break;
//                    case "imageText":
//                        r.add(findItem(imageTextObject, (imageText) -> imageText.getId() == item.getObjectId()));
//                        break;
//                    case "quote":
//                        r.add(findItem(quoteObject, (quote) -> quote.getId() == item.getObjectId()));
//                        break;

                    }
                }

                Map<String, Object> r2 = new HashMap<>();
                r2.put("title", p.getTitle());
                r2.put("quotes", quoteObject);
                r2.put("contents", r);
                if (!videoObjects.isEmpty()) {
                    r2.put("video", videoObjects.get(0));
                }

                Map<String, Object> interaction = new HashMap<>();

                int readCount = 0;
                interaction.put("readCount", readCount);

                int likeCount = 0;
                /*String statsLikes = "SELECT COUNT(*) AS count FROM likes WHERE object_type = 'knowledge-point' AND object_id = " + id.toString();
                Query lq = em.createNativeQuery(statsLikes, Video.class);
                List likeCountList = lq.getResultList(); //->Object[]->count*/
                interaction.put("likeCount", 0);

           /* interaction.put("previous", previous);
            interaction.put("next", next);*/

                r2.put("interaction", interaction);
                r2.put("problems", problemObjects);
                //em.getTransaction().commit();

                conditions = new HashMap<>();
                conditions.put("objectType", "knowledge-point");
                conditions.put("objectId", id);
                List<Comment> comments = JPAEntry.getList(Comment.class, conditions);

                r2.put("comments", comments);


                if (!r.isEmpty()) {
                    result = Response.ok(new Gson().toJson(r2)).build();
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

