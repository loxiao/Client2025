package com.example.administrator.client2025.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class Category implements Serializable {
    @JSONField(ordinal = 1)
    private int c_id;         // 类型ID
    @JSONField(ordinal = 2)
    private String c_name;    // 类型名称
    @JSONField(ordinal = 3)
    private String c_desc;    // 类型描述

    // 构造方法
    public Category() {}

    public Category(int c_id, String c_name, String c_desc) {
        this.c_id = c_id;
        this.c_name = c_name;
        this.c_desc = c_desc;
    }

    // getter和setter
    public int getC_id() { return c_id; }
    public void setC_id(int c_id) { this.c_id = c_id; }
    public String getC_name() { return c_name; }
    public void setC_name(String c_name) { this.c_name = c_name; }
    public String getC_desc() { return c_desc; }
    public void setC_desc(String c_desc) { this.c_desc = c_desc; }

    @Override
    public String toString() {
        return "Category{" +
                "c_id=" + c_id +
                ", c_name='" + c_name + '\'' +
                ", c_desc='" + c_desc + '\'' +
                '}';
    }
}
