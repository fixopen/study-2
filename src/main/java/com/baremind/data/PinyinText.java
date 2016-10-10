package com.baremind.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
}
