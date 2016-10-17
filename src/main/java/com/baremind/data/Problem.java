package com.baremind.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by lenovo on 2016/8/18.
 */
//@Struct(name = "problems")
@Entity
@Table(name = "problems")
public class Problem {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "volume_id")
    private Long volumeId;

    @Column(name = "knowledge_point_id")
    private Long knowledgePointId;

    @Column(name = "title")
    private String title;

    @Column(name = "store_path")
    private String storePath;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "video_image")
    private Long videoImage;

    @Column(name = "index")
    private Long index;

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public Long getVideoImage() {
        return videoImage;
    }

    public void setVideoImage(Long videoImage) {
        this.videoImage = videoImage;
    }

    //{"videoUrl":"/data","storePath":"d:/1474270688455.jpeg","standardAnswers":[0,1],"options":["das","asdf","afds","adf"],"title":"dfas","knowledgePointId":5,"volumeId":1,"subjectId":1}
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(Long volumeId) {
        this.volumeId = volumeId;
    }

    public Long getKnowledgePointId() {
        return knowledgePointId;
    }

    public void setKnowledgePointId(Long knowledgePointId) {
        this.knowledgePointId = knowledgePointId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

}
