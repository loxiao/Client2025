package com.example.administrator.client2025.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.client2025.MainActivity;
import com.example.administrator.client2025.R;
import com.example.administrator.client2025.entity.News;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 新闻适配器，用于显示新闻列表
 */
public class NewsAdapter extends ArrayAdapter<News> {
    private int rid;
    private Context context;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private OnNewsItemClickListener listener;

    public interface OnNewsItemClickListener {
        void onEditClick(int position, News news);
        void onDeleteClick(int position, News news);
    }

    public NewsAdapter(Context context, int resource, List<News> objects) {
        super(context, resource, objects);
        this.context = context;
        rid = resource;
        // 尝试将context转换为OnNewsItemClickListener接口
        if (context instanceof OnNewsItemClickListener) {
            this.listener = (OnNewsItemClickListener) context;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final News news = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(rid, parent, false);

        ImageView newsPic = (ImageView) view.findViewById(R.id.news_pic);
        TextView newsTitle = (TextView) view.findViewById(R.id.news_title);
        TextView newsAuthor = (TextView) view.findViewById(R.id.news_author);
        TextView newsTime = (TextView) view.findViewById(R.id.news_time);
        TextView newsType = (TextView) view.findViewById(R.id.news_type);
        Button btnEdit = (Button) view.findViewById(R.id.btn_edit);
        Button btnDelete = (Button) view.findViewById(R.id.btn_delete);

        newsTitle.setText(news.getN_title());
        newsAuthor.setText("作者：" + news.getN_author());
        newsTime.setText("发布时间：" + dateFormat.format(news.getN_time()));
        newsType.setText("类型：" + getNewsTypeName(news.getN_type()));

        // 使用Picasso加载网络图片
        String picPath = MainActivity.serverUrl.picPath + news.getN_pic();
        Picasso.with(context).load(picPath).into(newsPic);

        // 设置编辑按钮点击事件
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEditClick(position, news);
                }
            }
        });

        // 设置删除按钮点击事件
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteClick(position, news);
                }
            }
        });

        return view;
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
    
    // 设置监听器
    public void setListener(OnNewsItemClickListener listener) {
        this.listener = listener;
    }
}