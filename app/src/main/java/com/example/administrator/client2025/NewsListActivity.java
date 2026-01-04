package com.example.administrator.client2025;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.administrator.client2025.entity.News;
import com.example.administrator.client2025.utils.NewsAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewsListActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, NewsAdapter.OnNewsItemClickListener {

    private Button btn_publish = null, btn_refresh = null, btn_exit = null;
    private TextView tv_user = null;
    private ListView lv_news = null;
    private NewsAdapter adapter = null;
    private List<News> newsList = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = ((String) msg.obj).trim();
            String str = "";
            switch (msg.what) {
                case 300: // 获取新闻列表
                    if (result.contains("[")) {
                        ShowNewsListView(result);
                        str = "获取新闻列表---成功！！";
                    } else {
                        str = "获取新闻列表---不成功！！";
                    }
                    break;
                case 400: // 删除新闻
                    // 服务器返回"yes"表示成功，"no"表示失败
                    if (result.equals("yes")) {
                        GetAllNewsByGet(); // 刷新新闻列表
                        str = "删除新闻---成功！！";
                    } else {
                        str = "删除新闻---不成功！！";
                    }
                    break;
            }
            Toast.makeText(NewsListActivity.this, str, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        btn_publish = (Button) findViewById(R.id.btn_publish);
        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        btn_exit = (Button) findViewById(R.id.btn_exit);
        btn_publish.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        btn_exit.setOnClickListener(this);

        tv_user = (TextView) findViewById(R.id.tv_user);
        tv_user.setText("你好，" + MainActivity.admin.getA_name());

        lv_news = (ListView) findViewById(R.id.lv_news);
        lv_news.setOnItemClickListener(this);

        GetAllNewsByGet();
    }

    // 以GET方式向服务器请求新闻列表
    private void GetAllNewsByGet() {
        // 检查服务器URL是否正确初始化
        if (MainActivity.serverUrl == null || MainActivity.serverUrl.newsList == null) {
            Toast.makeText(this, "服务器地址未配置，请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        final OkHttpClient client = new OkHttpClient();
        final String requestUrl = MainActivity.serverUrl.newsList + "?page=1";
        final Request request = new Request.Builder()
                .url(requestUrl)
                .get()
                .build();

        // 记录请求URL以便调试
        android.util.Log.d("NewsListActivity", "请求URL: " + requestUrl);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    android.util.Log.d("NewsListActivity", "响应状态: " + response.code() + " " + response.message());
                    
                    if (response.isSuccessful()) {
                        String result = response.body().string().trim();
                        android.util.Log.d("NewsListActivity", "响应内容: " + result);

                        Message msg = Message.obtain();
                        msg.what = 300;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    } else {
                        throw new IOException("服务器返回错误: " + response.code() + " " + response.message());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    android.util.Log.e("NewsListActivity", "网络请求异常", e);
                    Message msg = Message.obtain();
                    msg.what = 300;
                    msg.obj = "网络请求失败: " + e.getMessage() + "\n请求URL: " + requestUrl;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    // 在ListView中显示新闻列表
    private void ShowNewsListView(String jsonString) {
        newsList = null;
        adapter = null;

        newsList = new ArrayList<News>();
        newsList = JSONObject.parseArray(jsonString, News.class);
        adapter = new NewsAdapter(NewsListActivity.this, R.layout.news_item, newsList);
        adapter.setListener(this);
        lv_news.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_publish:
                onPublishClick();
                break;
            case R.id.btn_refresh:
                GetAllNewsByGet();
                break;
            case R.id.btn_exit:
                finish();
                break;
        }
    }

    private void onPublishClick() {
        Intent intent = new Intent(this, NewsPublishActivity.class);
        startActivity(intent);
    }

    @Override
    public void onEditClick(int position, News news) {
        Intent intent = new Intent(this, NewsEditActivity.class);
        intent.putExtra("news", news);
        startActivityForResult(intent, 2001);
    }

    @Override
    public void onDeleteClick(int position, final News news) {
        new AlertDialog.Builder(this)
                .setTitle("删除新闻")
                .setMessage("确定要删除这篇新闻吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeleteNewsByPost(news.getN_id());
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 以POST方式向服务器请求删除新闻
    private void DeleteNewsByPost(int newsId) {
        // 检查必要参数
        if (newsId <= 0) {
            android.util.Log.e("NewsListActivity", "无效的新闻ID: " + newsId);
            Message msg = Message.obtain();
            msg.what = 400;
            msg.obj = "删除失败：无效的新闻ID";
            handler.sendMessage(msg);
            return;
        }

        // 检查服务器URL配置
        if (MainActivity.serverUrl == null || MainActivity.serverUrl.newsDelete == null) {
            android.util.Log.e("NewsListActivity", "服务器删除地址未配置");
            Message msg = Message.obtain();
            msg.what = 400;
            msg.obj = "删除失败：服务器地址未配置";
            handler.sendMessage(msg);
            return;
        }

        final OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("n_id", String.valueOf(newsId))
                .build();
        final Request request = new Request.Builder()
                .url(MainActivity.serverUrl.newsDelete)
                .post(requestBody)
                .build();

        // 记录请求信息以便调试
        final String requestUrl = MainActivity.serverUrl.newsDelete;
        android.util.Log.d("NewsListActivity", "删除新闻请求 - URL: " + requestUrl + ", 新闻ID: " + newsId);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    android.util.Log.d("NewsListActivity", "删除新闻响应 - 状态码: " + response.code() + ", 消息: " + response.message());
                    
                    if (response.isSuccessful()) {
                        String result = response.body().string().trim();
                        android.util.Log.d("NewsListActivity", "删除新闻响应 - 内容: " + result);

                        // 确保返回的结果不为空
                        if (result == null || result.isEmpty()) {
                            result = "删除失败：服务器返回空响应";
                        }

                        Message msg = Message.obtain();
                        msg.what = 400;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    } else {
                        String errorMsg = "删除失败：服务器返回错误 (" + response.code() + ": " + response.message() + ")";
                        android.util.Log.e("NewsListActivity", errorMsg);
                        Message msg = Message.obtain();
                        msg.what = 400;
                        msg.obj = errorMsg;
                        handler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    String errorMsg = "删除失败：网络请求异常 (" + e.getMessage() + ")";
                    android.util.Log.e("NewsListActivity", "删除新闻网络异常", e);
                    Message msg = Message.obtain();
                    msg.what = 400;
                    msg.obj = errorMsg;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        News news = newsList.get(position);

        Intent intent = new Intent(NewsListActivity.this, NewsDetailActivity.class);
        intent.putExtra("newsId", news.getN_id());
        intent.putExtra("newsTitle", news.getN_title());
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2001 && resultCode == 2001) {
            // 新闻编辑完成，刷新列表
            GetAllNewsByGet();
        }
    }
}