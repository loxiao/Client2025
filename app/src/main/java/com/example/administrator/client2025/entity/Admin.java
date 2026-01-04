package com.example.administrator.client2025.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by yuanh on 2021/11/20.
 */
public class Admin {
    @JSONField(ordinal = 1)
    private int         a_id;
    @JSONField(ordinal = 2)
    private String      a_name;
    @JSONField(ordinal = 3)
    private String      a_pwd;
    @JSONField(ordinal = 4)
    private String      a_phone;
    @JSONField(ordinal = 5)
    private String      a_pic;
    @JSONField(ordinal = 6)
    private int  a_state;           //---0:默认初值；  1：用户名重复；    2：用户名不存在；   3：密码不匹配   4：OK
    //---5:图片发生改变

    public Admin() {
    }

    public Admin(int a_id, String a_name, String a_pwd, String a_phone, String a_pic, int a_state) {
        this.a_id = a_id;
        this.a_name = a_name;
        this.a_pwd = a_pwd;
        this.a_phone = a_phone;
        this.a_pic = a_pic;
        this.a_state = a_state;
    }

    public int getA_id() {
        return a_id;
    }

    public void setA_id(int a_id) {
        this.a_id = a_id;
    }

    public String getA_name() {
        return a_name;
    }

    public void setA_name(String a_name) {
        this.a_name = a_name;
    }

    public String getA_pwd() {
        return a_pwd;
    }

    public void setA_pwd(String a_pwd) {
        this.a_pwd = a_pwd;
    }

    public String getA_phone() {
        return a_phone;
    }

    public void setA_phone(String a_phone) {
        this.a_phone = a_phone;
    }

    public String getA_pic() {
        return a_pic;
    }

    public void setA_pic(String a_pic) {
        this.a_pic = a_pic;
    }

    public int getA_state() {
        return a_state;
    }

    public void setA_state(int a_state) {
        this.a_state = a_state;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"a_id\":")
                .append(a_id);
        sb.append(",\"a_name\":\"")
                .append(a_name).append('\"');
        sb.append(",\"a_pwd\":\"")
                .append(a_pwd).append('\"');
        sb.append(",\"a_phone\":\"")
                .append(a_phone).append('\"');
        sb.append(",\"a_pic\":\"")
                .append(a_pic).append('\"');
        sb.append(",\"a_state\":")
                .append(a_state);
        sb.append('}');
        return sb.toString();
    }
}