package com.xugaoxiang.ott.player.adpter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.xugaoxiang.ott.player.R;

import java.util.ArrayList;

/**
 * Created by user on 2016/9/21.
 */
public class LeftTypeCategory extends RecyclerView.Adapter<LeftViewHolder>{

    private ArrayList<String> list;
    private Context context;

    public LeftTypeCategory(ArrayList<String> list , Context context) {
        this.list = list;
        this.context = context;
    }
    @Override
    public LeftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.list_catogry_item, null);
        return new LeftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LeftViewHolder holder, int position) {
        holder.tv_category.setId(position);
        holder.tv_category.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
