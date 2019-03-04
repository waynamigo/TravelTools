package com.example.traveltools.bean;

import java.io.Serializable;

public class SubComment implements Serializable{
    Integer commentId;
    Integer userId;
    String username;
    Integer ownerId;
    Integer wieboId;
    String content;
    String date;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Integer getWieboId() {
        return wieboId;
    }

    public void setWieboId(Integer wieboId) {
        this.wieboId = wieboId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
