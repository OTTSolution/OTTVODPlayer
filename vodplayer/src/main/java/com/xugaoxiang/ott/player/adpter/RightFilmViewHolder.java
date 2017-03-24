package com.xugaoxiang.ott.player.adpter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xugaoxiang.ott.player.R;

/**
 * Created by user on 2016/9/22.
 */
public class RightFilmViewHolder extends RecyclerView.ViewHolder{
    public final ImageView siv_icon;
    public final TextView tv_name;

    public RightFilmViewHolder(View itemView) {
        super(itemView);
        siv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
        tv_name = (TextView) itemView.findViewById(R.id.tv_name);
    }
}
