package com.baremind.data;

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
@Table(name = "pinyin_texts")
public class PinyinText {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "pinyin")
    private String pinyin;

    @Column(name = "content")
    private String content;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static Map<String, Object> convertToMap(PinyinText pt) {
        Map<String, Object> ptm = new HashMap<>();
        ptm.put("id", pt.getId());
        ptm.put("type", "pinyinText");
        ptm.put("pinyin", pt.getPinyin());
        ptm.put("content", pt.getContent());
        return ptm;
    }
}
