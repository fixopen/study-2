package com.baremind.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 2016/9/18.
 */
@Entity
@Table(name = "texts")
public class Text implements com.baremind.data.Entity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "content")
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static Map<String, Object> convertToMap(Text t) {
        Map<String, Object> tm = new HashMap<>();
        tm.put("id", t.getId());
        tm.put("content", t.getContent());
        tm.put("type", "text");
        return tm;
    }
}
