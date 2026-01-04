package com.example.administrator.client2025.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by yuanh on 2021/11/20.
 */
public class News implements Serializable {
    @JSONField(ordinal = 1)
    private int n_id;
    @JSONField(ordinal = 2)
    private String n_title;
    @JSONField(ordinal = 3)
    private String n_content;
    @JSONField(ordinal = 4)
    private String n_author;
    @JSONField(ordinal = 5, format = "yyyy-MM-dd HH:mm:ss")
    private Date n_time;
    @JSONField(ordinal = 6)
    private String n_pic;
    @JSONField(ordinal = 7)
    private int n_type;

    public News() {
    }

    public News(int n_id, String n_title, String n_content, String n_author, Date n_time, String n_pic, int n_type) {
        this.n_id = n_id;
        this.n_title = n_title;
        this.n_content = n_content;
        this.n_author = n_author;
        this.n_time = n_time;
        this.n_pic = n_pic;
        this.n_type = n_type;
    }

    public int getN_id() {
        return n_id;
    }

    public void setN_id(int n_id) {
        this.n_id = n_id;
    }

    public String getN_title() {
        return n_title;
    }

    public void setN_title(String n_title) {
        this.n_title = n_title;
    }

    public String getN_content() {
        return n_content;
    }

    public void setN_content(String n_content) {
        this.n_content = n_content;
    }

    public String getN_author() {
        return n_author;
    }

    public void setN_author(String n_author) {
        this.n_author = n_author;
    }

    public Date getN_time() {
        return n_time;
    }

    public void setN_time(Date n_time) {
        this.n_time = n_time;
    }

    public String getN_pic() {
        return n_pic;
    }

    public void setN_pic(String n_pic) {
        this.n_pic = n_pic;
    }

    public int getN_type() {
        return n_type;
    }

    public void setN_type(int n_type) {
        this.n_type = n_type;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append('"').append("n_id").append('"').append(':').append(n_id);
        sb.append(',');
        sb.append('"').append("n_title").append('"').append(":'").append(n_title).append('"');
        sb.append(',');
        sb.append('"').append("n_content").append('"').append(":'").append(n_content).append('"');
        sb.append(',');
        sb.append('"').append("n_author").append('"').append(":'").append(n_author).append('"');
        sb.append(',');
        sb.append('"').append("n_time").append('"').append(":'").append(n_time).append('"');
        sb.append(',');
        sb.append('"').append("n_pic").append('"').append(":'").append(n_pic).append('"');
        sb.append(',');
        sb.append('"').append("n_type").append('"').append(':').append(n_type);
        sb.append('}');
        return sb.toString();
    }
}