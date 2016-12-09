package com.baremind.data;

import com.baremind.Logs;
import com.baremind.utils.JPAEntry;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Entity;
/**
 * Created by lenovo on 2016/8/18.
 */
@Entity
@Table(name = "knowledge_points")
public class KnowledgePoint {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "volume_id")
    private Long volumeId;

    @Column(name = "grade")
    private int grade;

    @Column(name = "title")
    private String title;

    @Column(name = "\"order\"")
    private int order;

    @Column(name = "store_path")
    private String storePath;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "show_time")
    private Date showTime;

    public Date getShowTime() {
        return showTime;
    }

    public void setShowTime(Date showTime) {
        this.showTime = showTime;
    }

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

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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


    public static Map<String, Object> convertToMap(KnowledgePoint kp, Date now, Date yesterday) {
        Map<String, Object> kpm = new HashMap<>();
        kpm.put("id", kp.getId());
        kpm.put("volumeId", kp.getVolumeId());
        kpm.put("name", kp.getTitle());
        kpm.put("showTime", kp.getShowTime());
        kpm.put("likeCount", 0);
        String stateType = "old";

        EntityManager em = JPAEntry.getEntityManager();
        String stats = "SELECT COUNT(l) FROM KnowledgePoint l WHERE l.volumeId = :volumeId AND l.id = :id  AND l.showTime > :yesterday AND l.showTime < :now";
        TypedQuery<Long> q = em.createQuery(stats, Long.class);
        q.setParameter("volumeId", kp.getVolumeId());
        q.setParameter("id", kp.getId());
        q.setParameter("yesterday", yesterday);
        q.setParameter("now", now);
        Long count = q.getSingleResult();
        if (count > 0) {
            stateType = "new";
        }

        Long likeCount = Logs.getStatsCount("knowledge-point", kp.getId(), "like");
        if (likeCount != null) {
            kpm.put("likeCount", likeCount);
        }
        kpm.put("readCount", 0);
        Long readCount = Logs.getStatsCount("knowledge-point", kp.getId(), "read");
        if (readCount != null) {
            kpm.put("readCount", readCount);
        }
        String type = "normal";
        String query = "SELECT m.type FROM KnowledgePointContentMap m GROUP BY m.type";
        TypedQuery<String> pq = em.createQuery(query, String.class);
        List<String> sl = pq.getResultList();
        if (sl.size() == 1) {
            if (sl.get(0).equals("problem")) {
                type = "pk";
            }
        }
        switch (stateType) {
            case "old":
                switch (type) {
                    case "normal":
                        kpm.put("type", "normalOld");
                        break;
                    case "pk":
                        kpm.put("type", "pkOld");
                        break;
                }
                break;
            case "new":
                switch (type) {
                    case "normal":
                        kpm.put("type", "normalNew");
                        break;
                    case "pk":
                        kpm.put("type", "pkNew");
                        break;
                }
                break;
        }
        return kpm;
    }


}
