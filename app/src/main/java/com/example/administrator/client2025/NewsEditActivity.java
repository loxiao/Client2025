package com.example.administrator.client2025;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.administrator.client2025.entity.News;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewsEditActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_back = null, btn_save = null, btn_reset = null, btn_select_pic = null;
    private TextView tv_user = null;
    private EditText edt_news_id = null, edt_news_title = null, edt_news_author = null, edt_news_content = null, edt_news_pic = null;
    private ImageView iv_news_pic = null;
    private Spinner spinner_news_type = null;

    private List<String> newsTypeList = null;
    private ArrayAdapter<String> typeAdapter = null;
    private News originalNews = null;
    private String selectedImagePath = null;
    private static final int SELECT_IMAGE_REQUEST = 1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = ((String) msg.obj).trim();
            String str = "";
            switch (msg.what) {
                case 100: // 保存新闻修改
                    if (result.contains("yes")) {
                        Intent tt = new Intent();
                        setResult(2001, tt);
                        finish();
                        str = "新闻修改---成功！！";
                    } else {
                        str = "新闻修改---不成功！！";
                    }
                    break;
                case 200: // 获取新闻详情
                    if (result.contains("{")) {
                        originalNews = JSON.parseObject(result, News.class);
                        populateNewsData(originalNews);
                        str = "获取新闻详情---成功！！";
                    } else {
                        str = "获取新闻详情---不成功！！";
                    }
                    break;
            }
            Toast.makeText(NewsEditActivity.this, str, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_edit);

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_reset = (Button) findViewById(R.id.btn_reset);

        btn_back.setOnClickListener(this);
        btn_save.setOnClickListener(this);
        btn_reset.setOnClickListener(this);

        tv_user = (TextView) findViewById(R.id.tv_user);
        tv_user.setText("你好，" + MainActivity.admin.getA_name());

        edt_news_id = (EditText) findViewById(R.id.edt_news_id);
        edt_news_title = (EditText) findViewById(R.id.edt_news_title);
        edt_news_author = (EditText) findViewById(R.id.edt_news_author);
        edt_news_content = (EditText) findViewById(R.id.edt_news_content);
        edt_news_pic = (EditText) findViewById(R.id.edt_news_pic);
        iv_news_pic = (ImageView) findViewById(R.id.iv_news_pic);
        btn_select_pic = (Button) findViewById(R.id.btn_select_pic);
        btn_select_pic.setOnClickListener(this);

        spinner_news_type = (Spinner) findViewById(R.id.spinner_news_type);
        initNewsType();

        // 获取从新闻列表传递过来的新闻对象
        Intent intent = getIntent();
        if (intent != null) {
            originalNews = (News) intent.getSerializableExtra("news");
            if (originalNews != null) {
                populateNewsData(originalNews);
            } else {
                // 如果没有获取到新闻对象，尝试获取新闻ID
                int newsId = intent.getIntExtra("newsId", -1);
                if (newsId != -1) {
                    edt_news_id.setText(String.valueOf(newsId));
                    getNewsDetail(newsId);
                }
            }
        }
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

    // 获取新闻详情
    private void getNewsDetail(int newsId) {
        // 检查服务器URL是否正确初始化
        if (MainActivity.serverUrl == null || MainActivity.serverUrl.newsDetail == null) {
            Toast.makeText(this, "服务器地址未配置，请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(MainActivity.serverUrl.newsDetail + "?id=" + newsId)
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
                        msg.what = 200;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    } else {
                        throw new IOException("服务器返回错误: " + response.code() + " " + response.message());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = 200;
                    msg.obj = "网络请求失败: " + e.getMessage();
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    // 填充新闻数据到表单
    private void populateNewsData(News news) {
        if (news != null) {
            edt_news_id.setText(String.valueOf(news.getN_id()));
            edt_news_title.setText(news.getN_title());
            edt_news_author.setText(news.getN_author());
            edt_news_content.setText(news.getN_content());
            edt_news_pic.setText(news.getN_pic());
            
            // 设置新闻类型
            int typeId = news.getN_type();
            switch (typeId) {
                case 1:
                    spinner_news_type.setSelection(0);
                    break;
                case 2:
                    spinner_news_type.setSelection(1);
                    break;
                case 3:
                    spinner_news_type.setSelection(2);
                    break;
                case 4:
                    spinner_news_type.setSelection(3);
                    break;
                case 5:
                    spinner_news_type.setSelection(4);
                    break;
                default:
                    spinner_news_type.setSelection(0);
                    break;
            }
            
            // 显示新闻图片
            if (news.getN_pic() != null && !news.getN_pic().isEmpty() && MainActivity.serverUrl != null && MainActivity.serverUrl.picPath != null) {
                try {
                    // 使用Picasso加载网络图片，并添加错误处理
                    com.squareup.picasso.Picasso.with(this)
                            .load(MainActivity.serverUrl.picPath + news.getN_pic())
                            .error(R.mipmap.ic_launcher) // 加载失败时显示默认图片
                            .placeholder(R.mipmap.ic_launcher) // 加载中显示默认图片
                            .into(iv_news_pic);
                } catch (Exception e) {
                    android.util.Log.e("NewsEditActivity", "图片加载失败: " + e.getMessage());
                    iv_news_pic.setImageResource(R.mipmap.ic_launcher);
                }
            } else {
                iv_news_pic.setImageResource(R.mipmap.ic_launcher);
            }
        }
    }

    // 保存新闻修改
    private void saveNewsEdit() {
        String id = edt_news_id.getText().toString().trim();
        String title = edt_news_title.getText().toString().trim();
        String author = edt_news_author.getText().toString().trim();
        String content = edt_news_content.getText().toString().trim();
        String pic = edt_news_pic.getText().toString().trim();
        int type = getSelectedNewsTypeId();
        String oldPic = originalNews != null ? originalNews.getN_pic() : "";

        if (id.isEmpty()) {
            Toast.makeText(this, "新闻ID不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

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

        // 检查服务器URL是否正确初始化
        if (MainActivity.serverUrl == null || MainActivity.serverUrl.newsEdit == null) {
            Toast.makeText(this, "服务器地址未配置，请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        // 使用图片上传方式保存新闻修改
        uploadImage(selectedImagePath, id, title, content, author, type, oldPic);
    }

    // 选择图片
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_IMAGE_REQUEST);
    }

    // 处理图片选择结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            selectedImagePath = getRealPathFromURI(selectedImage);
            if (selectedImagePath != null) {
                // 显示选中的图片
                Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
                iv_news_pic.setImageBitmap(bitmap);
                // 设置图片路径到EditText
                String fileName = new File(selectedImagePath).getName();
                edt_news_pic.setText(fileName);
            } else {
                Toast.makeText(this, "获取图片路径失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 获取图片的真实路径
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) {
            return null;
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    // 重置表单
    private void resetForm() {
        populateNewsData(originalNews);
        // 重置图片预览
        if (originalNews != null && originalNews.getN_pic() != null && !originalNews.getN_pic().isEmpty() && MainActivity.serverUrl != null && MainActivity.serverUrl.picPath != null) {
            try {
                // 使用Picasso加载网络图片，并添加错误处理
                com.squareup.picasso.Picasso.with(this)
                        .load(MainActivity.serverUrl.picPath + originalNews.getN_pic())
                        .error(R.mipmap.ic_launcher)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(iv_news_pic);
            } catch (Exception e) {
                android.util.Log.e("NewsEditActivity", "重置图片失败: " + e.getMessage());
                iv_news_pic.setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            iv_news_pic.setImageResource(R.mipmap.ic_launcher);
        }
        selectedImagePath = null;
    }

    // 上传图片到服务器
    private void uploadImage(final String imagePath, final String newsId, final String title, final String content, final String author, final int type, final String oldPic) {
        if (imagePath == null || imagePath.isEmpty()) {
            // 如果没有选择新图片，直接保存新闻信息
            saveNewsWithoutImage(newsId, title, content, author, type, oldPic);
            return;
        }

        // 检查必要参数是否为空
        if (newsId == null || newsId.isEmpty() || title == null || title.isEmpty() || content == null || content.isEmpty() || author == null || author.isEmpty()) {
            Toast.makeText(this, "必要参数不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查服务器URL配置
        if (MainActivity.serverUrl == null || MainActivity.serverUrl.newsEdit == null) {
            Toast.makeText(this, "服务器地址未配置，请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        final OkHttpClient client = new OkHttpClient();
        File file = new File(imagePath);
        if (!file.exists()) {
            Toast.makeText(this, "图片文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 准备时间参数
            String timeStr;
            if (originalNews != null && originalNews.getN_time() != null) {
                timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(originalNews.getN_time());
            } else {
                timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            }

            // 创建MultipartBody
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("n_id", newsId)
                    .addFormDataPart("title", title)
                    .addFormDataPart("content", content)
                    .addFormDataPart("author", author)
                    .addFormDataPart("type", String.valueOf(type))
                    .addFormDataPart("old_pic", oldPic != null ? oldPic : "")
                    .addFormDataPart("time", timeStr)
                    .addFormDataPart("pic", file.getName(), RequestBody.create(MediaType.parse("*"), file))
                    .build();

            final Request request = new Request.Builder()
                    .url(MainActivity.serverUrl.newsEdit)
                    .post(requestBody)
                    .build();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Response response = null;
                    try {
                        response = client.newCall(request).execute();
                        android.util.Log.d("NewsEditActivity", "图片上传响应状态: " + response.code() + " " + response.message());
                        
                        if (response.isSuccessful()) {
                            String result = response.body().string().trim();
                            android.util.Log.d("NewsEditActivity", "图片上传响应内容: " + result);

                            Message msg = Message.obtain();
                            msg.what = 100;
                            msg.obj = result;
                            handler.sendMessage(msg);
                        } else {
                            throw new IOException("服务器返回错误: " + response.code() + " " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        android.util.Log.e("NewsEditActivity", "图片上传异常", e);
                        Message msg = Message.obtain();
                        msg.what = 100;
                        msg.obj = "图片上传失败: " + e.getMessage();
                        handler.sendMessage(msg);
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e("NewsEditActivity", "图片处理异常", e);
            Toast.makeText(this, "图片处理失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // 不包含图片的新闻保存
    private void saveNewsWithoutImage(String newsId, String title, String content, String author, int type, String oldPic) {
        // 检查必要参数是否为空
        if (newsId == null || newsId.isEmpty() || title == null || title.isEmpty() || content == null || content.isEmpty() || author == null || author.isEmpty()) {
            Toast.makeText(this, "必要参数不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查服务器URL配置
        if (MainActivity.serverUrl == null || MainActivity.serverUrl.newsEdit == null) {
            Toast.makeText(this, "服务器地址未配置，请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 准备时间参数
            String timeStr;
            if (originalNews != null && originalNews.getN_time() != null) {
                timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(originalNews.getN_time());
            } else {
                timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            }
            
            // 准备发送数据到服务器
            final OkHttpClient client = new OkHttpClient();
            FormBody body = new FormBody.Builder()
                    .add("n_id", newsId)
                    .add("title", title)
                    .add("content", content)
                    .add("author", author)
                    .add("type", String.valueOf(type))
                    .add("old_pic", oldPic != null ? oldPic : "")
                    .add("time", timeStr)
                    .build();

            final Request request = new Request.Builder()
                    .url(MainActivity.serverUrl.newsEdit)
                    .post(body)
                    .build();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Response response = null;
                    try {
                        response = client.newCall(request).execute();
                        android.util.Log.d("NewsEditActivity", "新闻保存响应状态: " + response.code() + " " + response.message());
                        
                        if (response.isSuccessful()) {
                            String result = response.body().string().trim();
                            android.util.Log.d("NewsEditActivity", "新闻保存响应内容: " + result);

                            Message msg = Message.obtain();
                            msg.what = 100;
                            msg.obj = result;
                            handler.sendMessage(msg);
                        } else {
                            throw new IOException("服务器返回错误: " + response.code() + " " + response.message());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        android.util.Log.e("NewsEditActivity", "新闻保存异常", e);
                        Message msg = Message.obtain();
                        msg.what = 100;
                        msg.obj = "网络请求失败: " + e.getMessage();
                        handler.sendMessage(msg);
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e("NewsEditActivity", "新闻处理异常", e);
            Toast.makeText(this, "新闻处理失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_save:
                saveNewsEdit();
                break;
            case R.id.btn_reset:
                resetForm();
                break;
            case R.id.btn_select_pic:
                selectImage();
                break;
        }
    }
}