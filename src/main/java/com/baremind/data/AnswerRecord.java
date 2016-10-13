package com.baremind.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by User on 2016/9/27.
 */
@Entity
@Table(name = "answer_records")
public class AnswerRecord {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "answer")
    private Long answer;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "problem_id")
    private Long problemId;

    @Column(name = "commit_time")
    private Date commitTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnswer() {
        return answer;
    }

    public void setAnswer(Long answer) {
        this.answer = answer;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public Date getCommitTime() {
        return commitTime;
    }

    public void setCommitTime(Date commitTime) {
        this.commitTime = commitTime;
    }
}
