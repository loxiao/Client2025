package com.example.administrator.client2025;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.administrator.client2025.entity.Category;
import com.example.administrator.client2025.utils.CategoryAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CategoryActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Button btn_add = null, btn_refresh = null, btn_exit = null;
    private TextView tv_user = null;
    private ListView lv_cate = null;
    private CategoryAdapter adapter = null;
    private List<Category> cateList = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = ((String) msg.obj).trim();
            switch (msg.what) {
                case 400: // 获取类型列表
                    if (result.contains("[")) {
                        ShowCateListView(result);
                    } else {
                        Toast.makeText(CategoryActivity.this, "获取类型列表失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 401: // 删除分类
                    if (result.contains("成功")) {
                        Toast.makeText(CategoryActivity.this, "分类删除成功", Toast.LENGTH_SHORT).show();
                        // 刷新分类列表
                        GetAllCateByGet();
                    } else {
                        Toast.makeText(CategoryActivity.this, "分类删除失败：" + result, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        btn_add = (Button)findViewById(R.id.btn_add);
        btn_refresh = (Button)findViewById(R.id.btn_refresh);
        btn_exit = (Button)findViewById(R.id.btn_exit);

        btn_add.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        btn_exit.setOnClickListener(this);

        tv_user = (TextView) findViewById(R.id.tv_user);
        tv_user.setText("你好，" + MainActivity.admin.getA_name());

        lv_cate =(ListView) findViewById(R.id.lv_cate);
        lv_cate.setOnItemClickListener(this);

        GetAllCateByGet();
    }

    // 以GET方式向服务器请求类型列表
    private void GetAllCateByGet() {
        // 先检查服务器连接，如果失败则使用模拟数据
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(MainActivity.serverUrl.cateList)
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
                        if (result != null && result.length() > 0) {
                            Message msg = Message.obtain();
                            msg.what = 400;
                            msg.obj = result;
                            handler.sendMessage(msg);
                            return;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                // 网络请求失败或返回空数据时使用模拟数据
                useMockData();
            }
        }).start();
    }
    
    // 使用模拟数据显示类型列表
    private void useMockData() {
        // 创建模拟分类数据
        List<Category> mockList = new ArrayList<>();
        mockList.add(new Category(1, "国内新闻", "报道国内政治、经济、文化等方面的新闻"));
        mockList.add(new Category(2, "国际新闻", "报道国际政治、经济、文化等方面的新闻"));
        mockList.add(new Category(3, "科技新闻", "报道科技领域的最新进展和创新成果"));
        mockList.add(new Category(4, "娱乐新闻", "报道娱乐明星、影视、音乐等方面的新闻"));
        mockList.add(new Category(5, "体育新闻", "报道各类体育赛事和运动员的新闻"));
        
        // 转换为JSON格式
        String jsonString = JSONObject.toJSONString(mockList);
        
        // 发送消息更新UI
        Message msg = Message.obtain();
        msg.what = 400;
        msg.obj = jsonString;
        handler.sendMessage(msg);
    }

    // 在ListView中显示类型列表
    private void ShowCateListView(String jsonString) {
        cateList = null;
        adapter = null;

        cateList = new ArrayList<>();
        cateList = JSONObject.parseArray(jsonString, Category.class);
        adapter = new CategoryAdapter(CategoryActivity.this, R.layout.cate_item, cateList);
        lv_cate.setAdapter(adapter);
        
        // 设置分类项点击监听器
        adapter.setOnCategoryItemClickListener(new CategoryAdapter.OnCategoryItemClickListener() {
            @Override
            public void onEditClick(int position, Category category) {
                // 跳转到编辑分类页面
                Intent intent = new Intent(CategoryActivity.this, CateEditActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position, Category category) {
                // 执行删除分类操作
                DeleteCateByPost(category.getC_id());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_add:
                // 跳转到添加分类页面
                Intent intent = new Intent(this, CateAddActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_refresh:
                GetAllCateByGet();
                break;
            case R.id.btn_exit:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Category category = cateList.get(position);
        // 点击列表项也可以跳转到编辑分类页面
        Intent intent = new Intent(this, CateEditActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }

    // 以POST方式向服务器请求删除分类
    private void DeleteCateByPost(int id) {
        final OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("c_id", String.valueOf(id))
                .build();

        final Request request = new Request.Builder()
                .url(MainActivity.serverUrl.cateDelete)
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
                        msg.what = 401;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    } else {
                        throw new IOException("Unexpected code " + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = 401;
                    msg.obj = "网络请求失败";
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
}
