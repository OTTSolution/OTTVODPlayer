package com.xugaoxiang.ott.player.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.xugaoxiang.ott.player.R;
import com.xugaoxiang.ott.player.bean.PlayFilmDetail;
import com.xugaoxiang.ott.player.db.dao.MemoryFilmDao;
import com.xugaoxiang.ott.player.utils.AppNetConfig;
import com.xugaoxiang.ott.player.utils.NetworkUtils;
import com.xugaoxiang.ott.player.utils.TwitterRestClient;
import com.xugaoxiang.ott.player.utils.UIUtils;
import com.xugaoxiang.ott.player.view.PayDialog;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by user on 2016/9/22.
 */
public class FilmDetailActivity extends BaseActivity {
    @Bind(R.id.siv_detail)
    ImageView sivDetail;
    @Bind(R.id.tv_play)
    TextView tvPlay;
    @Bind(R.id.tv_collect)
    TextView tvCollect;
    @Bind(R.id.tv_film_name)
    TextView tvFilmName;
    @Bind(R.id.tv_price)
    TextView tvPrice;
    @Bind(R.id.tv_introduce)
    TextView tvIntroduce;
    @Bind(R.id.tv_detail)
    TextView tvDetail;
    private String filmId = "";
    private PlayFilmDetail filmDetail;
    private int price;
    private Context context;
    private PayDialog payDialog;
    private MemoryFilmDao filmDao;

    private final static String TAG = "FilmDetailActivity";
    private int tag;

    @Override
    public void initView() {
        setContentView(R.layout.activity_film_detail);
        ButterKnife.bind(this);
        this.context = FilmDetailActivity.this;
    }

    @Override
    public void initData() {
        tvPlay.setText(UIUtils.getString(R.string.play));
        tvCollect.setText(UIUtils.getString(R.string.stow));
        Intent intent = getIntent();
        if (intent != null) {
            filmId = intent.getStringExtra("filmId");
        }
        getFromServiceFilmDetail();
    }

