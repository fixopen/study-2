package com.baremind.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by User on 2016/9/19.
 */
@Entity
@Table(name = "problem_standard_answers")
public class ProblemStandardAnswer implements com.baremind.data.Entity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "problem_id")
    private Long problemId;

    @Column(name = "name")
    private Long name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public Long getName() {
        return name;
    }

    public void setName(Long name) {
        this.name = name;
    }
}
