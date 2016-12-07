package com.baremind;

import com.baremind.data.KnowledgePoint;
import com.baremind.data.Log;
import com.baremind.data.User;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;


@Path("knowledge-points")
public class KnowledgePoints {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest req, @CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        req.getRemoteAddr();
        List<KnowledgePoint> r = JPAEntry.getList(KnowledgePoint.class, CharacterEncodingFilter.getFilters(filter));
        List<String> ids = new ArrayList<>();
        for (KnowledgePoint ri : r) {
            ids.add(ri.getId().toString());
        }
        EntityManager em = JPAEntry.getEntityManager();
        String contentCountQuery = "SELECT knowledge_point_id, object_type, count(*) FROM knowledge_point_content_maps WHERE knowledge_point_id IN (" + Resources.join(ids) + ") GROUP BY knowledge_point_id, object_type";
        TypedQuery<KnowledgePoint.ContentStats> cq = (TypedQuery<KnowledgePoint.ContentStats>)em.createNativeQuery(contentCountQuery, KnowledgePoint.ContentStats.class);
        List<KnowledgePoint.ContentStats> contentStats = cq.getResultList();
        String likeCountQuery = "SELECT object_id, count(*) FROM logs WHERE object_type = 'knowledge-point' AND object_id IN (" + Resources.join(ids) + ") AND action = 'like' GROUP BY object_id";
        TypedQuery<KnowledgePoint.BaseStats> lq = (TypedQuery<KnowledgePoint.BaseStats>)em.createNativeQuery(likeCountQuery, KnowledgePoint.BaseStats.class);
        List<KnowledgePoint.BaseStats> likeStats = lq.getResultList();
        String readCountQuery = "SELECT object_id, count(*) FROM logs WHERE object_type = 'knowledge-point' AND object_id IN (" + Resources.join(ids) + ") AND action = 'read' GROUP BY object_id";
        TypedQuery<KnowledgePoint.BaseStats> rq = (TypedQuery<KnowledgePoint.BaseStats>)em.createNativeQuery(readCountQuery, KnowledgePoint.BaseStats.class);
        List<KnowledgePoint.BaseStats> readStats = lq.getResultList();
        String likedQuery = "SELECT object_id, count(*) FROM logs WHERE object_type = 'knowledge-point' AND object_id IN (" + Resources.join(ids) + ") AND action = 'read' AND user_id = " + JPAEntry.getLoginId(sessionId).toString() + " GROUP BY object_id";
        TypedQuery<KnowledgePoint.BaseStats> ldq = (TypedQuery<KnowledgePoint.BaseStats>)em.createNativeQuery(likedQuery, KnowledgePoint.BaseStats.class);
        List<KnowledgePoint.BaseStats> likedStats = ldq.getResultList();

        Map<String, String> orders = new HashMap<>();
        orders.put("order", "ASC");
        final Date now = new Date();
        final Date yesterday = Date.from(now.toInstant().plusSeconds(-24 * 3600));
        return Impl.get(sessionId, filter, orders, KnowledgePoint.class, knowledgePoint -> KnowledgePoint.convertToMap(knowledgePoint, likeStats, likedStats, readStats, contentStats, now, yesterday), null);

//        Map<String, String> orders = new HashMap<>();
//        orders.put("order", "ASC");
//        final Date now = new Date();
//        final Date yesterday = Date.from(now.toInstant().plusSeconds(-24 * 3600));
//        return Impl.get(sessionId, filter, orders, KnowledgePoint.class, knowledgePoint -> KnowledgePoint.convertToMap(knowledgePoint, JPAEntry.getLoginId(sessionId), now, yesterday), (knowledgePoint) -> {
//            boolean result = true;
//            EntityManager em = JPAEntry.getEntityManager();
//            String stats = "SELECT COUNT(m) FROM KnowledgePointContentMap m WHERE m.knowledgePointId = " + knowledgePoint.getId().toString();
//            TypedQuery<Long> q = em.createQuery(stats, Long.class);
//            Long c = q.getSingleResult();
//            if (c == 0L) {
//                result = false;
//            }
//            return result;
//        });
    }

    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        final Date now = new Date();
        final Date yesterday = Date.from(now.toInstant().plusSeconds(-24 * 3600));
        return Impl.getById(sessionId, id, KnowledgePoint.class, (knowledgePoint) -> KnowledgePoint.convertToMap(knowledgePoint, JPAEntry.getLoginId(sessionId), now, yesterday));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, KnowledgePoint entity) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.create(sessionId, entity, null);
            }
        }
        return result;
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, KnowledgePoint newData) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.updateById(sessionId, id, newData, KnowledgePoint.class, (exist, knowledgePoint) -> {
                    Long volumeId = knowledgePoint.getVolumeId();
                    if (volumeId != null) {
                        exist.setVolumeId(volumeId);
                    }
                    String title = knowledgePoint.getName();
                    if (title != null) {
                        exist.setName(title);
                    }
                    Date showTime = knowledgePoint.getShowTime();
                    if (showTime != null) {
                        exist.setShowTime(showTime);
                    }
                    int order = knowledgePoint.getOrder();
                    if (order != 0) {
                        exist.setOrder(order);
                    }
                    Date show = knowledgePoint.getShowTime();
                    if (show != null) {
                        exist.setShowTime(show);
                    }
                }, null);
            }
        }
        return result;
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            User admin = JPAEntry.getObject(User.class, "id", JPAEntry.getLoginId(sessionId));
            if (admin != null && admin.getIsAdministrator()) {
                result = Impl.deleteById(sessionId, id, KnowledgePoint.class);
            }
        }
        return result;
    }

    static List<Map<String, Object>> toMaps(Long userId, List<KnowledgePoint> knowledgePoints) {
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
            Map<String, Object> kpm = KnowledgePoint.convertToMap(kp, userId, now, yesterday);
            kpsm.add(kpm);
        }
        return kpsm;
    }

    @PUT
    @Path("{id}/like")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response like(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            Log log = Logs.insert(Long.parseLong(sessionId), "knowledge-point", id, "like");
            result = Response.ok(new Gson().toJson(log)).build();
        }
        return result;
    }

    @PUT
    @Path("{id}/unlike")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response unlike(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            Long count = Logs.deleteLike(Long.parseLong(sessionId), "knowledge-point", id);
            result = Response.ok("{\"count\":" + count.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/like-count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLikeCount(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            Long likeCount = Logs.getStatsCount("knowledge-point", id, "like");
            result = Response.ok("{\"count\":" + likeCount.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/is-self-like")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSelfLike(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            Boolean has = Logs.has(Long.parseLong(sessionId), "knowledge-point", id, "like");
            result = Response.ok("{\"like\":" + has.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("{id}/read-count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadCount(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            Long readCount = Logs.getStatsCount("knowledge-point", id, "read");
            result = Response.ok("{\"count\":" + readCount.toString() + "}").build();
        }
        return result;
    }


    @GET
    @Path("{id}/contents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContentsById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            result = Response.status(404).build();
            KnowledgePoint p = JPAEntry.getObject(KnowledgePoint.class, "id", id);
            if (p != null) {
                result = Response.ok(new Gson().toJson(p.getContent()), "application/json; charset=utf-8").build();
            }
        }
        return result;
    }
}

