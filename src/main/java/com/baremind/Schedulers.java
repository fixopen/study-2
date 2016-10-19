package com.baremind;

import com.baremind.data.Image;
import com.baremind.data.ProblemOption;
import com.baremind.data.Scheduler;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Time;
import java.util.*;

//GET /api/schedulers/this-week
//GET /api/schedulers/34
//GET /api/schedulers?filter={"week":34,"year":2014}

@Path("schedulers")
public class Schedulers {
    @GET //查询(获取本周)课表
    @Path("this-week")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getThisWeekScheduler(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            Calendar cal = Calendar.getInstance();//创建一个日期实例
            cal.setTime(new Date());//实例化一个日期
            int year = cal.get(Calendar.YEAR);
            int weekNo = cal.get(Calendar.WEEK_OF_YEAR);

            Map<String, Object> filterObject = new HashMap<>(2);
            filterObject.put("year", year);
            filterObject.put("week", weekNo);
            List<Scheduler> schedulers = JPAEntry.getList(Scheduler.class, filterObject);
            result = Response.ok(new Gson().toJson(schedulers)).build();
        }
        return result;
    }

    @GET //根据周查询课表
    @Path("{week}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWeekScheduler(@CookieParam("sessionId") String sessionId, @PathParam("week") Integer week) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            Calendar cal = Calendar.getInstance();//创建一个日期实例
            cal.setTime(new Date());//实例化一个日期
            int year = cal.get(Calendar.YEAR);
            Map<String, Object> filterObject = new HashMap<>(2);
            filterObject.put("year", year);
            filterObject.put("week", week);
            List<Scheduler> schedulers = JPAEntry.getList(Scheduler.class, filterObject);
            result = Response.ok(new Gson().toJson(schedulers)).build();
        }
        return result;
    }

    @GET //根据条件查询课表
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWeekScheduler(@CookieParam("sessionId") String sessionId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response r = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            List<Scheduler> schedulers = JPAEntry.getList(Scheduler.class, filterObject);
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
                ArrayList a2 = new ArrayList();
                ArrayList a1 = new ArrayList();
                ArrayList a  =  new  ArrayList();
                ArrayList AA  =  new  ArrayList();
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


            AA.add(a1);
            AA.add(a2);
            AA.add(a);

            //left.getYear() - right.getYear(), left.getWeek() - right.getWeek(), left.getDay() - right.getDay()
            r = Response.ok(new Gson().toJson(AA)).build();
        }
        return r;
    }

    @GET //获取classroom-key
    @Path("key")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKey(@CookieParam("sessionId") String sessionId) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
            String key = "";
            result = Response.ok("{\"key\":\"" + key + "\"}").build();
        }
        return result;
    }

    @POST //添加课表
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createScheduler(@CookieParam("sessionId") String sessionId, Scheduler scheduler) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
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
    public Response updateScheduler(@CookieParam("sessionId") String sessionId, @PathParam("id") Integer id, Scheduler scheduler) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(sessionId)) {
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

                Time endTime = scheduler.getEndTime();
                if (endTime != null) {
                    existScheduler.setEndTime(endTime);
                }

                int grade = scheduler.getGrade();
                if (grade != 0) {
                    existScheduler.setGrade(grade);
                }

                Time startTime = scheduler.getStartTime();
                if (startTime != null) {
                    existScheduler.setStartTime(startTime);
                }


                int state = scheduler.getState();
                if (state != 0) {
                    existScheduler.setState(state);
                }

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
