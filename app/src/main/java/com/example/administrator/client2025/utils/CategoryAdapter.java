package com.example.administrator.client2025.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.client2025.R;
import com.example.administrator.client2025.entity.Category;

import java.util.List;

public class CategoryAdapter extends ArrayAdapter<Category> {
    private int rid;
    private Context context;
    private OnCategoryItemClickListener listener;

    // 定义接口用于回调
    public interface OnCategoryItemClickListener {
        void onEditClick(int position, Category category);
        void onDeleteClick(int position, Category category);
    }

    // 设置监听器的方法
    public void setOnCategoryItemClickListener(OnCategoryItemClickListener listener) {
        this.listener = listener;
    }

    public CategoryAdapter(Context context, int resource, List<Category> objects) {
        super(context, resource, objects);
        this.context = context;
        this.rid = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int finalPosition = position;
        final Category finalCategory = getItem(position);
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(rid, parent, false);
        } else {
            view = convertView;
        }

        TextView tvId = (TextView) view.findViewById(R.id.tv_cate_id);
        TextView tvName =(TextView) view.findViewById(R.id.tv_cate_name);
        TextView tvDesc =(TextView) view.findViewById(R.id.tv_cate_desc);
        Button btnEdit = (Button) view.findViewById(R.id.btn_edit);
        Button btnDelete = (Button) view.findViewById(R.id.btn_delete);

        tvId.setText(String.valueOf(finalCategory.getC_id()));
        tvName.setText(finalCategory.getC_name());
        tvDesc.setText(finalCategory.getC_desc());

        // 设置编辑按钮点击事件
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEditClick(finalPosition, finalCategory);
                }
            }
        });

        // 设置删除按钮点击事件
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDeleteClick(finalPosition, finalCategory);
                }
            }
        });

        return view;
    }
}
