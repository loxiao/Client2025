package com.example.administrator.client2025;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.client2025.entity.News;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsPublishActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_back = null, btn_publish = null, btn_reset = null;
    private TextView tv_user = null;
    private EditText edt_news_title = null, edt_news_author = null, edt_news_content = null, edt_news_pic = null;
    private Spinner spinner_news_type = null;

    private List<String> newsTypeList = null;
    private ArrayAdapter<String> typeAdapter = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = ((String) msg.obj).trim();
            switch (msg.what) {
                case 500: // 发布新闻
                    if (result.contains("成功")) {
                        Toast.makeText(NewsPublishActivity.this, "新闻发布成功", Toast.LENGTH_SHORT).show();
                        // 返回新闻列表页面
                        Intent intent = new Intent(NewsPublishActivity.this, NewsListActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(NewsPublishActivity.this, "新闻发布失败：" + result, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_publish);

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_publish = (Button) findViewById(R.id.btn_publish);
        btn_reset = (Button) findViewById(R.id.btn_reset);

        btn_back.setOnClickListener(this);
        btn_publish.setOnClickListener(this);
        btn_reset.setOnClickListener(this);

        tv_user = (TextView) findViewById(R.id.tv_user);
        tv_user.setText("你好，" + MainActivity.admin.getA_name());

        edt_news_title = (EditText) findViewById(R.id.edt_news_title);
        edt_news_author = (EditText) findViewById(R.id.edt_news_author);
        edt_news_content = (EditText) findViewById(R.id.edt_news_content);
        edt_news_pic = (EditText) findViewById(R.id.edt_news_pic);

        spinner_news_type = (Spinner) findViewById(R.id.spinner_news_type);
        initNewsType();
    }

    // 初始化新闻类型列表
    private void initNewsType() {
        newsTypeList = new ArrayList<>();
        newsTypeList.add("国内新闻(1)");
        newsTypeList.add("国际新闻(2)");
        newsTypeList.add("科技新闻(3)");
        newsTypeList.add("财经新闻(4)");
        newsTypeList.add("体育新闻(5)");

        typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, newsTypeList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_news_type.setAdapter(typeAdapter);
    }

    // 获取选中的新闻类型ID
    private int getSelectedNewsTypeId() {
        String selected = spinner_news_type.getSelectedItem().toString();
        return Integer.parseInt(selected.substring(selected.indexOf("(") + 1, selected.indexOf(")")));
    }

    // 发布新闻
    private void publishNews() {
        String title = edt_news_title.getText().toString().trim();
        String author = edt_news_author.getText().toString().trim();
        String content = edt_news_content.getText().toString().trim();
        String pic = edt_news_pic.getText().toString().trim();
        int type = getSelectedNewsTypeId();

        if (title.isEmpty()) {
            Toast.makeText(this, "请输入新闻标题", Toast.LENGTH_SHORT).show();
            return;
        }

        if (author.isEmpty()) {
            Toast.makeText(this, "请输入作者", Toast.LENGTH_SHORT).show();
            return;
        }

        if (content.isEmpty()) {
            Toast.makeText(this, "请输入新闻内容", Toast.LENGTH_SHORT).show();
            return;
        }

        // 准备发送数据到服务器
        final OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("title", title)
                .add("content", content)
                .add("author", author)
                .add("type", String.valueOf(type))
                .add("pic", pic)
                .add("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                .build();

        final Request request = new Request.Builder()
                .url(MainActivity.serverUrl.newsPublish)
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
                        msg.what = 500;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = 500;
                    msg.obj = "网络请求失败";
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    // 重置表单
    private void resetForm() {
        edt_news_title.setText("");
        edt_news_author.setText("");
        edt_news_content.setText("");
        edt_news_pic.setText("");
        spinner_news_type.setSelection(0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_publish:
                publishNews();
                break;
            case R.id.btn_reset:
                resetForm();
                break;
        }
    }
}
