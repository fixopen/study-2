package com.baremind;

import com.baremind.data.Scheduler;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;


//GET /api/schedulers/this-week
//GET /api/schedulers/34
//GET /api/schedulers?filter={"week":34,"year":2014}

@Path("schedulers")
public class Schedulers {
    @GET //根据周查询课表
    @Path("years/{year}/weeks/{week}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWeekScheduler(@CookieParam("userId") String userId, @PathParam("year") Integer year, @PathParam("week") Integer week) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
//            Calendar cal = Calendar.getInstance();//创建一个日期实例
//            cal.setTime(new Date());//实例化一个日期
//            int year = cal.get(Calendar.YEAR);
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
    public Response getWeekScheduler(@CookieParam("userId") String userId, @PathParam("id") Long id) {
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
            ArrayList<Scheduler> featuredUpsideDown = new ArrayList<>();
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
                Integer duration = scheduler.getDuration();
                if (duration != null) {
                    existScheduler.setDuration(duration);
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
                String cover = scheduler.getCover();
                if (cover != null) {
                    existScheduler.setCover(cover);
                }
                String cdnLink = scheduler.getCdnLink();
                if (cdnLink != null) {
                    existScheduler.setCdnLink(cdnLink);
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
