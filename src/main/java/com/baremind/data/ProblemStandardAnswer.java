package com.baremind.data;

import com.baremind.utils.JPAEntry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

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

    @Column(name = "index")
    private Integer index;

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

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer name) {
        this.index = name;
    }

    public static Map<String, Object> convertToMap(ProblemStandardAnswer o) {
        Map<String, Object> psa = new HashMap<>();
        psa.put("id", o.getId());
        psa.put("problemId", o.getProblemId());
        psa.put("index", o.getIndex());
        return psa;
    }
}
