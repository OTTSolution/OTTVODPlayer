package com.xugaoxiang.ott.player.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xugaoxiang.ott.player.R;
import com.xugaoxiang.ott.player.adpter.LeftTypeCategory;
import com.xugaoxiang.ott.player.adpter.RightFilmAdapter;
import com.xugaoxiang.ott.player.bean.PlayFilmInfo;
import com.xugaoxiang.ott.player.bean.PlayType;
import com.xugaoxiang.ott.player.utils.AppNetConfig;
import com.xugaoxiang.ott.player.utils.StreamUtils;
import com.xugaoxiang.ott.player.utils.TwitterRestClient;
import com.xugaoxiang.ott.player.utils.UIUtils;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.open.androidtvwidget.recycle.RecyclerViewTV;
import com.open.androidtvwidget.view.GridViewTV;

import org.apache.http.Header;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

//import com.longjingtech.ott.play.view.FocusGridLayoutManager;

public class MainActivity extends BaseActivity {

    @Bind(R.id.rlv_film)
    GridViewTV rlvFilm;
    @Bind(R.id.rlv_type)
    RecyclerViewTV rlvType;
    @Bind(R.id.tv_select_num)
    TextView tvSelectNum;
    @Bind(R.id.pb_loading)
    ProgressBar pbLoading;
    private PlayFilmInfo filmInfo;
    private RightFilmAdapter adapter;
    private int rlvPosition = -1;
    private int rlvColumn = 5;
    private Gson gson;
    private int leftPosintion = -1;
    private PlayType playType;
    private static final int CODE_FIRST_FOCUS = 0;
    private static final int CODE_DELAY_LOAD = 1;
    private static final int CODE_FLV_FOCUSE = 2;
    private static final String TAG = "MainActivity";

