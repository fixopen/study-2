package com.baremind.data;

import com.baremind.Logs;
import com.baremind.utils.JPAEntry;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2016/8/18.
 */
@Entity
@Table(name = "knowledge_points")
public class KnowledgePoint implements com.baremind.data.Entity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "volume_id")
    private Long volumeId;

    @Column(name = "price")
    private Long price;

    @Column(name = "name")
    private String name;

    @Column(name = "\"order\"")
    private int order;

    @Column(name = "show_time")
    private Date showTime;

    @Column(name = "discount")
    private double discount;

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(Long volumeId) {
        this.volumeId = volumeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Date getShowTime() {
        return showTime;
    }

    public void setShowTime(Date show) {
        showTime = show;
    }

    public static Map<String, Object> convertToMap(KnowledgePoint kp, Date now, Date yesterday) {
        Map<String, Object> kpm = new HashMap<>();
        kpm.put("id", kp.getId());
        kpm.put("volumeId", kp.getVolumeId());
        kpm.put("name", kp.getName());
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
        String query = "SELECT m.objectType FROM KnowledgePointContentMap m GROUP BY m.objectType";
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
