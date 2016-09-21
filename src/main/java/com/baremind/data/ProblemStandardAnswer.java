package com.baremind.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by User on 2016/9/19.
 */
@Entity
@Table(name = "problems_standard_answers")
public class ProblemStandardAnswer {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "problems_id")
    private Long problemsId;

    @Column(name = "name")
    private Long name;

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

    public Long getName() {
        return name;
    }

    public void setName(Long name) {
        this.name = name;
    }
}
