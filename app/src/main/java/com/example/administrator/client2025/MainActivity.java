package com.example.administrator.client2025;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.example.administrator.client2025.entity.Admin;
import com.example.administrator.client2025.entity.ServerUrl;

import java.io.IOException;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public  static ServerUrl serverUrl = null;           //---系统使用的服务器链接网址
    public  static Admin admin = null;               //---登录成功后的登录账号

    private Button      btn_login = null,   btn_cancel = null;
    private EditText    edt_ip = null,      edt_uname = null,       edt_upwd = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (admin != null) admin = null;

            String result = (String) msg.obj;
            String str="";
            switch (msg.what) {
                case 100:       //...........登录
                    try {
                        admin = JSON.parseObject(result, Admin.class);
                        int  state=admin.getA_state();
                        switch (state){
                            case  4:
                                Intent tt = new Intent(MainActivity.this, UsersActivity.class);
                                tt.putExtra("result",result);
                                tt.putExtra("ip",    edt_ip.getText().toString().trim());
                                startActivity(tt);
                                finish();
                                break;
                            case 3:
                                str="密码不匹配！！";
                                break;
                            case 2:
                                str="用户名不存在！！";
                                break;
                        }
                    } catch (Exception e) {
                        str="网络连接出现问题！！";
                    }

                    if(str.trim().length()>0)
                        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_login.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        edt_ip = (EditText) findViewById(R.id.edt_ip);
        edt_uname = (EditText) findViewById(R.id.edt_uname);
        edt_upwd = (EditText) findViewById(R.id.edt_upwd);

        // 确保serverUrl在应用启动时就正确初始化
        if (serverUrl == null) {
            serverUrl = new ServerUrl();
        }
        // 默认填充一个常用的本地服务器地址，方便用户使用
        edt_ip.setText("192.168.203.57:8080");
    }

    @Override
    public void onClick(View v) {
        if (admin != null)
            admin = null;

        String ip=edt_ip.getText().toString().trim();
        String ss="";

        if(ip.length()>0) {
            serverUrl.changeUrl(ip);

            int id = v.getId();
            switch (id) {
                case R.id.btn_login:
                    String name = edt_uname.getText().toString().trim();
                    String pwd  = edt_upwd.getText().toString().trim();

                    if(name.length()<1)         ss="请输入账号！！";
                    else if(pwd.length()<1)     ss="请输入密码！！";
                    else {
                        LoginByPost(name,pwd);
                    }

                    if(ss.length()>0)
                        Toast.makeText(this,ss ,Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_cancel:
                    edt_uname.setText("");
                    edt_upwd.setText("");

                    break;
            }
        }
        else
            Toast.makeText(this,"请输入服务器的IP地址！！",Toast.LENGTH_SHORT).show();
    }


    //---以post方式向服务器发送登录请求
    private void LoginByPost(String name,String pwd) {

        final OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("a_name", name)            //---采用键值对形式，向服务器传递数据
                .add("a_pwd",  pwd)
                .build();                       //---生成body对象

        final Request request = new Request.Builder()
                .url(serverUrl.login)           //---发送的目的地网址
                .post(body)                     //---post方式发送的具体内容
                .build();                       //---生成request对象

        new Thread(new Runnable() {             //---生成新的线程
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();   //---发送数据并得到回复
                    if (response.isSuccessful()) {
                        String  result=response.body().string().trim();

                        Message msg=Message.obtain();
                        msg.what=100;
                        msg.obj=result;
                        handler.sendMessage(msg);
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();         //---启动该线程
    }
}