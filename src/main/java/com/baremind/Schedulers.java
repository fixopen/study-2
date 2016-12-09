package com.baremind;

import com.baremind.data.Log;
import com.baremind.data.Scheduler;
import com.baremind.utils.CharacterEncodingFilter;
import com.baremind.utils.Condition;
import com.baremind.utils.IdGenerator;
import com.baremind.utils.JPAEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.activation.registries.LogSupport;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;


@Path("schedulers")
public class Schedulers {
    Long findSubjectIdByName(String k) {
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

    Long findGradeByName(String k) {
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

    String GradeByName(int k) {
        String result = null;
        switch (k) {
            case 20:
                result = "低年级";
                break;
            case 21:
                result = "高年级";
                break;
            case 1:
                result = "一年级";
                break;
            case 2:
                result = "二年级";
                break;
            case 3:
                result = "三年级";
                break;
            case 4:
                result = "四年级";
                break;
            case 5:
                result = "五年级";
                break;
            case 6:
                result = "六年级";
                break;
        }
        return result;
    }
    @GET //根据条件查询课表
    @Path("keywords/{keywords}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getkeywords(@CookieParam("userId") String userId, @PathParam("keywords") String keywords) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            int a = 0;
            int b = 0;
            int c = 0;
            int yu = keywords.indexOf("语文");
            int shu = keywords.indexOf("数学");
            String filter = "";
            String k = "{";
            String g = "}";
            String d = ",";

            String subject = null;
            String teacherfilter = null;
            String gradefilter = null;

            if(yu != -1 && shu != -1){
                if(yu < shu){
                    subject = "subjectId:1";
                    a =1;
                }else{
                    subject = "subjectId:2";
                    a =1;
                }
            }else{
                if(yu != -1){
                    subject = "subjectId:1";
                    a =1;
                }
                if(shu != -1){
                    subject = "subjectId:2";
                    a =1;
                }
            }

            EntityManager em = JPAEntry.getEntityManager();
            String stats = "SELECT l.teacher FROM Scheduler l GROUP BY l.teacher";
            TypedQuery<String> q = em.createQuery(stats, String.class);
            List list  = q.getResultList();
            for (int i = 0; i < list.size(); i++)
            {
                String tea = (String) list.get(i);
                int teacher = keywords.indexOf(tea);
               if(teacher != -1){
                   teacherfilter = "teacher:"+tea+"";
                   b =2;
                   break;
               }
            }

            String stats2 = "SELECT l.grade FROM Scheduler l GROUP BY l.grade";
            TypedQuery<Long> gardes = em.createQuery(stats2, Long.class);
            List list2  = gardes.getResultList();
            for (int j = 0; j < list2.size(); j++)
            {
                int gra = (int) list2.get(j);
                String pegra = GradeByName(gra);
                int grade = keywords.indexOf(pegra);
                if(grade != -1){
                   long  gradel = findGradeByName(pegra);
                     gradefilter = "grade:"+gradel+"";
                    c=3;
                    break;
                }
            }


            if (a == 1 && b == 2 && c == 3) {
                filter = k + subject + d + teacherfilter + d + gradefilter + g;
            }
            if(a != 1 && b!=2 && c!=3){
                filter = "{subjectId:0}";
            }
            if(a == 1 && b!=2 && c!=3){
                filter = k+subject+g;
            }
            if(a == 1 && b==2 && c!=3){
                filter = k + subject + d + teacherfilter + g;
            }
            if(a != 1 && b==2 && c==3){
                filter = k + teacherfilter + d + gradefilter + g;
            }
            if(a != 1 && b!=2 && c==3){
                filter = k+gradefilter+g;
            }
            if(a != 1 && b==2 && c!=3){
                filter = k+teacherfilter+g;
            }
           /* if (a == 1 && b == 2) {
                filter = k + subject + d + teacherfilter + g;
            }*/
            /*if (b == 2 && c == 3) {
                filter = k + teacherfilter + d + gradefilter + g;
            }*/
            /*if (a == 1 && c == 3) {
                filter = k + subject + d + gradefilter + g;
            }*/







            /*if(a==1){
                filter = k+subject+g;
            }
            if(b==2){
                filter = k+teacherfilter+g;
            }
            if(c==3){
                filter = k+gradefilter+g;
            }*/



            final Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
//            System.out.println(filterObject);
            Map<String, String> orders = new HashMap<>();
            orders.put("startTime", "DESC");
            List<Scheduler> schedulers = JPAEntry.getList(Scheduler.class, filterObject, orders);
            result = Response.ok(new Gson().toJson(schedulers)).build();
        }

           /* String[] keywordArray = keywords.split(" ");
            EntityManager em = JPAEntry.getEntityManager();
            String stats = "SELECT s FROM Scheduler s WHERE (s.cdnLink IS NOT NULL AND s.cdnLink != '')";
            //boolean isFirst = true;
            for (int i = 0; i < keywordArray.length; ++i) {
                //if (isFirst) {
                //    stats += " WHERE ";
                //    isFirst = false;
                //} else {
                    stats += " AND ";
                //}
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
            result = Response.ok(new Gson().toJson(schedulers)).build();*/

        return result;
    }

    @GET //根据科目查询老师
    @Path("teachers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeacher(@CookieParam("userId") String userId, @QueryParam("filter") String filter) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            System.out.println(filter);
            Map<String, Object> filterObject = CharacterEncodingFilter.getFilters(filter);
            System.out.println(filterObject);
            EntityManager em = JPAEntry.getEntityManager();
            String stats = "SELECT l.teacher FROM Scheduler l WHERE l.subjectId = :subjectId GROUP BY l.teacher";
            TypedQuery<String> q = em.createQuery(stats, String.class);
            q.setParameter("subjectId", filterObject.get("subjectId"));
            result = Response.ok(new Gson().toJson(q.getResultList())).build();
        }
        return result;
    }

