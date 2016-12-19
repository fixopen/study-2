package com.baremind.data;


import com.baremind.Logs;
import com.baremind.Resources;
import com.baremind.utils.JPAEntry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2016/8/18.
 */
@Entity
@Table(name = "schedulers")
public class Scheduler implements com.baremind.data.Entity, Resource {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "year")
    private Integer year;

    @Column(name = "week")
    private Integer week;

    @Column(name = "day")
    private Integer day;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "name")
    private String name;

//    @Column(name = "abstraction")
//    private String abstraction;
//
//    @Column(name = "outline")
//    private String outline;

    @Column(name = "description")
    private String description;

//    @Column(name = "prepare")
//    private String prepare;

    @Column(name = "cover_id")
    private Long coverId;

    @Column(name = "content_link")
    private String contentLink;

    @Column(name = "direct_link")
    private String directLink;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "price")
    private Long price;

    @Column(name = "discount")
    private Double discount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getAbstraction() {
//        return abstraction;
//    }
//
//    public void setAbstraction(String abstraction) {
//        this.abstraction = abstraction;
//    }
//
//    public String getOutline() {
//        return outline;
//    }
//
//    public void setOutline(String outline) {
//        this.outline = outline;
//    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public String getPrepare() {
//        return prepare;
//    }
//
//    public void setPrepare(String prepare) {
//        this.prepare = prepare;
//    }

    public Long getCoverId() {
        return coverId;
    }

    public void setCoverId(Long cover) {
        this.coverId = cover;
    }

    public String getContentLink() {
        return contentLink;
    }

    public void setContentLink(String cdnLink) {
        this.contentLink = cdnLink;
    }

    public String getDirectLink() {
        return directLink;
    }

    public void setDirectLink(String directLink) {
        this.directLink = directLink;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public static Map<String, Object> convertToMap(Scheduler scheduler, List<User> teachers, List<Image> covers, List<Object[]> likeCount, List<Object[]> likedCount, List<Object[]> readCount, List<Comment> comments, boolean isVIP) {
        Map<String, Object> schedulerMap = new HashMap<>();
        schedulerMap.put("id", scheduler.getId());
        schedulerMap.put("year", scheduler.getYear());
        schedulerMap.put("week", scheduler.getWeek());
        schedulerMap.put("day", scheduler.getDay());
        schedulerMap.put("startTime", scheduler.getStartTime());
        schedulerMap.put("endTime", scheduler.getEndTime());
        schedulerMap.put("subjectId", scheduler.getSubjectId());
        schedulerMap.put("grade", scheduler.getGrade());
        schedulerMap.put("name", scheduler.getName());
        //schedulerMap.put("abstraction", scheduler.getAbstraction());
        //schedulerMap.put("outline", scheduler.getOutline());
        schedulerMap.put("description", scheduler.getDescription());
        Image cover = Resources.findItem(covers, item -> item.getId() == scheduler.getCoverId());
        if (cover != null) {
            schedulerMap.put("cover", cover.getStorePath());
        }
        User teacher = JPAEntry.getObject(User.class,"id",scheduler.getTeacherId());
        if (teacher != null) {
            schedulerMap.put("teacher", teacher.getName());
            schedulerMap.put("teacherDescription", teacher.getDescription());
        }
        schedulerMap.put("price", scheduler.getPrice());
        schedulerMap.put("discount", scheduler.getDiscount());
        //schedulerMap.put("price", scheduler.getAmount());
        schedulerMap.put("likeCount", Resources.findUntypedItem(likeCount, scheduler.getId()));
        schedulerMap.put("readCount", Resources.findUntypedItem(readCount, scheduler.getId()));
        schedulerMap.put("comments", Resources.findItems(comments, c -> c.getObjectId() == scheduler.getId()));
        schedulerMap.put("liked", Resources.findUntypedItem(likedCount, scheduler.getId()) != null);
        if (isVIP) {
            schedulerMap.put("contentLink", scheduler.getContentLink());
            schedulerMap.put("directLink", scheduler.getDirectLink());
        }
        return schedulerMap;
    }

    public static Map<String, Object> convertToMap(Scheduler scheduler, Long userId) {
        Map<String, Object> schedulerMap = new HashMap<>();
        schedulerMap.put("id", scheduler.getId());
        schedulerMap.put("year", scheduler.getYear());
        schedulerMap.put("week", scheduler.getWeek());
        schedulerMap.put("day", scheduler.getDay());
        schedulerMap.put("startTime", scheduler.getStartTime());
        schedulerMap.put("endTime", scheduler.getEndTime());
        schedulerMap.put("subjectId", scheduler.getSubjectId());
        schedulerMap.put("grade", scheduler.getGrade());
        schedulerMap.put("name", scheduler.getName());
//        schedulerMap.put("abstraction", scheduler.getAbstraction());
//        schedulerMap.put("outline", scheduler.getOutline());
//        schedulerMap.put("description", scheduler.getDescription());
//        Image cover = JPAEntry.getObject(Image.class, "id", scheduler.getCoverId());
//        if (cover != null) {
//            schedulerMap.put("cover", cover.getStorePath());
//        }
//        User teacher = JPAEntry.getObject(User.class, "id", scheduler.getTeacherId());
//        if (teacher != null) {
//            schedulerMap.put("teacher", teacher.getName());
//            schedulerMap.put("teacherDescription", teacher.getDescription());
//        }
        schedulerMap.put("price", scheduler.getPrice());
        schedulerMap.put("discount", scheduler.getDiscount());
        //schedulerMap.put("price", scheduler.getAmount());
        schedulerMap.put("likeCount", 0);
        Long likeCount = Logs.getStatsCount("scheduler", scheduler.getId(), "like");
        if (likeCount != null) {
            schedulerMap.put("likeCount", likeCount);
        }
        schedulerMap.put("liked", Logs.has(userId, "scheduler", scheduler.getId(), "like"));
        schedulerMap.put("readCount", 0);
        Long readCount = Logs.getStatsCount("scheduler", scheduler.getId(), "read");
        if (readCount != null) {
            schedulerMap.put("readCount", readCount);
        }
        return schedulerMap;
    }

    @Override
    public Long getAmount() {
        return (long)(price * discount);
    }

    @Override
    public void setAmount(Long a) {
        //do nothing
    }

    @Override
    public Map<String, Object> getContent() {
        Map<String, Object> schedulerMap = new HashMap<>();
        schedulerMap.put("contentLink", getContentLink());
        schedulerMap.put("directLink", getDirectLink());
        return schedulerMap;
    }
}
