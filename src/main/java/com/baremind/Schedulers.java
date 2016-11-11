package com.baremind;

import com.baremind.data.Scheduler;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.persistence.EntityManager;
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
    private Long findSubjectIdByName(String k) {
        Long result = null;
        switch (k) {
            case "语文":
                result = 1l;
                break;
            case "数学":
                result = 2l;
                break;
        }
        return result;
    }

    private Long findGradeByName(String k) {
        Long result = null;
        switch (k) {
            case "低年级":
                result = 20l;
                break;
            case "高年级":
                result = 21l;
                break;
            case "一年级":
                result = 1l;
                break;
            case "二年级":
                result = 2l;
                break;
            case "三年级":
                result = 3l;
                break;
            case "四年级":
                result = 4l;
                break;
            case "五年级":
                result = 5l;
                break;
            case "六年级":
                result = 6l;
                break;
        }
        return result;
    }

    @GET //根据周查询课表
    @Path("keywords/{keywords}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByKeywords(@CookieParam("userId") String userId, @PathParam("keywords") String keywords) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            String[] keywordArray = keywords.split(" ");
            EntityManager em = JPAEntry.getEntityManager();
            String stats = "SELECT s FROM Scheduler s";
            boolean isFirst = true;
            for (int i = 0; i < keywordArray.length; ++i) {
                if (isFirst) {
                    stats += " WHERE ";
                    isFirst = false;
                } else {
                    stats += " AND ";
                }
                String k = keywordArray[i];
                stats += "((s.teacher = '" + k + "')";
                Long subjectId = findSubjectIdByName(k);
                if (subjectId != null) {
                    stats += " OR (s.subjectId = " + subjectId.toString() + ")";
                }
                Long grade = findGradeByName(k);
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

    @GET //根据科目查询老师
    @Path("teachers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeacher(@CookieParam("userId") String userId, @QueryParam("filter") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            EntityManager em = JPAEntry.getEntityManager();
            String stats = "SELECT l.teacher FROM Scheduler l WHERE l.subjectId = :subjectId GROUP BY l.teacher";
            TypedQuery<String> q = em.createQuery(stats, String.class);
            q.setParameter("subjectId", filterObject.get("subjectId"));
            result = Response.ok(new Gson().toJson(q.getResultList())).build();
        }
        return result;
    }

    @GET //根据科目查询年级
    @Path("grades")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGrades(@CookieParam("userId") String userId, @QueryParam("filter") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            EntityManager em = JPAEntry.getEntityManager();
            String stats = "SELECT l.grade FROM Scheduler l WHERE l.subjectId = :subjectId GROUP BY l.grade";
            TypedQuery<Long> q = em.createQuery(stats, Long.class);
            q.setParameter("subjectId", filterObject.get("subjectId"));
            result = Response.ok(new Gson().toJson(q.getResultList())).build();
        }
        return result;
    }

    @GET //根据周查询课表
    @Path("years/{year}/weeks/{week}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWeekScheduler(@CookieParam("userId") String userId, @PathParam("year") Integer year, @PathParam("week") Integer week) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Map<String, Object> filterObject = new HashMap<>(2);
            filterObject.put("year", year);
            filterObject.put("week", week);

           List<Scheduler> schedulers = JPAEntry.getList(Scheduler.class, filterObject);
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            //Gson gson = new GsonBuilder().registerTypeAdapter(java.sql.Time.class, new TimeTypeAdapter()).create();
            result = Response.ok(gson.toJson(schedulers)).build();
        }
        return result;
    }

    @GET //根据周查询课表
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSchedulerById(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Scheduler scheduler = JPAEntry.getObject(Scheduler.class, "id", id);
            if (scheduler != null) {
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                //Gson gson = new GsonBuilder().registerTypeAdapter(java.sql.Time.class, new TimeTypeAdapter()).create();
                result = Response.ok(gson.toJson(scheduler)).build();
            }
        }
        return result;
    }

    @GET //根据条件查询课表
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSchedulers(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response r = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            Map<String, String> orders = new HashMap<>();
            orders.put("startTime", "DESC");
            List<Scheduler> schedulers = JPAEntry.getList(Scheduler.class, filterObject, orders);
            ArrayList<Scheduler> featured = new ArrayList<>();
            ArrayList<Scheduler> playing = new ArrayList<>();
            ArrayList<Scheduler> passed = new ArrayList<>();
            Date now = new Date();
            for (Scheduler scheduler : schedulers) {
                if (now.before(scheduler.getStartTime())) {
                    featured.add(scheduler);
                } else {
                    if (now.before(scheduler.getEndTime())) {
                        playing.add(scheduler);
                    } else {
                        passed.add(scheduler);
                    }
                }
            }
            ArrayList<ArrayList<Scheduler>> result = new ArrayList<>();
            result.add(playing);//正播
            Collections.reverse(featured); // 倒序排列
            result.add(featured); //未播
            result.add(passed);//播过
            //Gson gson = new GsonBuilder().registerTypeAdapter(java.sql.Time.class, new TimeTypeAdapter()).create();
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            r = Response.ok(gson.toJson(result)).build();
        }
        return r;
    }

    @GET //获取classroom-key
    @Path("key")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKey(@CookieParam("userId") String userId) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            String key = "";
            result = Response.ok("{\"key\":\"" + key + "\"}").build();
        }
        return result;
    }

    @POST //添加课表
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createScheduler(@CookieParam("userId") String userId, Scheduler scheduler) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            scheduler.setId(IdGenerator.getNewId());
            JPAEntry.genericPost(scheduler);
            result = Response.ok(scheduler).build();
        }
        return result;
    }

    @PUT //修改课表
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateScheduler(@CookieParam("userId") String userId, @PathParam("id") Long id, Scheduler scheduler) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            result = Response.status(404).build();
            Scheduler existScheduler = JPAEntry.getObject(Scheduler.class, "id", id);
            if (existScheduler != null) {
                Integer year = scheduler.getYear();
                if (year != null) {
                    existScheduler.setYear(year);
                }
                Integer week = scheduler.getWeek();
                if (week != null) {
                    existScheduler.setWeek(week);
                }
                Integer day = scheduler.getDay();
                if (day != null) {
                    existScheduler.setDay(day);
                }
                Date startTime = scheduler.getStartTime();
                if (startTime != null) {
                    existScheduler.setStartTime(startTime);
                }
                Date endTime = scheduler.getEndTime();
                if (endTime != null) {
                    existScheduler.setEndTime(endTime);
                }
                Long subjectId = scheduler.getSubjectId();
                if (subjectId != null) {
                    existScheduler.setSubjectId(subjectId);
                }
                Integer grade = scheduler.getGrade();
                if (grade != null) {
                    existScheduler.setGrade(grade);
                }
                String title = scheduler.getName();
                if (title != null) {
                    existScheduler.setName(title);
                }
                Long cover = scheduler.getCoverId();
                if (cover != null) {
                    existScheduler.setCoverId(cover);
                }
                String cdnLink = scheduler.getContentLink();
                if (cdnLink != null) {
                    existScheduler.setContentLink(cdnLink);
                }
                String directLink = scheduler.getDirectLink();
                if (directLink != null) {
                    existScheduler.setDirectLink(directLink);
                }
                String description = scheduler.getDescription();
                if (description != null) {
                    existScheduler.setDescription(description);
                }
                String teacher = scheduler.getTeacher();
                if (teacher != null) {
                    existScheduler.setTeacher(teacher);
                }
                String teacherDescription = scheduler.getTeacherDescription();
                if (teacherDescription != null) {
                    existScheduler.setTeacherDescription(teacherDescription);
                }
                JPAEntry.genericPut(existScheduler);
                result = Response.ok(existScheduler).build();
            }
        }
        return result;
    }
}
