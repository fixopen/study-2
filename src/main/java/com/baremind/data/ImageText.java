package com.baremind.data;

import com.baremind.utils.JPAEntry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 2016/9/20.
 */
@Entity
@Table(name = "image_texts")
public class ImageText implements com.baremind.data.Entity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "content")
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static Map<String, Object> convertToMap(ImageText it) {
        Map<String, Object> itm = new HashMap<>();
        itm.put("id", it.getId());
        itm.put("type", "imageText");
        itm.put("content", it.getContent());
        Image image = JPAEntry.getObject(Image.class, "id", it.getImageId());
        itm.put("href", image.getStorePath());
        return itm;
    }
}
