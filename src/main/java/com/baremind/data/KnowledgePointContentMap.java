package com.baremind.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by User on 2016/9/18.
 */
@Entity
@Table(name = "knowledge_point_content_maps")
public class KnowledgePointContentMap implements com.baremind.data.Entity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "knowledge_point_id")
    private Long knowledgePointId;

    @Column(name = "volume_id")
    private Long volumeId;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "grade")
    private Long grade;

    @Column(name = "type")
    private String type;


    @Column(name = "\"order\"")
    private int order;

    @Column(name = "object_id")
    private Long objectId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getKnowledgePointId() {
        return knowledgePointId;
    }

    public void setKnowledgePointId(Long knowledgePointId) {
        this.knowledgePointId = knowledgePointId;
    }

    public Long getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(Long volumeId) {
        this.volumeId = volumeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getGrade() {
        return grade;
    }

    public void setGrade(Long grade) {
        this.grade = grade;
    }
}
