package com.baremind.data;

import com.baremind.utils.JPAEntry;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fixopen on 15/11/2016.
 */
@javax.persistence.Entity
@Table(name = "books")
public class Book implements com.baremind.data.Entity {
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

    public static Map<String, Object> convertToMap(Book book) {
        Map<String, Object> vm = new HashMap<>();
        vm.put("id", book.getId());
        vm.put("subjectNo", book.getSubjectNo());
        vm.put("gradeNo", book.getGradeNo());
        vm.put("bookNo", book.getBookNo());
        vm.put("name", book.getName());
        vm.put("records", JPAEntry.getList(AudioRecord.class, "bookId", book.getId()));
        return vm;
    }
}
