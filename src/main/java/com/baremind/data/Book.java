package com.baremind.data;

import com.baremind.Logs;
import com.baremind.Resources;
import com.baremind.utils.JPAEntry;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by fixopen on 15/11/2016.
 */
@Entity
@Table(name = "books")
public class Book implements Resource {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "subject_no")
    private String subjectNo;

    @Column(name = "grade_no")
    private String gradeNo;

    @Column(name = "book_no")
    private String bookNo;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubjectNo() {
        return subjectNo;
    }

    public void setSubjectNo(String subjectNo) {
        this.subjectNo = subjectNo;
    }

    public String getGradeNo() {
        return gradeNo;
    }

    public void setGradeNo(String gradeNo) {
        this.gradeNo = gradeNo;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Map<String, Object> convertToMap(Book book, Long userId) {
        Map<String, Object> vm = new HashMap<>();
        vm.put("id", book.getId());
        vm.put("subjectNo", book.getSubjectNo());
        vm.put("gradeNo", book.getGradeNo());
        vm.put("bookNo", book.getBookNo());
        vm.put("name", book.getName());
        vm.put("description", book.getDescription());
        Map<String, Object> condition = new HashMap<>();
        condition.put("bookId", book.getId());
        Map<String, String> orders = new HashMap<>();
        orders.put("pageNo", "ASC");
        orders.put("unitNo", "ASC");
        vm.put("records", JPAEntry.getList(AudioRecord.class, condition, orders));
        vm.put("readCount", Logs.getStatsCount("book", book.getId(), "read"));
        vm.put("likeCount", Logs.getStatsCount("book", book.getId(), "like"));
        if (userId != null) {
            vm.put("liked", Logs.has(userId, "book", book.getId(), "like"));
        }
        Map<String, Object> commentCondition = new HashMap<>();
        commentCondition.put("objectType", "book");
        commentCondition.put("objectId", book.getId());
        List<Comment> comments = JPAEntry.getList(Comment.class, commentCondition);
        List<String> ownerIds = comments.stream().map(c -> c.getUserId().toString()).collect(Collectors.toList());
        EntityManager em = JPAEntry.getEntityManager();
        List<User> owners = Resources.getList(em, ownerIds, User.class);
        List<Map<String, Object>> commentsMap = comments.stream().map(c -> Comment.convertToMap(c, owners)).collect(Collectors.toList());
        vm.put("comments", commentsMap);
        return vm;
    }

    public static Map<String, Object> convertToMap(Book book, String pageNo, Long userId) {
        Map<String, Object> vm = new HashMap<>();
        vm.put("id", book.getId());
        vm.put("subjectNo", book.getSubjectNo());
        vm.put("gradeNo", book.getGradeNo());
        vm.put("bookNo", book.getBookNo());
        vm.put("name", book.getName());
        vm.put("description", book.getDescription());
        Map<String, Object> condition = new HashMap<>();
        condition.put("bookId", book.getId());
        condition.put("pageNo", pageNo);
        Map<String, String> orders = new HashMap<>();
        orders.put("unitNo", "ASC");
        vm.put("records", JPAEntry.getList(AudioRecord.class, condition, orders));
        vm.put("readCount", Logs.getStatsCount("book", book.getId(), "read"));
        vm.put("likeCount", Logs.getStatsCount("book", book.getId(), "like"));
        if (userId != null) {
            vm.put("liked", Logs.has(userId, "book", book.getId(), "like"));
        }
        Map<String, Object> commentCondition = new HashMap<>();
        commentCondition.put("objectType", "book");
        commentCondition.put("objectId", book.getId());
        List<Comment> comments = JPAEntry.getList(Comment.class, commentCondition);
        List<String> ownerIds = comments.stream().map(c -> c.getUserId().toString()).collect(Collectors.toList());
        EntityManager em = JPAEntry.getEntityManager();
        List<User> owners = Resources.getList(em, ownerIds, User.class);
        List<Map<String, Object>> commentsMap = comments.stream().map(c -> Comment.convertToMap(c, owners)).collect(Collectors.toList());
        vm.put("comments", commentsMap);
        return vm;
    }

    @Override
    public Long getSubjectId() {
        Long result = null;
        Subject s = JPAEntry.getObject(Subject.class, "no", getSubjectNo());
        if (s != null) {
            result = s.getId();
        }
        return result;
    }

    @Override
    public Long getAmount() {
        return 0L;
    }

    @Override
    public void setAmount(Long a) {
        //do nothing
    }

    @Override
    public Map<String, Object> getContent() {
        Map<String, Object> schedulerMap = new HashMap<>();
        schedulerMap.put("link", "p-zhibo-" + getSubjectNo() + getGradeNo() + "/" + getBookNo() + ".mp4");
        return schedulerMap;
    }
}
