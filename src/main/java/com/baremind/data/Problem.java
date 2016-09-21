package com.baremind.data;

import org.eclipse.persistence.annotations.Array;
import org.eclipse.persistence.annotations.Struct;

import javax.persistence.*;
import java.util.List;

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

   /* //@Column(name = "options")
    @Array(databaseType="text[]")
    private List<String> options;*/

//    @SuppressWarnings("JpaAttributeTypeInspection")
//    @Column(name = "options", columnDefinition = "name[]")
//    @Convert(converter = ListToArrayConverter.class)
//    private List<String> options;

//    @Transient
//    private String[] transferOptions;

//    void f() {
//        if (object instanceof UUID) {
//            PGobject pg = new PGobject();
//            pg.setType("uuid");
//            try {
//                pg.setValue(object.toString());
//            } catch (SQLException e) {
//                logger.error("Failed to convert value: " + object, e);
//            }
//            return pg;
//        }
//        return null;
//    }

    /*@Column(name = "standard_answers")
    private int[] standardAnswers;*/

    /*@Column(name = "order")
    private int order;*/

    @Column(name = "store_path")
    private String storePath;

    @Column(name = "video_url")
    private String videoUrl;

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

   /* public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int[] getStandardAnswers() {
        return standardAnswers;
    }

    public void setStandardAnswers(int[] standardAnswers) {
        this.standardAnswers = standardAnswers;
    }
*/
    /* public int getOrder() {
         return order;
     }

     public void setOrder(int order) {
         this.order = order;
     }
 */
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
