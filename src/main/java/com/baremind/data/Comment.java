package com.baremind.data;

import com.baremind.Logs;
import com.baremind.utils.JPAEntry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo on 2016/8/17.
 */
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "object_type")
    private String objectType;

    @Column(name = "object_id")
    private Long objectId;

    @Column(name = "content")
    private String content;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public static Map<String, Object> convertToMap(Comment comment) {
        Map<String, Object> commentMap = new HashMap<>();
        commentMap.put("id", comment.getId());
        commentMap.put("content", comment.getContent());
        SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        commentMap.put("createTime", time.format(comment.getCreateTime()));
        commentMap.put("objectId", comment.getObjectId());
        commentMap.put("objectType", comment.getObjectType());
        commentMap.put("updateTime", comment.getUpdateTime());
        commentMap.put("userId", comment.getUserId());
        User user = JPAEntry.getObject(User.class, "id", comment.getUserId());
        if (user != null) {
            commentMap.put("userName", user.getName());
            commentMap.put("userAvatar", user.getHead());
        }
        commentMap.put("likeCount", Logs.getStatsCount("comment", comment.getId(), "like"));
        return commentMap;
    }
}
