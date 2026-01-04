package com.example.administrator.client2025.entity;

public class ServerUrl {

    public String  host;
    public String  picPath;

    public String  login;
    public String  addPic;
    public String  getAllAdmin;
    public String  registerAdmin;
    public String  editAdmin;
    public String  deleteAdmin;
    public String  newsList;
    public String  newsDetail;
    public String  newsPublish;
    public String  newsEdit;
    public String  newsDelete;
    public String  cateList;
    public String  cateAdd;
    public String  cateEdit;
    public String  cateDelete;

    public ServerUrl() {
        // 默认使用本地服务器地址
        changeUrl("http://192.168.203.57:8080");
    }

    public void changeUrl(String ip) {
        // 确保IP地址包含http://前缀
        if (!ip.startsWith("http://")) {
            ip = "http://" + ip;
        }
        host			=ip;
        picPath         =host+"/upimages/";

        login           =host+"/LoginServlet";
        addPic          =host+"/admin/AddPic";
        getAllAdmin     =host+"/admin/GetAll";
        registerAdmin   =host+"/admin/Register";
        editAdmin       =host+"/admin/Edit";
        deleteAdmin     =host+"/admin/Delete";
        newsList        =host+"/news/list";
        newsDetail      =host+"/news/detail";
        newsPublish     =host+"/news/publish";
        newsEdit        =host+"/news/update";
        newsDelete      =host+"/news/delete";
        cateList        =host+"/category/list";
        cateAdd         =host+"/category/add";
        cateEdit        =host+"/category/edit";
        cateDelete      =host+"/category/delete";
    }

}