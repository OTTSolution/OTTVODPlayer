package com.xugaoxiang.ott.player.adpter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xugaoxiang.ott.player.R;
import com.xugaoxiang.ott.player.bean.PlayFilmInfo;


/**
 * Created by user on 2016/9/22.
 */
public class RightFilmAdapter extends BaseAdapter{

    private PlayFilmInfo filmInfo;
    private Context context;
    private ViewHoler viewHoler;

    public RightFilmAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        if (filmInfo == null){
            return 0;
        }else {
            return filmInfo.getData().size();
        }
    }

    @Override
    public PlayFilmInfo.DataBean getItem(int position) {
        return filmInfo.getData().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView =  View.inflate(context, R.layout.film_grid_item, null);
            viewHoler = new ViewHoler(convertView);
            convertView.setTag(viewHoler);
        }else {
            viewHoler = (ViewHoler) convertView.getTag();
        }
        final PlayFilmInfo.DataBean dataBean = filmInfo.getData().get(position);
        viewHoler.siv_icon.setId(position);
        viewHoler.tv_name.setText(dataBean.getName());
        Glide.with(context)
                .load(dataBean.getPhoto())
                .into(viewHoler.siv_icon);
        return convertView;
    }

    public void setData(PlayFilmInfo filmInfo){
        this.filmInfo = filmInfo;
    }

    public void clearData(){
        this.filmInfo = null;
    }

    static class ViewHoler{
        public final ImageView siv_icon;
        public final TextView tv_name;
        public ViewHoler(View itemView) {
            siv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
//
//    @Override
//    public RightFilmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view =  View.inflate(context, R.layout.film_grid_item, null);
//        return new RightFilmViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(final RightFilmViewHolder holder, int position) {
//        final PlayFilmInfo.DataBean dataBean = filmInfo.getData().get(position);
//        holder.siv_icon.setId(position);
//        holder.tv_name.setText(dataBean.getName());
////        Handler handler = new Handler(){
////            @Override
////            public void handleMessage(Message msg) {
//        Glide.with(context)
//                .load(dataBean.getPhoto())
//                .into(holder.siv_icon);
////            }
////        };
////        handler.sendEmptyMessageDelayed(1,100);
//    }
//
//    @Override
//    public int getItemCount() {
//        if (filmInfo != null){
//            return filmInfo.getData().size();
//        }
//        return 0;
//    }
}
