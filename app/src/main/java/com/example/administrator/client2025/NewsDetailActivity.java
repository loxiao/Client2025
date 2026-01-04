package com.example.administrator.client2025;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.administrator.client2025.entity.News;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_back = null, btn_back = null;
    private TextView news_detail_title = null;
    private TextView news_detail_author = null;
    private TextView news_detail_time = null;
    private TextView news_detail_type = null;
    private ImageView news_detail_pic = null;
    private TextView news_detail_content = null;

    private int newsId = -1;
    private String newsTitle = "";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = ((String) msg.obj).trim();
            switch (msg.what) {
                case 400: // 获取新闻详情
                    if (result.contains("{")) {
                        News news = JSON.parseObject(result, News.class);
                        ShowNewsDetail(news);
                    } else {
                        Toast.makeText(NewsDetailActivity.this, "获取新闻详情失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        tv_back = (TextView) findViewById(R.id.tv_back);
        btn_back = (TextView) findViewById(R.id.btn_back);
        tv_back.setOnClickListener(this);
        btn_back.setOnClickListener(this);

        news_detail_title = (TextView) findViewById(R.id.news_detail_title);
        news_detail_author = (TextView) findViewById(R.id.news_detail_author);
        news_detail_time = (TextView) findViewById(R.id.news_detail_time);
        news_detail_type = (TextView) findViewById(R.id.news_detail_type);
        news_detail_pic = (ImageView) findViewById(R.id.news_detail_pic);
        news_detail_content = (TextView) findViewById(R.id.news_detail_content);

        // 获取从新闻列表传递过来的新闻ID和标题
        Intent intent = getIntent();
        if (intent != null) {
            newsId = intent.getIntExtra("newsId", -1);
            newsTitle = intent.getStringExtra("newsTitle");
            news_detail_title.setText(newsTitle);

            if (newsId != -1) {
                GetNewsDetailByGet(newsId);
            }
        }
    }

    // 以GET方式向服务器请求新闻详情
    private void GetNewsDetailByGet(int id) {
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(MainActivity.serverUrl.newsDetail + "?id=" + id)
                .get()
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
                        msg.what = 400;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // 显示新闻详情
    private void ShowNewsDetail(News news) {
        if (news != null) {
            news_detail_title.setText(news.getN_title());
            news_detail_author.setText("作者：" + news.getN_author());
            news_detail_time.setText("发布时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(news.getN_time()));
            news_detail_type.setText("类型：" + getNewsTypeName(news.getN_type()));
            news_detail_content.setText(news.getN_content());

            // 使用Picasso加载网络图片
            String picPath = MainActivity.serverUrl.picPath + news.getN_pic();
            Picasso.with(this).load(picPath).into(news_detail_pic);
        }
    }

    // 将新闻类型ID转换为中文名称
    private String getNewsTypeName(int typeId) {
        switch (typeId) {
            case 1:
                return "国内新闻";
            case 2:
                return "国际新闻";
            case 3:
                return "科技新闻";
            case 4:
                return "财经新闻";
            case 5:
                return "体育新闻";
            default:
                return "其他新闻";
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_back:
            case R.id.btn_back:
                finish();
                break;
        }
    }
}