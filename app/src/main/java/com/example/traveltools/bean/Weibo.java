package com.example.traveltools.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by waynamigo on 18-8-23.
 */

public class Weibo implements Serializable {
    private String content;
    private String date;
    private Integer userId;
    private String userName;
    private Integer weiboId;
    private String filename;

    public Weibo(String content, String date, Integer userId, String userName, Integer weiboId) {
        this.content = content;
        this.date = date;
        this.userId = userId;
        this.userName = userName;
        this.weiboId = weiboId;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getWeiboId() {
        return weiboId;
    }

    public void setWeiboId(Integer weiboId) {
        this.weiboId = weiboId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
