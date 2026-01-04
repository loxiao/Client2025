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

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class CateAddActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_back = null, btn_save = null, btn_reset = null;
    private EditText edt_cate_name = null, edt_cate_desc = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = ((String) msg.obj).trim();
            switch (msg.what) {
                case 200: // 添加分类
                    if (result.contains("成功")) {
                        Toast.makeText(CateAddActivity.this, "分类添加成功", Toast.LENGTH_SHORT).show();
                        // 返回分类列表页面
                        Intent intent = new Intent(CateAddActivity.this, CategoryActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(CateAddActivity.this, "分类添加失败：" + result, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cate_add);

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_reset = (Button) findViewById(R.id.btn_reset);

        btn_back.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_reset.setOnClickListener(this);

        edt_cate_name = (EditText) findViewById(R.id.edt_cate_name);
        edt_cate_desc = (EditText) findViewById(R.id.edt_cate_desc);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_save:
                String name = edt_cate_name.getText().toString().trim();
                String desc = edt_cate_desc.getText().toString().trim();

                if (name.length() < 1) {
                    Toast.makeText(this, "请输入分类名称", Toast.LENGTH_SHORT).show();
                } else {
                    AddCateByPost(name, desc);
                }
                break;
            case R.id.btn_reset:
                edt_cate_name.setText("");
                edt_cate_desc.setText("");
                break;
        }
    }

    // 以POST方式向服务器请求添加分类
    private void AddCateByPost(String name, String desc) {
        final OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("c_name", name)
                .add("c_desc", desc)
                .build();

        final Request request = new Request.Builder()
                .url(MainActivity.serverUrl.cateAdd)
                .post(body)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String result = response.body().string().trim();

                        Message msg = Message.obtain();
                        msg.what = 200;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = 200;
                    msg.obj = "网络请求失败";
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
}