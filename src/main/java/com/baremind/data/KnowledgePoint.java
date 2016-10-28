package com.baremind.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by lenovo on 2016/8/18.
 */
@Entity
@Table(name = "knowledge_points")
public class KnowledgePoint {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "volume_id")
    private Long volumeId;

    @Column(name = "name")
    private String name;

    @Column(name = "\"order\"")
    private int order;

    @Column(name = "show_time")
    private Date showTime;

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
}
