package com.baremind;

import com.baremind.data.*;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.Condition;
import com.baremind.utils.Impl;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

//GET /api/schedulers/this-week
//GET /api/schedulers/34
//GET /api/schedulers?filter={"week":34,"year":2014}

@Path("schedulers")
public class Schedulers {
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response get(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
//        Response result = Response.status(401).build();
//        User operator = JPAEntry.getLoginUser(sessionId);
//        if (operator != null) {
//            List<Scheduler> r = JPAEntry.getList(Scheduler.class, Impl.getFilters(filter));
//            List<String> ids = new ArrayList<>();
//            List<String> teacherIds = new ArrayList<>();
//            List<String> coverIds = new ArrayList<>();
//            for (Scheduler ri : r) {
//                ids.add(ri.getId().toString());
//                teacherIds.add(ri.getTeacherId().toString());
//                coverIds.add(ri.getCoverId().toString());
//            }
//            EntityManager em = JPAEntry.getEntityManager();
//            List<User> teachers = Resources.getList(em, teacherIds, User.class);
//            List<Image> covers = Resources.getList(em, coverIds, Image.class);
//
//            String likeCountQuery = "SELECT l.objectId, count(l) FROM Log l WHERE l.objectType = 'scheduler' AND l.objectId IN (" + Resources.join(ids) + ") AND l.action = 'like' GROUP BY l.objectId";
//            Query lq = em.createQuery(likeCountQuery);
//            final List<Object[]> likeStats = lq.getResultList();
//            String readCountQuery = "SELECT l.objectId, count(l) FROM Log l WHERE l.objectType = 'scheduler' AND l.objectId IN (" + Resources.join(ids) + ") AND l.action = 'read' GROUP BY l.objectId";
//            Query rq = em.createQuery(readCountQuery);
//            final List<Object[]> readStats = lq.getResultList();
//            String likedQuery = "SELECT l.objectId, count(l) FROM Log l WHERE l.objectType = 'scheduler' AND l.objectId IN (" + Resources.join(ids) + ") AND l.action = 'read' AND l.userId = " + JPAEntry.getLoginUser(sessionId).getId().toString() + " GROUP BY l.objectId";
//            Query ldq = em.createQuery(likedQuery);
//            final List<Object[]> likedStats = ldq.getResultList();
//            List<Comment> comments = Resources.getListByColumn(em, "objectId", ids, Comment.class);
//
//            Map<String, String> orders = new HashMap<>();
//            orders.put("startTime", "DESC");
//            result = Impl.get(sessionId, filter, orders, Scheduler.class, scheduler -> Scheduler.convertToMap(scheduler, teachers, covers, likeStats, likedStats, readStats, comments, Users.isVIP(operator)), null);
//        }
//        return result;
//    }

    @GET //根据条件查询课表
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSchedulers(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            Long userId = JPAEntry.getLoginUser(sessionId).getId();
            final Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            //把每一个前台传过来的数据当成key，value处理
            if(filterObject != null){
                filterObject.forEach((key, value) -> {
                    //判断value是字符串不
                    if (value instanceof String) {
                        //这个字符串是不是有<、>、!= 开头
                        String[] opAndValue = split((String) value);
                        if (!opAndValue[0].equals("")) {
                            Object val = null;
                            if (!opAndValue[1].equals("NULL")) {
                                switch (key) {
                                    case "endTime":
                                        val = new Date(Long.parseLong(opAndValue[1]));
//                                        String str = opAndValue[1];
//                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                        try {
//                                            Date date = format.parse(str);
//                                            val = date;
//                                        } catch (ParseException e) {
//                                            e.printStackTrace();
//                                        }
                                        break;
                                    default:
                                        val = opAndValue[1];
                                }
                            }
                            Condition c = new Condition(opAndValue[0], val);
                            filterObject.put(key, c);
                        }
                    }
                });
            }


            Map<String, String> orders = new HashMap<>();
            Map<String,Object> map = new HashMap<>();
            orders.put("startTime", "DESC");
            List<Scheduler> schedulers = JPAEntry.getList(Scheduler.class, filterObject, orders);

            List<String> ids = new ArrayList<>();
            //List<String> teacherIds = new ArrayList<>();
            //List<String> coverIds = new ArrayList<>();
            for (Scheduler ri : schedulers) {
                ids.add(ri.getId().toString());
                //teacherIds.add(ri.getTeacherId().toString());
                //coverIds.add(ri.getCoverId().toString());
            }
            EntityManager em = JPAEntry.getEntityManager();
            //List<User> teachers = Resources.getList(em, teacherIds, User.class);
            //List<Image> covers = Resources.getList(em, coverIds, Image.class);

            ArrayList<Scheduler> featured = new ArrayList<>();
            ArrayList<Scheduler> playing = new ArrayList<>();
            ArrayList<Scheduler> passed = new ArrayList<>();
            ArrayList<Scheduler> featuredUpsideDown = new ArrayList<>();
            Date now = new Date();
            for (Scheduler scheduler : schedulers) {
                if (now.before(scheduler.getStartTime())) {
                    featured.add(scheduler);
                } else {
                    if (now.before(scheduler.getEndTime())) {
                        playing.add(scheduler);
                    } else {
                        map.put("id",scheduler.getId());
                        map.put("grade",scheduler.getGrade());
                        map.put("id",scheduler.getId());
                        map.put("id",scheduler.getId());
                        map.put("id",scheduler.getId());
                        map.put("id",scheduler.getId());
                        map.put("id",scheduler.getId());
                        passed.add(scheduler);

                    }
                }
            }
            List<List<Scheduler>> results = new ArrayList<>();
            results.add(playing);//正播
            Collections.reverse(featured); // 倒序排列
            results.add(featured); //未播
            results.add(passed);//播过

            String likeCountQuery = "SELECT l.objectId, count(l) FROM Log l WHERE l.objectType = 'scheduler' AND l.objectId IN (" + KnowledgePoints.join(ids) + ") AND l.action = 'like' GROUP BY l.objectId";
            Query lq = em.createQuery(likeCountQuery);
            final List<Object[]> likeStats = lq.getResultList();
            String readCountQuery = "SELECT l.objectId, count(l) FROM Log l WHERE l.objectType = 'scheduler' AND l.objectId IN (" + KnowledgePoints.join(ids) + ") AND l.action = 'read' GROUP BY l.objectId";
            Query rq = em.createQuery(readCountQuery);
            final List<Object[]> readStats = lq.getResultList();
            String likedQuery = "SELECT l.objectId, count(l) FROM Log l WHERE l.objectType = 'scheduler' AND l.objectId IN (" + KnowledgePoints.join(ids) + ") AND l.action = 'read' AND l.userId = " + userId + " GROUP BY l.objectId";
            Query ldq = em.createQuery(likedQuery);
            final List<Object[]> likedStats = ldq.getResultList();
            List<Comment> comments = Resources.getListByColumn(em, "objectId", ids, Comment.class);
            List<List<Map<String, Object>>> res = new ArrayList<>();

            for (List<Scheduler> ri : results) {
                List<Map<String, Object>> rim = new ArrayList<>();
                for (Scheduler rr : ri) {
                    rim.add(Scheduler.convertToMap(rr, null, null, likeStats, likedStats, readStats, comments, false));
                }
                res.add(rim);
            }

            //Gson gson = new GsonBuilder().registerTypeAdapter(java.sql.Time.class, new TimeTypeAdapter()).create();
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            result = Response.ok(gson.toJson(res)).build();
        }
        return result;
    }



