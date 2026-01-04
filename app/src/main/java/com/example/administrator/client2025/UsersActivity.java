package com.example.administrator.client2025;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.example.administrator.client2025.entity.Admin;
import com.example.administrator.client2025.utils.AdminAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UsersActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{

    private Button btn_register=null,btn_find=null,btn_refresh=null,btn_exit=null;
    private TextView tv1_user=null;

    private ListView lv_user=null;
    private AdminAdapter adapter=null;
    private List<Admin>     list=null;

    private View            body=null;              //---自定义的查询对话框

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String  result=((String)msg.obj).trim();
            String  str="";

            switch (msg.what){
                case 200:       //...........获取全部、查询用户信息
                    if(result.contains("{") )
                        ShowListView(result);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        btn_register=(Button)findViewById(R.id.btn_register);
        btn_find    =(Button)findViewById(R.id.btn_find);
        btn_refresh =(Button)findViewById(R.id.btn_refresh);
        btn_exit    =(Button)findViewById(R.id.btn_exit);

        btn_register.setOnClickListener(this);
        btn_find.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        btn_exit.setOnClickListener(this);

        tv1_user    =(TextView)findViewById(R.id.tv1_user);
        if (MainActivity.admin != null) {
            tv1_user.setText("你好，"+MainActivity.admin.getA_name());
        } else {
            tv1_user.setText("你好，游客");
        }

        lv_user=(ListView)findViewById(R.id.lv_user);
        lv_user.setOnItemClickListener(this);

        GetAllAdminByPost();
    }

    //-------------------以post方式向服务器发送登录请求
    private void GetAllAdminByPost() {
        final OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .build();

        final Request request = new Request.Builder()
                .url(MainActivity.serverUrl.getAllAdmin)
                .post(body)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String  result=response.body().string().trim();

                        Message msg= Message.obtain();
                        msg.what=200;
                        msg.obj=result;
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


    //-------------------在ListView中显示全部数据
    private void   ShowListView(String  jsonString){
        list=null;
        adapter=null;

        list = new ArrayList<Admin>();
        list = JSONObject.parseArray(jsonString, Admin.class);
        adapter=new AdminAdapter(UsersActivity.this,R.layout.mylistview,list);
        lv_user.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        int  id=v.getId();
        switch (id){
            case R.id.btn_register:
                Intent tt=new Intent(UsersActivity.this,AddAdminActivity.class);
                startActivityForResult(tt,1000);
                break;
            case R.id.btn_find:
                findDialog();
                break;
            case R.id.btn_refresh:
                GetAllAdminByPost();
                break;
            case R.id.btn_exit:
                finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Admin  aa = list.get(position);

        Intent  tt=new Intent(UsersActivity.this,EditAdminActivity.class);
        tt.putExtra("id",aa.getA_id());
        tt.putExtra("name",aa.getA_name());
        tt.putExtra("pwd",aa.getA_pwd());
        tt.putExtra("phone",aa.getA_phone());
        tt.putExtra("pic",aa.getA_pic());

        startActivityForResult(tt,2000);
    }

    //------------加载菜单资源
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.adminmenu,menu);
        return true;
    }

    //------------单击菜单后的操作
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int  id=item.getItemId();
        switch (id){
            case  R.id.menu_user:
                Toast.makeText(this,"用户管理",Toast.LENGTH_SHORT).show();
                break;
            case  R.id.menu_cate:
                Intent intentCate = new Intent(UsersActivity.this, CategoryActivity.class);
                startActivity(intentCate);
                break;
            case  R.id.menu_news:
                Intent intent = new Intent(UsersActivity.this, NewsListActivity.class);
                startActivity(intent);
                break;
            case  R.id.menu_exit:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1000 && resultCode==1001){          //----register
            GetAllAdminByPost();
        }else  if(requestCode==2000 && resultCode==2001){   //----edit
            GetAllAdminByPost();
        }
    }


    //-----------------------------查询用户
    private   void  findDialog(){
        AlertDialog.Builder  builder=new AlertDialog.Builder(this);

        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("查询用户");

        body=getLayoutInflater().inflate(R.layout.find,null,false);
        builder.setView(body);

        builder.setPositiveButton("查询", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                findData();
            }
        });

        builder.create();
        builder.show();
    }

    private   void  findData(){
        if(body!=null){

            EditText e_find=(EditText)body.findViewById(R.id.edt_find);

            String  find=e_find.getText().toString().trim();

            if(find.length()>0  &&  list.size()>0){

                for(int i=list.size()-1;i>=0;i--){
                    if(list.get(i).getA_name().contains(find)==false)
                        list.remove( i );
                }

                if(list.size()>0){
                    lv_user.setAdapter(null);

                    adapter=new AdminAdapter(UsersActivity.this,R.layout.mylistview,list);
                    lv_user.setAdapter(adapter);

                }
            }
        }
    }
}
