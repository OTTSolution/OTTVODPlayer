package com.xugaoxiang.ott.player.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xugaoxiang.ott.player.R;
import com.xugaoxiang.ott.player.db.dao.MemoryFilmDao;
import com.xugaoxiang.ott.player.utils.UIUtils;
import com.xugaoxiang.ott.player.view.RotaProgressBar;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by user on 2016/10/12.
 */
public class VideoPlayerActivity extends BaseActivity implements MediaPlayer.EventListener, IVLCVout.Callback {
    @Bind(R.id.tv_film_name)
    TextView tvFilmName;
    @Bind(R.id.seekbar)
    SeekBar seekbar;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.ll_film_info)
    RelativeLayout llFilmInfo;
    @Bind(R.id.surfaceview)
    SurfaceView surfaceview;
    @Bind(R.id.pb_progress)
    RotaProgressBar pbProgress;
    @Bind(R.id.iv_play_state)
    ImageView ivPlayState;
    @Bind(R.id.player_overlay_textinfo)
    TextView playerOverlayTextinfo;
    @Bind(R.id.tv_speed)
    TextView tvSpeed;
    private static final int CODE_UPDATE_OVERLAY = 1;
    private static final int CODE_DELAY_HIDE = 2;
    private static final int CODE_SEEK_HIDE = 3;
    private static final int CODE_SHOWLOADING = 4;
    private static final int CODE_HIDELOADING = 5;
    private static String url;
    private static String title;
    private static int film_time;
    private static final String TAG  = "VideoPlayerActivity";

    private LibVLC libVLC;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private IVLCVout vlcVout;
    private Media media;
    private long length;

    private boolean mPausable;
    private boolean mSeekable;
    private long mForcedTime;
    private long mLastTime;
    private long lastTimeStamp;
    private long lastTotalRxBytes;
    private long position;
    private static int filmId;
    private MemoryFilmDao filmDao;
    private int isFirstPlay = -1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_UPDATE_OVERLAY:
                    tvTime.setText(UIUtils.getMediaTime((int) mediaPlayer.getTime()) + " / " + UIUtils.getMediaTime((int) length));
                    seekbar.setProgress((int) mediaPlayer.getTime());
                    handler.sendEmptyMessageDelayed(CODE_UPDATE_OVERLAY, 1000);
                    break;
                case CODE_DELAY_HIDE:
                    llFilmInfo.setVisibility(View.INVISIBLE);
                    ivPlayState.setVisibility(View.INVISIBLE);
                    handler.removeMessages(CODE_UPDATE_OVERLAY);
                    break;
                case CODE_SEEK_HIDE:
                    playerOverlayTextinfo.setVisibility(View.INVISIBLE);
                    break;
                case CODE_SHOWLOADING:
                    showLoading();
                    handler.sendEmptyMessageDelayed(CODE_SHOWLOADING, 1000);
                    break;
                case CODE_HIDELOADING:
                    hideLoading();
                    handler.removeMessages(CODE_SHOWLOADING);
                    break;
            }
        }
    };
    private long startTime;
    private boolean isPlayEnd = false;

    @Override
    public void initView() {
        setContentView(R.layout.activity_video_play);
        ButterKnife.bind(this);
        tvFilmName.setText(title);
    }

    @Override
    public void initListener() {
        mediaPlayer.setEventListener(this);
        vlcVout.addCallback(this);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void initData() {
        filmDao = MemoryFilmDao.getInstance(this);
        if (film_time > 0){
            isFirstPlay = 0;
        }
        playfilm();
        lastTotalRxBytes = getTotalRxBytes();
        lastTimeStamp = System.currentTimeMillis();
    }

    private void playfilm() {
        surfaceHolder = surfaceview.getHolder();
        libVLC = new LibVLC(getApplicationContext());
        surfaceHolder.setKeepScreenOn(true);
        mediaPlayer = new MediaPlayer(libVLC);
        vlcVout = mediaPlayer.getVLCVout();
        vlcVout.setVideoView(surfaceview);
        vlcVout.attachViews();
        play();
    }

    private void play() {
        Uri uri = Uri.parse(url);
        media = new Media(libVLC, uri);
        mediaPlayer.setMedia(media);
        mediaPlayer.play();
    }

    public static void openList() {

    }

    public static void openfilm(Context context, String url, String title, int film_time, String filmId) {
        VideoPlayerActivity.url = url;
        VideoPlayerActivity.title = title;
        VideoPlayerActivity.film_time = film_time;
        VideoPlayerActivity.filmId = Integer.parseInt(filmId);
        context.startActivity(new Intent(context, VideoPlayerActivity.class));
    }

    public void pausePlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        vlcVout.detachViews();
        vlcVout.removeCallback(this);
        mediaPlayer.setEventListener(null);
    }

    private void pauseOrPlay() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.play();
            ivPlayState.setVisibility(View.INVISIBLE);
            showOverlayTimeout(true);
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            showOverlayTimeout(false);
        }
    }

    private void resumePlay() {
        vlcVout.setVideoView(surfaceview);
        vlcVout.attachViews();
        vlcVout.addCallback(this);
        mediaPlayer.setEventListener(this);
        mediaPlayer.play();
    }

    private void showOverlayTimeout(boolean isPlaying) {
        llFilmInfo.setVisibility(View.VISIBLE);
        handler.sendEmptyMessage(CODE_UPDATE_OVERLAY);
        if (isPlaying) {
            handler.removeMessages(CODE_DELAY_HIDE);
            handler.sendEmptyMessageDelayed(CODE_DELAY_HIDE, 4000);
        } else {
            ivPlayState.setVisibility(View.VISIBLE);
        }
    }

    private long getTime() {
        long time = mediaPlayer.getTime();
        if (mForcedTime != -1 && mLastTime != -1) {
            if (mLastTime > mForcedTime) {
                if (time <= mLastTime && time > mForcedTime || time > mLastTime)
                    mLastTime = mForcedTime = -1;
            } else {
                if (time > mForcedTime)
                    mLastTime = mForcedTime = -1;
            }
        } else if (time == 0)
            time = (int) mediaPlayer.getTime();
        return mForcedTime == -1 ? time : mForcedTime;
    }


    private void seekDelta(int delta) {
        if (mediaPlayer.getLength() <= 0 || !mediaPlayer.isSeekable()) return;
        position = getTime() + delta;
        if (position < 0) position = 0;
        if (position > length - 10000) position = length - 10000;
        showSeekInfo(UIUtils.getMediaTime((int) position) + " / " + UIUtils.getMediaTime((int) length) , 1000);
        mForcedTime = position;
    }

    private void showSeekInfo(String text , int delayed) {
        playerOverlayTextinfo.setVisibility(View.VISIBLE);
        playerOverlayTextinfo.setText(text);
        handler.removeMessages(CODE_SEEK_HIDE);
        handler.sendEmptyMessageDelayed(CODE_SEEK_HIDE, delayed);
    }

    private void seek(long position) {
        if (position >= length) {
            return;
        } else {
            mediaPlayer.setTime(position);
        }
    }

    private String getNetSpeed() {
        long nowTotalRxBytes = getTotalRxBytes();
        long nowTimeStamp = System.currentTimeMillis();
        long speed = 0;
        long s = nowTimeStamp - lastTimeStamp;
        if (s <= 0) {
            s = 1;
        }
        speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (s));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        return String.valueOf(speed) + "KB/s";
    }

    private void showLoading() {
        if (pbProgress.getVisibility() == View.INVISIBLE) {
            pbProgress.setVisibility(View.VISIBLE);
            tvSpeed.setVisibility(View.VISIBLE);
        }
        tvSpeed.setText(getNetSpeed());
    }

    private void hideLoading() {
        pbProgress.setVisibility(View.INVISIBLE);
        tvSpeed.setVisibility(View.GONE);
    }

    private long getTotalRxBytes() {
        return TrafficStats.getUidRxBytes(getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            resumePlay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(CODE_SHOWLOADING);
        pausePlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isPlayEnd){
            if (filmDao.findFilm(filmId) > 0 && mediaPlayer.getTime() > 0){
                filmDao.updata(filmId , (int) mediaPlayer.getTime());
            }else {
                filmDao.addMemoryFilm(filmId , (int) mediaPlayer.getTime());
            }
        }
        pausePlay();
        mediaPlayer = null;
        handler.removeMessages(CODE_UPDATE_OVERLAY);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                pauseOrPlay();
                break;
            case KeyEvent.KEYCODE_MENU:
                showOverlayTimeout(true);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                seekDelta(10000);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (System.currentTimeMillis() - startTime < 5000){
                    mediaPlayer.setTime(0);
                    return false;
                }
                seekDelta(-10000);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (position < 0){
                    position = 0;
                    return false;
                }
                seek(position);
                mLastTime = mediaPlayer.getTime();
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (position > length - 10000){
                    return  false;
                }
                seek(position);
                mLastTime = mediaPlayer.getTime();
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onEvent(MediaPlayer.Event event) {
        Log.e(TAG , "TYPE:"+event.type+",STATE:"+mediaPlayer.getPlayerState());
        isEnd(event);
        switch (event.type) {
            case MediaPlayer.Event.Buffering:
                if (event.getBuffering() == 100f) {
                    handler.sendEmptyMessage(CODE_HIDELOADING);
                    if (isFirstPlay == 1){
                        startTime = System.currentTimeMillis();
                        showSeekInfo(UIUtils.getString(R.string.resume_1)+UIUtils.getMediaTime(film_time)+UIUtils.getString(R.string.resume_2) , 5000);
                        isFirstPlay = -1;
                    }
                    if (isFirstPlay == 0){
                        mediaPlayer.setTime(film_time);
                        isFirstPlay = 1;
                    }
                } else {
                    handler.sendEmptyMessage(CODE_SHOWLOADING);
                }
                break;
            case MediaPlayer.Event.PausableChanged:
                mPausable = event.getPausable();
                break;
            case MediaPlayer.Event.SeekableChanged:
                mSeekable = event.getSeekable();
                break;
            case MediaPlayer.Event.Playing:

                break;

            case MediaPlayer.Event.EndReached:
//                if (mediaPlayer.getTime() - (5 * 60000) > length){
//                    exit();
//                }
                break;
        }
    }

    public void isEnd(MediaPlayer.Event event){
        //播放结束
        if (length < mediaPlayer.getLength() || event.getTimeChanged() == 0 || length == 0 || event.getTimeChanged() > length) {
            return;
        }
        if (mediaPlayer.getPlayerState() == Media.State.Ended && mediaPlayer.getTime() > length - 8000) {
            exit();
        }
    }

    public void exit(){
        mLastTime = 0;
        mForcedTime = 0;
        position = 0;
        seekbar.setProgress(0);
        mediaPlayer.setTime(0);
        mediaPlayer.stop();
        showOverlayTimeout(false);
        filmDao.delete(filmId);
        finish();
    }

    @Override
    public void onNewLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        length = mediaPlayer.getLength();
        seekbar.setMax((int) length);
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        int videoWidth = width;
        int videoHight = height;

        ViewGroup.LayoutParams layoutParams = surfaceview.getLayoutParams();
        layoutParams.width = point.x;
        layoutParams.height = (int) Math.ceil((float) videoHight * (float) point.x / (float) videoWidth);
        surfaceview.setLayoutParams(layoutParams);
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }
}