    private void getFromServiceFilmDetail() {
        RequestParams params = new RequestParams();
        params.add("id", filmId);
        if (MainActivity.language != null){
            if (MainActivity.language.equals("英文")){
                params.add("lang_id" , 2+"");
            }
        }
        TwitterRestClient.get(AppNetConfig.BASE_URL + AppNetConfig.VIDEO_DETAIL, params, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
//                UIUtils.Toast("网络开小差了，请稍后再试吧！", false);
                Log.e(TAG , "onFailure"+s);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                Gson gson = new Gson();
                filmDetail = gson.fromJson(s, PlayFilmDetail.class);
                if (filmDetail != null) {
                    setDisplay();
                }
            }
        });
    }

    private void setDisplay() {
        tvFilmName.setText(TextUtils.isEmpty(filmDetail.getName())?"":filmDetail.getName());
        if(!TextUtils.isEmpty(MainActivity.language) && MainActivity.language.equals("英文")){
            tvIntroduce.setText(TextUtils.isEmpty(filmDetail.getIntroduce())?"SUMMARY：":"SUMMARY："+filmDetail.getIntroduce().trim());
            tvDetail.setText(TextUtils.isEmpty(filmDetail.getDetail())?"DESCRIPTION：":"DESCRIPTION ："+filmDetail.getDetail().trim());
        }else {
            tvIntroduce.setText(TextUtils.isEmpty(filmDetail.getIntroduce())?"简介：":"简介："+filmDetail.getIntroduce().trim());
            tvDetail.setText(TextUtils.isEmpty(filmDetail.getDetail())?"详情：":"详情："+filmDetail.getDetail().trim());
        }
        price = Integer.parseInt(filmDetail.getPrice());
        Glide.with(this)
                .load(filmDetail.getPhoto())
                .into(sivDetail);
        if (price > 0) {
            getServiceIsPay();
        }
    }

    @Override
    public void initListener() {
        tvPlay.setOnClickListener(this);
        tvCollect.setOnClickListener(this);
    }

    @Override
    public void processClick(View view) {
        switch (view.getId()) {
            case R.id.tv_play:
                if (filmDetail != null) {
                    if (tag == 1 || tag == 3) {
                        showPayDialog();
                    } else {
                        play();
                    }
                } else {
                    UIUtils.Toast("网络开小差了，请稍后再试吧！", false);
                }
                break;
            case R.id.tv_collect:

                break;
        }
    }

    private void getServiceIsPay() {
        RequestParams params = new RequestParams();
        params.put("video_id", filmDetail.getId());
        params.put("user_id", NetworkUtils.getMacFromIp()==null?NetworkUtils.getMacFromFile():NetworkUtils.getMacFromIp());
        TwitterRestClient.get(AppNetConfig.BASE_URL + AppNetConfig.VIDEO_IS_PAY, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.e(TAG , "onFailure"+s);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                try {
                    JSONObject object = new JSONObject(s);
                    JSONObject result = object.getJSONObject("result");
                    tag = result.getInt("tag");
                    Log.e("----------" , tag +"");
                    if (tag == 1 || tag == 3) {
                        tvPrice.setVisibility(View.VISIBLE);
                        tvPrice.setText(filmDetail.getPrice() + "  RMB");
                    } else {
                        tvPrice.setVisibility(View.VISIBLE);
                        tvPrice.setText(UIUtils.getString(R.string.pay));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showPayDialog() {
        payDialog = new PayDialog(context);
        payDialog.setTitle(UIUtils.getString(R.string.dialog_title));
        payDialog.setMsg(UIUtils.getString(R.string.dialog_msg1) + price + UIUtils.getString(R.string.dialog_msg2));
        payDialog.setConfirm(UIUtils.getString(R.string.confirm));
        payDialog.setCancle(UIUtils.getString(R.string.cancel));
        payDialog.setClickListener(new PayDialog.DialogOnClickListerne() {
            @Override
            public void confirm() {
                filmPay();
            }

            @Override
            public void cancle() {

            }
        });
        payDialog.show();
    }

    private void filmPay() {
        if (filmDetail != null) {
            RequestParams params = new RequestParams();
            params.put("video_id", filmDetail.getId());
            params.put("user_id", NetworkUtils.getMacFromIp()==null?NetworkUtils.getMacFromFile():NetworkUtils.getMacFromIp());
            TwitterRestClient.get(AppNetConfig.BASE_URL + AppNetConfig.VIDEO_PAY, params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
//                    UIUtils.Toast("出了点问题，请重试！", false);
                    Log.e(TAG , "onFailure:"+s);
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(s);
                        JSONObject result = object.getJSONObject("result");
                        String tag = result.getString("tag");
                        Log.e("===========" , tag);
                        if (tag.equals("success")) {
                            payDialog.dismiss();
                            tvPrice.setText(UIUtils.getString(R.string.pay));
                            play();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void play() {
        filmDao = MemoryFilmDao.getInstance(FilmDetailActivity.this);
        int film_time = filmDao.findFilm(Integer.parseInt(filmId));
        VideoPlayerActivity.openfilm(FilmDetailActivity.this , filmDetail.getUrl() , filmDetail.getName() , film_time , filmId);
    }

//    private String getMacAddress() {
//        String macAddress = "";
//        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
//        macAddress = connectionInfo.getMacAddress();
//        return macAddress;
//    }
//
//    //通过解析这个文件来获取MAC,不同厂家的芯片有可能不同
//    private static final String ETH0_MAC_ADDR = "/sys/class/net/eth0/address" ;
//
//    private String getWireMacAddr() {
//        try {
//            return readLine(ETH0_MAC_ADDR);
//        } catch (IOException e) {
//            Log.e("=====",
//                    "IO Exception when getting eth0 mac address",
//                    e);
//            e.printStackTrace();
//            return "unavailable";
//        }
//    }
//
//    private static String readLine(String filename) throws IOException {
//        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
//        try {
//            return reader.readLine();
//        } finally {
//            reader.close();
//        }
//    }

}
