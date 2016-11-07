package com.baremind.data;

import com.baremind.ProblemOptions;
import com.baremind.utils.JPAEntry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Column(name = "name")
    private String name;

    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "index")
    private Integer index;

    @Column(name = "video_id")
    private Long videoId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public static Map<String, Object> convertToMap(Problem problemItem, List<ProblemOption> problemOptions, List<ProblemStandardAnswer> problemStandardAnswers) {
        Map<String, Object> pm = new HashMap<>();
        pm.put("id", problemItem.getId());
        if (problemStandardAnswers.size() > 1) {
            pm.put("type", "多选题");
        } else {
            pm.put("type", "单选题");
        }
        List<Map<String, Object>> poms = ProblemOptions.convertProblemOptions(problemOptions);
        pm.put("options", poms);
        pm.put("standardAnswers", problemStandardAnswers);
        pm.put("name", problemItem.getName());
        Image image = JPAEntry.getObject(Image.class, "id", problemItem.getImageId());
        if (image != null) {
            pm.put("storePath", image.getStorePath());
        }
        Video video = JPAEntry.getObject(Video.class, "id", problemItem.getVideoId());
        if (video != null) {
            pm.put("videoUrl", video.getStorePath());
            Image cover = JPAEntry.getObject(Image.class, "id", video.getCover());
            if (cover != null) {
                pm.put("videoImage", cover.getStorePath());
            }
        }
        return pm;
    }
}
