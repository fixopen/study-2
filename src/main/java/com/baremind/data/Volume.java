package com.baremind.data;

import com.baremind.utils.JPAEntry;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo on 2016/8/18.
 */
@Entity
@Table(name = "volumes")
public class Volume implements com.baremind.data.Entity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "name")
    private String name;

    @Column(name = "cover_id")
    private Long coverId;

    @Column(name = "\"order\"")
    private Integer order;

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

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
    }

    public Long getCoverId() {
        return coverId;
    }

    public void setCoverId(Long imageId) {
        this.coverId = imageId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public static Map<String, Object> convertToMap(Volume volume, Date now, Date yesterday) {
        Map<String, Object> vm = new HashMap<>();
        vm.put("id", volume.getId());
        vm.put("grade", volume.getGrade());
        vm.put("order", volume.getOrder());
        vm.put("subjectId", volume.getSubjectId());
        vm.put("coverId", volume.getCoverId());
        vm.put("name", volume.getName());
        vm.put("type", "old");
        EntityManager em = JPAEntry.getEntityManager();
        String stats = "SELECT COUNT(l) FROM KnowledgePoint l WHERE l.volumeId = :volumeId AND l.showTime > :yesterday AND l.showTime < :now";
        Query q = em.createQuery(stats, Long.class);
        q.setParameter("volumeId", volume.getId());
        q.setParameter("yesterday", yesterday);
        q.setParameter("now", now);
        Long count = (Long) q.getSingleResult();
        if (count > 0) {
            vm.put("type", "new");
        }
        return vm;
    }
}