    final String[] ops = {"IS NOT ", "IS ", "< ", "<= ", "> ", ">= ", "!= "};

    private String[] split(String v) {
        String[] result = {"", ""};
        for (String op: ops) {
            if (v.startsWith(op)) {
                result[0] = op.trim();
                result[1] = v.substring(op.length());
                break;
            }
        }
        return result;
    }


    @GET //根据id查询
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.getById(sessionId, id, Scheduler.class, scheduler -> Scheduler.convertToMap(scheduler, JPAEntry.getLoginUser(sessionId).getId()));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@CookieParam("sessionId") String sessionId, Scheduler entity) {
        return Impl.create(sessionId, entity, null, null);
    }

    @PUT //根据id修改
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id, Scheduler newData) {
        return Impl.updateById(sessionId, id, newData, Scheduler.class, (exist, scheduler) -> {
            Integer year = scheduler.getYear();
            if (year != null) {
                exist.setYear(year);
            }
            Integer week = scheduler.getWeek();
            if (week != null) {
                exist.setWeek(week);
            }
            Integer day = scheduler.getDay();
            if (day != null) {
                exist.setDay(day);
            }
            Date startTime = scheduler.getStartTime();
            if (startTime != null) {
                exist.setStartTime(startTime);
            }
            Date endTime = scheduler.getEndTime();
            if (endTime != null) {
                exist.setEndTime(endTime);
            }
            Long subjectId = scheduler.getSubjectId();
            if (subjectId != null) {
                exist.setSubjectId(subjectId);
            }
            Integer grade = scheduler.getGrade();
            if (grade != null) {
                exist.setGrade(grade);
            }
            String title = scheduler.getName();
            if (title != null) {
                exist.setName(title);
            }
            Long cover = scheduler.getCoverId();
            if (cover != null) {
                exist.setCoverId(cover);
            }
            String cdnLink = scheduler.getContentLink();
            if (cdnLink != null) {
                exist.setContentLink(cdnLink);
            }
            String directLink = scheduler.getDirectLink();
            if (directLink != null) {
                exist.setDirectLink(directLink);
            }
            String description = scheduler.getDescription();
            if (description != null) {
                exist.setDescription(description);
            }
            Long teacher = scheduler.getTeacherId();
            if (teacher != null) {
                exist.setTeacherId(teacher);
            }
        }, null);
    }

    @DELETE
    @Path("{id}")
    public Response deleteById(@CookieParam("sessionId") String sessionId, @PathParam("id") Long id) {
        return Impl.deleteById(sessionId, id, Scheduler.class);
    }

    private Long findSubjectIdByName(String k) {
        Long result = null;
        switch (k) {
            case "语文":
                result = 1L;
                break;
            case "数学":
                result = 2L;
                break;
        }
        return result;
    }

    private Long findGradeByName(String k) {
        Long result = null;
        switch (k) {
            case "低年级":
                result = 20L;
                break;
            case "高年级":
                result = 21L;
                break;
            case "一年级":
                result = 1L;
                break;
            case "二年级":
                result = 2L;
                break;
            case "三年级":
                result = 3L;
                break;
            case "四年级":
                result = 4L;
                break;
            case "五年级":
                result = 5L;
                break;
            case "六年级":
                result = 6L;
                break;
        }
        return result;
    }

    @GET //查询年级
    @Path("grades")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGrades(@CookieParam("sessionId") String sessionId, @QueryParam("filter") String filter) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            Map<String, Object> filterObject = Impl.getFilters(filter);
            EntityManager em = JPAEntry.getEntityManager();
            String stats = "SELECT l.grade FROM Scheduler l WHERE l.subjectId = :subjectId GROUP BY l.grade";
            TypedQuery<Long> q = em.createQuery(stats, Long.class);
            q.setParameter("subjectId", filterObject.get("subjectId"));
            result = Response.ok(new Gson().toJson(q.getResultList())).build();
        }
        return result;
    }

    @GET //根据keywords查询课表
    @Path("keywords/{keywords}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByKeywords(@CookieParam("sessionId") String sessionId, @PathParam("keywords") String keywords) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            String[] keywordArray = keywords.split(" ");
            EntityManager em = JPAEntry.getEntityManager();
            String stats = "SELECT s FROM Scheduler s";
            boolean isFirst = true;
            for (String aKeywordArray : keywordArray) {
                if (isFirst) {
                    stats += " WHERE ";
                    isFirst = false;
                } else {
                    stats += " AND ";
                }
                stats += "((s.teacher = '" + aKeywordArray + "')";
                Long subjectId = findSubjectIdByName(aKeywordArray);
                if (subjectId != null) {
                    stats += " OR (s.subjectId = " + subjectId.toString() + ")";
                }
                Long grade = findGradeByName(aKeywordArray);
                if (grade != null) {
                    stats += " OR (s.grade = " + grade.toString() + ")";
                }
                stats += ")";
            }
            TypedQuery<Scheduler> q = em.createQuery(stats, Scheduler.class);
            List<Scheduler> schedulers = q.getResultList();
            result = Response.ok(new Gson().toJson(schedulers)).build();
        }
        return result;
    }


    @GET
    @Path("one/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("sessionId") String sessionId,@PathParam("id") Long id) {
        Response result = Impl.validationUser(sessionId);
        Map map = new HashMap();
        if (result.getStatus() == 202) {
            Session session = JPAEntry.getSession(sessionId);
            Logs.insert(session.getUserId(),"playback",id,"read");
            Scheduler scheduler = JPAEntry.getObject(Scheduler.class, "id", id);
            map.put("id",scheduler.getId());
            map.put("grade",scheduler.getGrade());
            map.put("cdnLink",scheduler.getDirectLink());
            map.put("cover",scheduler.getCoverId());
            map.put("day",scheduler.getDay());
            map.put("description",scheduler.getDescription());
            map.put("year",scheduler.getYear());
            map.put("week",scheduler.getWeek());
            map.put("title",scheduler.getName());
            map.put("teacher",scheduler.getTeacherId());
            map.put("subjectId",scheduler.getSubjectId());
            map.put("startTime",scheduler.getStartTime());
            map.put("endtime",scheduler.getEndTime());

//            map.put("outline",scheduler.getOutline());
//            map.put("prepare",scheduler.getPrepare());
//            map.put("generalization",scheduler.getOutline());

            map.put("readCount",Logs.getStatsCount("playback", id, "read"));
            map.put("likeCount",Logs.getStatsCount("playback", id, "like"));
            map.put("liked", Logs.has(session.getUserId(), "playback", id, "like"));
            Map<String, Object> cc = new HashMap<>();
            cc.put("objectType", "playback");
            cc.put("objectId", id);
            map.put("comments",JPAEntry.getList(Comment.class, cc));
            map.put("teacherdescription",scheduler.getDescription());
            result = Response.ok(map).build();
        }
        return result;
    }

    @GET //根据周查询课表
    @Path("years/{year}/weeks/{week}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWeekScheduler(@CookieParam("sessionId") String sessionId, @PathParam("year") Integer year, @PathParam("week") Integer week) {
        Map<String, Object> filterObject = new HashMap<>(2);
        filterObject.put("year", year);
        filterObject.put("week", week);
        return Impl.get(sessionId, filterObject, null, Scheduler.class, scheduler -> Scheduler.convertToMap(scheduler, JPAEntry.getLoginUser(sessionId).getId()), null);
    }

    @GET //获取classroom-key
    @Path("key")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKey(@CookieParam("sessionId") String sessionId) {
        Response result = Impl.validationUser(sessionId);
        if (result.getStatus() == 202) {
            String key = "";
            result = Response.ok("{\"key\":\"" + key + "\"}").build();
        }
        return result;
    }
}
