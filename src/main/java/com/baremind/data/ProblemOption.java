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
@Table(name = "problem_options")
public class ProblemOption implements com.baremind.data.Entity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "problem_id")
    private Long problemId;

    @Column(name = "name")
    private String name;

    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "index")
    private Integer index;

    @Column(name = "\"order\"")
    private Integer order;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public static Map<String, Object> convertToMap(ProblemOption o) {
        Map<String, Object> pom = new HashMap<>();
        pom.put("id", o.getId());
        pom.put("name", o.getName());
        Image image = JPAEntry.getObject(Image.class, "id", o.getImageId());
        if (image != null) {
            pom.put("image", image);
            pom.put("index", o.getIndex());
        }
        pom.put("order", o.getOrder());
        return pom;
    }
}
