package com.xugaoxiang.ott.player.adpter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.xugaoxiang.ott.player.R;

/**
 * Created by user on 2016/9/21.
 */
public class LeftViewHolder extends RecyclerView.ViewHolder{

    public final TextView tv_category;

    public LeftViewHolder(View itemView) {
        super(itemView);
        tv_category = (TextView) itemView.findViewById(R.id.tv_category);
    }
}