    private static final String LOCAL_URL = Environment.getExternalStorageDirectory() + File.separator + "MyApp" + File.separator + "AppServer";
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_FIRST_FOCUS:
                    rlvType.getChildAt(0).requestFocus();
                    break;
                case CODE_DELAY_LOAD:
                    adapter.clearData();
                    adapter.notifyDataSetChanged();
                    setRightData(leftPosintion);
                    break;
                case CODE_FLV_FOCUSE:
                    setAllRlvTypeChildFocusble(false);
                    break;
            }
        }
    };
    private String url;
    private ArrayList<String> list = new ArrayList<>();
    private LeftTypeCategory adapter1;
    public static String language;
    private GridLayoutManager gridlayoutManager;
    public static boolean scroll_state;
    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        url = getLocalFileURL();
        if (!TextUtils.isEmpty(url)){
            AppNetConfig.BASE_URL = url;
        }
        
    }

    private String getLocalFileURL() {
        File file = new File(LOCAL_URL);
        String str = "";
        if (file.exists()){
            try {
                FileInputStream stream = new FileInputStream(file);
                str = StreamUtils.stream2String(stream);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        if (intent != null){
            language = intent.getStringExtra("language");
            if (!TextUtils.isEmpty(language) && language.equals("英文")){
                UIUtils.setLanguage(2);
            }else {
                UIUtils.setLanguage(1);
            }
        }
        gson = new Gson();
        setDefaultData();
        getFromServiceTypes();
        recyclerViewTypeGridLayout(GridLayoutManager.VERTICAL);
        adapter = new RightFilmAdapter(this);
        rlvFilm.setAdapter(adapter);
    }

    private void setDefaultData() {
        list.add(UIUtils.getString(R.string.film));
        list.add(UIUtils.getString(R.string.variety));
        list.add(UIUtils.getString(R.string.teleplay));
        list.add(UIUtils.getString(R.string.other));
        adapter1 = new LeftTypeCategory(list, this);
        rlvType.setAdapter(adapter1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void initListener() {
        setRightRlvListener();
        setRlvTypeListener();
//        rlvFilm.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                switch (newState){
//                    case RecyclerView.SCROLL_STATE_SETTLING:
//                        break;
//                    case RecyclerView.SCROLL_STATE_IDLE:
//                        break;
//                }
//            }
//        });

    }

    private void setAllRlvTypeChildFocusble(boolean b) {
        if (b){
            for (int i = 0; i < rlvType.getChildCount(); i++) {
                rlvType.getChildAt(i).setFocusable(false);
            }
        }else {
            for (int i = 0; i < rlvType.getChildCount(); i++) {
                rlvType.getChildAt(i).setFocusable(true);
            }
        }
    }
    private TextView tv_name;
    private void setRightRlvListener() {
        rlvFilm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rlvPosition = position;
                tvSelectNum.setText((rlvPosition + 1) + "/" + filmInfo.getData().size());
                setTextNumColor(tvSelectNum);
                setAllRlvTypeChildFocusble(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        rlvFilm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startDetailActivity(position);
            }
        });
//        rlvFilm.setOnItemListener(new RecyclerViewTV.OnItemListener() {
//
//
//            @Override
//            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
//                Log.e("getNextFocusDownId()" , rlvFilm.getNextFocusDownId()+"");
//
//                if (rlvFilm.gainFocus){
//                    Log.e("-------------" , position+"");
//                    rlvFilm.getChildAt(position).requestFocus();
//                }
//            }
//            @Override
//            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
//                rlvPosition = position;
//                tvSelectNum.setText((rlvPosition + 1) + "/" + filmInfo.getData().size());
//                setTextNumColor(tvSelectNum);
//                setAllRlvTypeChildFocusble(true);
//            }
//            @Override
//            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
//                startDetailActivity(position);
//            }
//            @Override
//            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
//            }
//
//        });
    }

    private void setTextNumColor(TextView tvSelectNum) {
        String text = tvSelectNum.getText().toString();
        int lastIndex = text.lastIndexOf("/");
        Spannable span = new SpannableString(text);
        span.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, lastIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSelectNum.setText(span);
    }

    private void startDetailActivity(int position) {
        Intent intent = new Intent(MainActivity.this, FilmDetailActivity.class);
        intent.putExtra("filmId", filmInfo.getData().get(position).getId());
        startActivity(intent);
    }
    private TextView tv_category;
    private void setRlvTypeListener() {
        rlvType.setOnItemListener(new RecyclerViewTV.OnItemListener() {
            @Override
            public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {

            }

            @Override
            public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
                if (tv_category != null)tv_category.setBackgroundResource(0);
                tv_category = (TextView) itemView.findViewById(position);
                tv_category.setBackgroundResource(R.drawable.button_zhibo);

                rlvPosition = -1;
                if (leftPosintion != position){
                    leftPosintion = position;
                    pbLoading.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessageDelayed(CODE_DELAY_LOAD, 500);
                }
                if (filmInfo != null){
                    tvSelectNum.setText((rlvPosition + 1) + "/" + filmInfo.getData().size());
                }
            }

            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {

            }

            @Override
            public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
            }
        });
    }

    private void setRightData(int position) {
        addMovieData(position);
    }

    private void addMovieData(int position) {
        RequestParams params = new RequestParams();
        if (playType != null){
            params.put("type", playType.getData().get(position).getId());
            if (language != null && language.equals("英文")){
                params.add("lang_id" , 2+"");
            }
        }
        TwitterRestClient.get(AppNetConfig.BASE_URL + AppNetConfig.VIDEO_TYPE, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
//                UIUtils.Toast("网络开小差了，请稍后再试吧！", false);
                Log.e(TAG , "onFailure");
                pbLoading.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                filmInfo = gson.fromJson(s, PlayFilmInfo.class);
                if (filmInfo != null) {
                    tvSelectNum.setText((rlvPosition + 1) + "/" + filmInfo.getData().size());
                    setTextNumColor(tvSelectNum);
                    adapter.setData(filmInfo);
                    adapter.notifyDataSetChanged();
                    pbLoading.setVisibility(View.INVISIBLE);
                    tvSelectNum.setText((rlvPosition + 1) + "/" + filmInfo.getData().size());
                } else {
                    UIUtils.Toast("网络出小差啦，请稍后再试！", false);
                }
            }
        });
    }

    @Override
    public void processFocus(View view, boolean hasFocus) {

    }


    private void getFromServiceTypes() {
        RequestParams params = new RequestParams();
        if (language != null){
            if (language.equals("英文")){
                params.add("lang_id" , 2+"");
            }
        }
        TwitterRestClient.get(AppNetConfig.BASE_URL + AppNetConfig.VIDEO_TYPES, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
//                UIUtils.Toast("网络开小差了，请稍后再试吧！", true);
                Log.e(TAG , "onFailure");
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                playType = gson.fromJson(s, PlayType.class);
                setCategoryData(playType);
            }
        });
    }

    private void setCategoryData(PlayType playType) {
        list.clear();
        for (int i = 0; i < playType.getData().size(); i++) {
            list.add(playType.getData().get(i).getType_name());
        }
        adapter1.notifyDataSetChanged();
        handler.sendEmptyMessageDelayed(CODE_FIRST_FOCUS, 100);
    }

    private void recyclerViewGridLayout(int orientation) {
//        FocusGridLayoutManager gridlayoutManager = new FocusGridLayoutManager(this, rlvColumn);
//        gridlayoutManager.setOrientation(orientation);
//        rlvFilm.setLayoutManager(gridlayoutManager);
//        rlvFilm.setSelectedItemAtCentered(false);
//        rlvFilm.setSelectedItemOffset(0, -34);
//        rlvFilm.setFocusable(false);
//        rlvFilm.addItemDecoration(new RecyclerViewItemSpace(0, 0, getResources().getDimensionPixelSize(R.dimen.w_28), getResources().getDimensionPixelSize(R.dimen.w_34)));
    }

    private void recyclerViewTypeGridLayout(int orientation) {
        gridlayoutManager = new GridLayoutManager(this, 1);
        gridlayoutManager.setOrientation(orientation);
        rlvType.setLayoutManager(gridlayoutManager);
        rlvType.setSelectedItemAtCentered(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (rlvPosition % 5 == 0) {
                    setAllRlvTypeChildFocusble(false);
                    rlvType.getChildAt(leftPosintion).requestFocus();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (rlvPosition < 5 && rlvPosition != -1) {
                    return true;
                }
                break;
//            case KeyEvent.KEYCODE_DPAD_DOWN:
//                if (filmInfo.getData().size() < 6 || rlvPosition > filmInfo.getData().size() - 6) {
//                    return true;
//                }
//                if (rlvPosition >= 0) {
//                    if (isLastItem()) {
//                        return true;
//                    }
//                }
//                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean isLastItem() {
        if (filmInfo.getData() == null) {
            return false;
        }
        int state = filmInfo.getData().size() % 5;
        if (state == 0) {
            if (rlvPosition > filmInfo.getData().size() - 6) {
                return true;
            }
        } else if (rlvPosition > filmInfo.getData().size() / 5 * 5 - 1) {
            return true;
        }
        return false;
    }

}
