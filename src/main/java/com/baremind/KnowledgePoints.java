package com.baremind;

import com.baremind.data.*;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.function.Predicate;

@Path("knowledge-points")
public class KnowledgePoints {
    static List<Map<String, Object>> toMaps(List<KnowledgePoint> knowledgePoints) {
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
        return kpsm;
    }

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

    private <T> List<T> getList(EntityManager em, List<String> ids, Class<T> type) {
        return getListByColumn(em, "id", ids, type);
    }

    private <T> List<T> getListByColumn(EntityManager em, String columnName, List<String> ids, Class<T> type) {
        List<T> result = null;
        if (!ids.isEmpty()) {
            //String query = "SELECT * FROM " + tableName + " WHERE " + columnName + " IN ( " + join(ids) + " )";
            //Query pq = em.createNativeQuery(query, type);
            //result = pq.getResultList();
            String query = "SELECT o FROM " + type.getSimpleName() + " o WHERE o." + columnName + " IN ( " + join(ids) + " )";
            TypedQuery<T> pq = em.createQuery(query, type);
            result = pq.getResultList();
        }
        return result;
    }

    @PUT
    @Path("{id}/like")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response like(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Log log = Logs.insert(Long.parseLong(userId), "knowledge-point", id, "like");
            result = Response.ok(new Gson().toJson(log)).build();
        }
        return result;
    }

    @PUT
    @Path("{id}/unlike")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unlike(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Long count = Logs.deleteLike(Long.parseLong(userId), "knowledge-point", id);
            result = Response.ok("{\"count\":" + count.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/like-count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLikeCount(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Long likeCount = Logs.getStatsCount("knowledge-point", id, "like");
            result = Response.ok("{\"count\":" + likeCount.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/is-self-like")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelfLike(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Boolean has = Logs.has(Long.parseLong(userId), "knowledge-point", id, "like");
            result = Response.ok("{\"like\":" + has.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/read-count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadCount(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Long readCount = Logs.getStatsCount("knowledge-point", id, "read");
            result = Response.ok("{\"count\":" + readCount.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/contents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContentsById(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        Long selfId = Long.parseLong(userId);
        if (JPAEntry.isLogining(selfId)) {
            result = Response.status(404).build();
            KnowledgePoint p = JPAEntry.getObject(KnowledgePoint.class, "id", id);
            if (p != null) {
                JPAEntry.log(selfId, "read", "knowledge-point", id);
                Map<String, Object> conditions = new HashMap<>();
                conditions.put("knowledgePointId", id);

                Map<String, String> orders = new HashMap<>();
                orders.put("order", "ASC");
                List<KnowledgePointContentMap> maps = JPAEntry.getList(KnowledgePointContentMap.class, conditions, orders);

                List<String> textIds = new ArrayList<>();
                List<String> imageIds = new ArrayList<>();
                List<String> videoIds = new ArrayList<>();
                List<String> problemIds = new ArrayList<>();
                List<String> imageTextIds = new ArrayList<>();
                List<String> quoteIds = new ArrayList<>();
                List<String> pinyinIds = new ArrayList<>();

                for (KnowledgePointContentMap item : maps) {
                    switch (item.getObjectType()) {
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
                        case "pinyinText":
                            pinyinIds.add(item.getObjectId().toString());
                            break;
                    }
                }

                EntityManager em = JPAEntry.getEntityManager();

                List<Text> textObjects = getList(em, textIds, Text.class);

                List<Image> imageObjects = getList(em, imageIds, Image.class);

                List<Video> videoObjects = getList(em, videoIds, Video.class);

                List<Problem> problemObjects = getList(em, problemIds, Problem.class);
                List<ProblemOption> problemOptionObjects = getListByColumn(em, "problemId", problemIds, ProblemOption.class);
                List<ProblemStandardAnswer> problemStandardAnswerObjects = getListByColumn(em, "problemId", problemIds, ProblemStandardAnswer.class);

                List<ImageText> imageTextObject = getList(em, imageTextIds, ImageText.class);

                List<Quote> quoteObject = getList(em, quoteIds, Quote.class);

                List<PinyinText> pinyinTextObject = getList(em, pinyinIds, PinyinText.class);

                List<Object> orderedContents = new ArrayList<>();
                List<Object> orderedProblems = new ArrayList<>();
                List<Object> orderedQuotes = new ArrayList<>();

                for (final KnowledgePointContentMap item : maps) {
                    switch (item.getObjectType()) {
                        case "text":
                            if (textObjects != null) {
                                Text t = findItem(textObjects, (Text text) -> text.getId().longValue() == item.getObjectId().longValue());
                                Map<String, Object> tm = Text.convertToMap(t);
                                orderedContents.add(tm);
                            }
                            break;
                        case "image":
                            if (imageObjects != null) {
                                Image i = findItem(imageObjects, (image) -> image.getId().longValue() == item.getObjectId().longValue());
                                Map<String, Object> im = Image.convertToMap(i);
                                orderedContents.add(im);
                            }
                            break;
                        case "imageText":
                            if (imageTextObject != null) {
                                ImageText it = findItem(imageTextObject, (imageText) -> imageText.getId().longValue() == item.getObjectId().longValue());
                                Map<String, Object> itm = ImageText.convertToMap(it);
                                orderedContents.add(itm);
                            }
                            break;
                        case "pinyinText":
                            if (pinyinTextObject != null) {
                                PinyinText pt = findItem(pinyinTextObject, (pinyinText) -> pinyinText.getId().longValue() == item.getObjectId().longValue());
                                Map<String, Object> qm = PinyinText.convertToMap(pt);
                                orderedContents.add(qm);
                            }
                            break;
                        case "problem":
                            if (problemObjects != null || problemOptionObjects != null || problemStandardAnswerObjects != null) {
                                Problem problemItem = findItem(problemObjects, (problem) -> problem.getId().longValue() == item.getObjectId().longValue());
                                List<ProblemOption> problemOptions = findItems(problemOptionObjects, (ProblemOption problemoption) -> problemoption.getProblemId().longValue() == item.getObjectId().longValue());
                                List<ProblemStandardAnswer> problemStandardAnswers = findItems(problemStandardAnswerObjects, (problemstandardanswers) -> problemstandardanswers.getProblemId().longValue() == item.getObjectId().longValue());
                                Map<String, Object> pm = Problem.convertToMap(problemItem, problemOptions, problemStandardAnswers);
                                orderedProblems.add(pm);
                            }
                            break;
                        case "quote":
                            if (quoteObject != null) {
                                Quote q = findItem(quoteObject, (quote) -> quote.getId().longValue() == item.getObjectId().longValue());
                                orderedQuotes.add(q);
                            }
                            break;
                    }
                }

                Map<String, Object> totalResult = new HashMap<>();
                totalResult.put("title", p.getName());
                totalResult.put("quotes", orderedQuotes);
                totalResult.put("contents", orderedContents);

                if ((videoObjects != null) && !videoObjects.isEmpty()) {
                    Video video = videoObjects.get(0);
                    Map<String, Object> vm = Video.convertToMap(video);
                    totalResult.put("video", vm);
                }

                Map<String, Object> interaction = new HashMap<>();
                interaction.put("likeCount", Logs.getStatsCount("knowledge-point", id, "like"));
                interaction.put("readCount", Logs.getStatsCount("knowledge-point", id, "read"));
                totalResult.put("interaction", interaction);

                totalResult.put("problems", orderedProblems);

                conditions = new HashMap<>();
                conditions.put("objectType", "knowledge-point");
                conditions.put("objectId", id);
                List<Comment> comments = JPAEntry.getList(Comment.class, conditions);
                List<Map<String, Object>> commentMaps = Comments.toMaps(comments);
                totalResult.put("comments", commentMaps);
                String v = new Gson().toJson(totalResult);
                result = Response.ok(v, "application/json; charset=utf-8").build();
            }
        }
        return result;
    }

    @POST//添
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createKnowledgePoint(@CookieParam("userId") String userId, KnowledgePoint knowledgePoint) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            knowledgePoint.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(knowledgePoint);
            result = Response.ok(knowledgePoint).build();
        }
        return result;
    }

    @GET //根据条件查询
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKnowledgePoints(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            Map<String, String> orders = new HashMap<>();
            orders.put("order", "ASC");
            List<KnowledgePoint> knowledgePoints = JPAEntry.getList(KnowledgePoint.class, filterObject, orders);
            if (!knowledgePoints.isEmpty()) {
                result = Response.ok(new Gson().toJson(KnowledgePoints.toMaps(knowledgePoints))).build();
            }
        }
        return result;
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKnowledgePointById(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            KnowledgePoint knowledgePoint = JPAEntry.getObject(KnowledgePoint.class, "id", id);
            if (knowledgePoint != null) {
                Date now = new Date();
                Date yesterday = Date.from(now.toInstant().plusSeconds(-24 * 3600));
                result = Response.ok(new Gson().toJson(KnowledgePoint.convertToMap(knowledgePoint, now, yesterday))).build();
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateKnowledgePoint(@CookieParam("userId") String userId, @PathParam("id") Long id, KnowledgePoint knowledgePoint) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            KnowledgePoint existknowledgePoint = JPAEntry.getObject(KnowledgePoint.class, "id", id);
            if (existknowledgePoint != null) {
                Long volumeId = knowledgePoint.getVolumeId();
                if (volumeId != null) {
                    existknowledgePoint.setVolumeId(volumeId);
                }
                String title = knowledgePoint.getName();
                if (title != null) {
                    existknowledgePoint.setName(title);
                }
                Date showTime = knowledgePoint.getShowTime();
                if (showTime != null) {
                    existknowledgePoint.setShowTime(showTime);
                }
                int order = knowledgePoint.getOrder();
                if (order != 0) {
                    existknowledgePoint.setOrder(order);
                }
                Date show = knowledgePoint.getShowTime();
                if (show != null) {
                    existknowledgePoint.setShowTime(show);
                }
                JPAEntry.genericPut(existknowledgePoint);
                result = Response.ok(existknowledgePoint).build();
            }
        }
        return result;
    }
}

