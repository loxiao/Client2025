package com.example.administrator.client2025;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.client2025.entity.Category;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class CateEditActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_back = null, btn_save = null, btn_reset = null;
    private TextView tv_cate_id = null;
    private EditText edt_cate_name = null, edt_cate_desc = null;
    private Category originalCategory = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = ((String) msg.obj).trim();
            switch (msg.what) {
                case 201: // 编辑分类
                    if (result.contains("成功")) {
                        Toast.makeText(CateEditActivity.this, "分类编辑成功", Toast.LENGTH_SHORT).show();
                        // 返回分类列表页面
                        Intent intent = new Intent(CateEditActivity.this, CategoryActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(CateEditActivity.this, "分类编辑失败：" + result, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cate_edit);

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_reset = (Button) findViewById(R.id.btn_reset);

        btn_back.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_reset.setOnClickListener(this);

        tv_cate_id = (TextView) findViewById(R.id.tv_cate_id);
        edt_cate_name = (EditText) findViewById(R.id.edt_cate_name);
        edt_cate_desc = (EditText) findViewById(R.id.edt_cate_desc);

        // 获取传递过来的分类对象
        originalCategory = (Category) getIntent().getSerializableExtra("category");
        if (originalCategory != null) {
            // 填充当前分类数据
            populateCategoryData(originalCategory);
        }
    }

    // 填充分类数据到界面
    private void populateCategoryData(Category category) {
        tv_cate_id.setText(String.valueOf(category.getC_id()));
        edt_cate_name.setText(category.getC_name());
        edt_cate_desc.setText(category.getC_desc());
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
                } else if (originalCategory != null) {
                    EditCateByPost(originalCategory.getC_id(), name, desc);
                }
                break;
            case R.id.btn_reset:
                if (originalCategory != null) {
                    populateCategoryData(originalCategory);
                }
                break;
        }
    }

    // 以POST方式向服务器请求编辑分类
    private void EditCateByPost(int id, String name, String desc) {
        final OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("c_id", String.valueOf(id))
                .add("c_name", name)
                .add("c_desc", desc)
                .build();

        final Request request = new Request.Builder()
                .url(MainActivity.serverUrl.cateEdit)
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
                        msg.what = 201;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = 201;
                    msg.obj = "网络请求失败";
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
}