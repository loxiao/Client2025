package com.example.administrator.client2025;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.client2025.entity.Admin;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class EditAdminActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn2_save=null,btn2_delete=null,btn2_back=null;
    private EditText edt2_id=null,edt2_name=null,edt2_pwd1=null,edt2_phone=null;
    private ImageView img2_my=null;
    private TextView tv3_user=null;

    private Admin admin=null;
    private Bitmap bitmap =null;

    String  newpic= "";         //----新的图片名称

    private Handler handler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String result = ((String) msg.obj).trim();

            String  str="";
            switch (msg.what) {
                case 100:
                    if(result.equals("yes")){
                        if(admin.getA_state()==5){
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] bb = baos.toByteArray();

                            updateAdminByPost_2(bb);
                        }
                        str="修改用户信息---成功！！";
                    }
                    else
                        str="修改用户信息---不成功！！";
                    break;
                case 200:
                    if(result.equals("yes"))
                        str="上传图片---成功！！";
                    else
                        str="上传图片---不成功！！";
                    break;
                case 300:
                    if(result.equals("yes")) {
                        Intent tt=new Intent();
                        setResult(2001,tt);

                        finish();
                        str = "删除用户信息---成功！！";
                    }
                    else
                        str="删除用户信息---不成功！！";
                    break;
            }

            Toast.makeText(EditAdminActivity.this,str,Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_admin);

        btn2_save    =(Button)findViewById(R.id.btn2_save);
        btn2_delete    =(Button)findViewById(R.id.btn2_delete);
        btn2_back    =(Button)findViewById(R.id.btn2_back);

        edt2_id    =(EditText)findViewById(R.id.edt2_id);
        edt2_name    =(EditText)findViewById(R.id.edt2_name);
        edt2_pwd1    =(EditText)findViewById(R.id.edt2_pwd1);
        edt2_phone    =(EditText)findViewById(R.id.edt2_phone);

        tv3_user    =(TextView)findViewById(R.id.tv3_user);
        tv3_user.setText("你好，"+MainActivity.admin.getA_name());

        img2_my    =(ImageView)findViewById(R.id.img2_my);

        btn2_save.setOnClickListener(this);
        btn2_delete.setOnClickListener(this);
        btn2_back.setOnClickListener(this);
        img2_my.setOnClickListener(this);

        admin=new Admin();
        Intent  tt=getIntent();
        admin.setA_id(tt.getIntExtra("id",-1));
        admin.setA_name(tt.getStringExtra("name"));
        admin.setA_pwd(tt.getStringExtra("pwd"));
        admin.setA_phone(tt.getStringExtra("phone"));
        admin.setA_pic(tt.getStringExtra("pic"));
        admin.setA_state(0);

        edt2_id.setText(admin.getA_id()+"");
        edt2_name.setText(admin.getA_name());
        edt2_pwd1.setText(admin.getA_pwd());
        edt2_phone.setText(admin.getA_phone());

        String  path=MainActivity.serverUrl.picPath+admin.getA_pic();

        //使用Picasso下载网络图片，并加载到指定的ImageView上
        Picasso.with(this).load(path).into(img2_my);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn2_save:
                String name=edt2_name.getText().toString().trim();
                String pwd=edt2_pwd1.getText().toString().trim();
                String phone=edt2_phone.getText().toString().trim();

                if(name.length()>0) admin.setA_name(name);
                if(pwd.length()>0) admin.setA_pwd(pwd);
                if(phone.length()>0) admin.setA_phone(phone);

                updateAdminByPost_1();
                break;
            case R.id.btn2_delete:
                deleteAdminByPost();
                break;
            case R.id.btn2_back:
                Intent tt=new Intent();
                setResult(2001,tt);

                finish();
                break;
            case R.id.img2_my:
                Intent intent = new Intent(Intent.ACTION_PICK);
                //指定获取的是图片
                intent.setType("image/*");
                startActivityForResult(intent, 2500);
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2500 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            if (uri != null) {
                try {
                    bitmap=null;
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                    img2_my.setImageBitmap(bitmap);

//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                    Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
//                    cursor.moveToFirst();
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    String path = cursor.getString(columnIndex);
//                    cursor.close();

                    if(bitmap!=null) {
                        admin.setA_state(5);
                        admin.setA_pic(System.currentTimeMillis()+".jpg");
                    }

                }catch (FileNotFoundException e){  }
            }
        }
    }

    //1、-----------向服务器上传一条用户的信息
    private void updateAdminByPost_1() {
        final OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("a_id",admin.getA_id()+"")
                .add("a_name",admin.getA_name())
                .add("a_pwd",admin.getA_pwd())
                .add("a_phone",admin.getA_phone())
                .add("a_pic",admin.getA_pic())
                .build();

        final Request request = new Request.Builder()
                .url(MainActivity.serverUrl.editAdmin)
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
        }).start();
    }

    //2、-----------向服务器上传1张图片（某个用户的头像）
    private void updateAdminByPost_2(final byte[] data) {
        final OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("image/png;charset=utf-8");
            }

            @Override
            public void writeTo(BufferedSink bufferedSink) throws IOException {
                bufferedSink.write(data);
                bufferedSink.flush();
                bufferedSink.close();
            }
        };

        final Request request = new Request.Builder()
                .url(MainActivity.serverUrl.addPic)
                .addHeader("post_type", "feed_back_post")        //添加自定义头部，标识上传类型
                .addHeader("a_id", admin.getA_id()+"")           //---图片对应的a_id
                .addHeader("a_pic", admin.getA_pic())            //---新的图片名称
                .post(requestBody)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()){
                        String  result=response.body().string().trim();
                        Message msg= Message.obtain();
                        msg.what=200;
                        msg.obj=result;
                        handler.sendMessage(msg);
                    }
                    else{
                        throw new IOException("传送发生错误!" + response);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    //-----------根据ID从数据库表格中删除一条数据
    private void deleteAdminByPost() {
        final OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("a_id",admin.getA_id()+"")
                .build();

        final Request request = new Request.Builder()
                .url(MainActivity.serverUrl.deleteAdmin)
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
                        msg.what=300;
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
}