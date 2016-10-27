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
    @Path("weeks/{week}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWeekScheduler(@CookieParam("userId") String userId, @PathParam("week") Integer week) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Calendar cal = Calendar.getInstance();//创建一个日期实例
            cal.setTime(new Date());//实例化一个日期
            int year = cal.get(Calendar.YEAR);
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
            List<Scheduler> schedulers = JPAEntry.getList(Scheduler.class, filterObject);
            //schedulers.remove(0);
            Collections.sort(schedulers, (left, right) -> {
                int result = 0;
                if (left.getYear() > right.getYear()) {
                    result = -1;
                } else if (left.getYear() < right.getYear()) {
                    result = 1;
                } else {
                    if (left.getWeek() > right.getWeek()) {
                        result = -1;
                    } else if (left.getWeek() < right.getWeek()) {
                        result = 1;
                    } else {
                        if (left.getDay() > right.getDay()) {
                            result = -1;
                        } else if (left.getDay() < right.getDay()) {
                            result = 1;
                        } else {
                            if (left.getStartTime().getTime() > right.getStartTime().getTime()) {
                                result = -1;
                            } else if (left.getStartTime().getTime() < right.getStartTime().getTime()) {
                                result = 1;
                            }
                        }
                    }
                }
                return result;
            });
            ArrayList<Scheduler> a2 = new ArrayList<>();
            ArrayList<Scheduler> a1 = new ArrayList<>();
            ArrayList<Scheduler> a = new ArrayList<>();
            for (Scheduler scheduler : schedulers) {
                if (scheduler.getState() == 2) {
                    a2.add(scheduler);
                }
                if (scheduler.getState() == 1) {
                    a1.add(scheduler);
                }
                if (scheduler.getState() == 0) {
                    a.add(scheduler);
                }
            }
            ArrayList<ArrayList<Scheduler>> result = new ArrayList<>();
            result.add(a1);
            result.add(a2);
            result.add(a);

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
                String description = scheduler.getDescription();
                if (description != null) {
                    existScheduler.setDescription(description);
                }
                Integer duration = scheduler.getDuration();
                if (duration != null) {
                    existScheduler.setDuration(duration);
                }

                Date endTime = scheduler.getEndTime();
                if (endTime != null) {
                    existScheduler.setEndTime(endTime);
                }

                int grade = scheduler.getGrade();
                if (grade != 0) {
                    existScheduler.setGrade(grade);
                }

                Date startTime = scheduler.getStartTime();
                if (startTime != null) {
                    existScheduler.setStartTime(startTime);
                }


                int state = scheduler.getState();

                existScheduler.setState(state);


                int day = scheduler.getDay();
                if (day != 0) {
                    existScheduler.setDay(day);
                }

                Long subjectId = scheduler.getSubjectId();
                if (subjectId != null) {
                    existScheduler.setSubjectId(subjectId);
                }


                String teacher = scheduler.getTeacher();
                if (teacher != null) {
                    existScheduler.setTeacher(teacher);
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

                String teacherDescription = scheduler.getTeacherDescription();
                if (teacherDescription != null) {
                    existScheduler.setTeacherDescription(teacherDescription);
                }

                String title = scheduler.getTitle();
                if (title != null) {
                    existScheduler.setTitle(title);
                }

                int week = scheduler.getWeek();
                if (week != 0) {
                    existScheduler.setWeek(week);
                }

                int year = scheduler.getYear();
                if (year != 0) {
                    existScheduler.setYear(year);
                }

                JPAEntry.genericPut(existScheduler);
                result = Response.ok(existScheduler).build();
            }
        }
        return result;
    }
}
