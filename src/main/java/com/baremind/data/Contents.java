package com.baremind.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by User on 2016/9/10.
 */
@Entity
@Table(name = "contents")
public class Contents {
    @Id
    @Column(name = "id")

    private Long id;

    @Column(name = "konwledge_point_id")
    private Long konwledgePointId;

    @Column(name = "volimes_id")
    private Long volimesId;

    @Column(name = "type")
    private String type;

    @Column(name = "content")
    private String content;

    @Column(name = "order")
    private Long order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getKonwledgePointId() {
        return konwledgePointId;
    }

    public void setKonwledgePointId(Long konwledgePointId) {
        this.konwledgePointId = konwledgePointId;
    }

    public Long getVolimesId() {
        return volimesId;
    }

    public void setVolimesId(Long volimesId) {
        this.volimesId = volimesId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }
}
