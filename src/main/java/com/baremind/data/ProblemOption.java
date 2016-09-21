package com.baremind.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by User on 2016/9/19.
 */
@Entity
@Table(name = "problems_options")
public class ProblemOption {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "problems_id")
    private Long problemsId;

    @Column(name = "name")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProblemsId() {
        return problemsId;
    }

    public void setProblemsId(Long problemsId) {
        this.problemsId = problemsId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
