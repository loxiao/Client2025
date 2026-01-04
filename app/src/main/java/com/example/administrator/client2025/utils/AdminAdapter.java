package com.example.administrator.client2025.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.client2025.MainActivity;
import com.example.administrator.client2025.R;
import com.example.administrator.client2025.entity.Admin;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by yuanh on 2021/11/20.
 */
public class AdminAdapter extends ArrayAdapter<Admin> {
    private int     rid;
    private Context context;

    public AdminAdapter(Context context, int resource, List<Admin> objects) {
        super(context, resource, objects);

        this.context=context;
        rid=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //从第position个位置，获取第position项的MyData实例
        Admin  data = getItem(position);

        //LayoutInflater的inflate()方法接收3个参数：
        //需要实例化布局资源的id
        //ViewGroup类型视图组对象
        //false表示只让父布局中声明的layout属性生效，但不会为这个view添加父布局
        View view = LayoutInflater.from(getContext()).inflate(  rid, parent, false  );

        //获取实例
        final  ImageView   pic = (ImageView) view.findViewById(R.id.pic);
        TextView    name = (TextView) view.findViewById(R.id.tv_name);
        TextView    detail = (TextView) view.findViewById(R.id.tv_phone);

        //设置图、文字对应的外观位置
        name.setText(data.getA_name());
        detail.setText(data.getA_phone());


        //设置图片对应的外观位置
        final String  picpath= MainActivity.serverUrl.picPath+data.getA_pic();

        //使用Picasso下载网络图片，并加载到指定的ImageView上
        Picasso.with(context).load(picpath).into(pic);


        return view;
    }

}