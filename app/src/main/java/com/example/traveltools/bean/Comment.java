package com.example.traveltools.bean;

import java.io.Serializable;

/**
 * Created by waynamigo on 18-8-7.
 */

public class Comment implements Serializable{
    String username;
    String content;
    Integer weiboId;
    Integer userId;
    String publish_time;
    Integer commentcount;
    String headpicurl;

    Integer secondId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getWeiboId() {
        return weiboId;
    }

    public void setWeiboId(Integer weiboId) {
        this.weiboId = weiboId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCommentcount() {
        return commentcount;
    }

    public void setCommentcount(Integer commentcount) {
        this.commentcount = commentcount;
    }

    public String getHeadpicurl() {
        return headpicurl;
    }

    public void setHeadpicurl(String headpicurl) {
        this.headpicurl = headpicurl;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public Integer getSecondId() {
        return secondId;
    }

    public void setSecondId(Integer secondId) {
        this.secondId = secondId;
    }
}