    @GET //根据科目查询年级
    @Path("teacherses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getgradeses(@CookieParam("userId") String userId) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            EntityManager em = JPAEntry.getEntityManager();
            String stats = "SELECT l.teacher FROM Scheduler l GROUP BY l.teacher";
            TypedQuery<String> q = em.createQuery(stats, String.class);
            result = Response.ok(new Gson().toJson(q.getResultList())).build();
        }
        return result;
    }

    @GET //根据科目查询年级
    @Path("grades")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getgrades(@CookieParam("userId") String userId, @QueryParam("filter") String filter) {
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

    @GET //根据科目查询年级
    @Path("gradeses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getgrades(@CookieParam("userId") String userId) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            EntityManager em = JPAEntry.getEntityManager();
            String stats = "SELECT l.grade FROM Scheduler l GROUP BY l.grade";
            TypedQuery<Long> q = em.createQuery(stats, Long.class);
            result = Response.ok(new Gson().toJson(q.getResultList())).build();
        }
        return result;
    }

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
    @GET //根据条件查询课表
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSchedulers(@CookieParam("userId") String userId, @QueryParam("filter") @DefaultValue("") String filter) {
        Response r = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
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

    @PUT
    @Path("{id}/like")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response like(@CookieParam("userId") String userId, @PathParam("id") Long id) {
        Response result = Response.status(401).build();
        if (JPAEntry.isLogining(userId)) {
            Log log = Logs.insert(Long.parseLong(userId), "playback", id, "like");
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
            Long count = Logs.deleteLike(Long.parseLong(userId), "playback", id);
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
            Long likeCount = Logs.getStatsCount("playback", id, "like");
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
            Boolean has = Logs.has(Long.parseLong(userId), "playback", id, "like");
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
            Long readCount = Logs.getStatsCount("playback", id, "read");
            result = Response.ok("{\"count\":" + readCount.toString() + "}").build();
        }
        return result;
    }

    @GET
    @Path("one/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@CookieParam("userId") String userId,@PathParam("id") Long id) {
        Response result = Response.status(401).build();
        Map map = new HashMap();
        if (JPAEntry.isLogining(userId)) {
            Logs.insert(userId,"playback",id,"read");
            Scheduler scheduler = JPAEntry.getObject(Scheduler.class, "id", id);
            map.put("id",scheduler.getId());
            map.put("grade",scheduler.getGrade());
            map.put("cdnLink",scheduler.getCdnLink());
            map.put("cover",scheduler.getCover());
            map.put("day",scheduler.getDay());
            map.put("description",scheduler.getDescription());
            map.put("duraction",scheduler.getDuration());
            map.put("year",scheduler.getYear());
            map.put("week",scheduler.getWeek());
            map.put("title",scheduler.getTitle());
            map.put("teacher",scheduler.getTeacher());
            map.put("subjectId",scheduler.getSubjectId());
            map.put("startTime",scheduler.getStartTime());
            map.put("endtime",scheduler.getEndTime());

            map.put("outline",scheduler.getOutline());
            map.put("prepare",scheduler.getPrepare());
            map.put("generalization",scheduler.getGeneralization());

            map.put("readCount",getReadCount(userId,scheduler.getId()));
            map.put("likeCount",getLikeCount(userId,scheduler.getId()));
            map.put("commit",Logs.getLogsCount(userId,"playback",scheduler.getId(),"comments"));
            map.put("teacherdescription",scheduler.getTeacherDescription());
            result = Response.ok(map).build();
        }
        return result;
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


              /*  int state = scheduler.getState();

                existScheduler.setState(state);*/


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

                String generalization = scheduler.getGeneralization();
                if (generalization != null) {
                    existScheduler.setGeneralization(generalization);
                }

                String outline = scheduler.getOutline();
                if (outline != null) {
                    existScheduler.setOutline(outline);
                }

                String prepare = scheduler.getPrepare();
                if (prepare != null) {
                    existScheduler.setPrepare(prepare);
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
