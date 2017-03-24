package com.xugaoxiang.ott.player.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.xugaoxiang.ott.player.MyApplication;

import java.util.Locale;


public class UIUtils {

    public static int getColor(int colorId) {
        return getContext().getResources().getColor(colorId);
    }

    public static View getXmlView(int layoutId) {
        return View.inflate(getContext(), layoutId, null);
    }

    public static String getString(int stringId) {
        return getContext().getResources().getString(stringId);
    }


    /**
     * 1dp---1px;
     * 1dp---0.75px;
     * 1dp---0.5px;
     * @param dp
     * @return
     */
    public static int dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

    ;

    public static int px2dp(int px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5);
    }

    public static Context getContext() {
        return MyApplication.context;
    }

    public static Handler getHandler() {
        return MyApplication.handler;
    }

    /**
     * 保证runnable对象的run方法是运行在主线程当中
     *
     * @param runnable
     */
    public static void runOnUIThread(Runnable runnable) {
        if (isInMainThread()) {
            runnable.run();
        } else {
            getHandler().post(runnable);
        }
    }

    private static boolean isInMainThread() {
        //当前线程的id
        int tid = android.os.Process.myTid();
        if (tid == MyApplication.mainThreadId) {
            return true;
        }
        return false;
    }


    public static void Toast(String text, boolean isLong) {
        Toast.makeText(getContext(), text, isLong == true ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static int getDimension(int id){
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (getContext().getResources().getDimension(id) * density);
    }

    public static String getMediaTime(int ms) {
        int hour, mintue, second;

        //计算小时 1 h = 3600000 ms
        hour = ms / 3600000;

        //计算分钟 1 min = 60000 ms
        mintue = (ms - hour * 3600000) / 60000;

        //计算秒钟 1 s = 1000 ms
        second = (ms - hour * 3600000 - mintue * 60000) / 1000;

        //格式化输出，补零操作
        String sHour, sMintue, sSecond;
        if (hour < 10) {
            sHour = "0" + String.valueOf(hour);
        } else {
            sHour = String.valueOf(hour);
        }

        if (mintue < 10) {
            sMintue = "0" + String.valueOf(mintue);
        } else {
            sMintue = String.valueOf(mintue);
        }

        if (second < 10) {
            sSecond = "0" + String.valueOf(second);
        } else {
            sSecond = String.valueOf(second);
        }

        return sHour + ":" + sMintue + ":" + sSecond;
    }

    public static void setLanguage(int languag){
        Resources resources = UIUtils.getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (languag == 1){
            config.locale = Locale.CHINA;
        }else if (languag == 2){
            config.locale = Locale.ENGLISH;
        }else {
            config.locale = Locale.CHINA;
        }
        resources.updateConfiguration(config, dm);
    }

//    public static void setViewBackground(View view , int themId){
//        if (Constants.xmlThem == null){
//            Constants.xmlThem = XmlUtils.pullThemXml();
//        }
//        if (Constants.xmlThem != null){
//            for (Them.ThemBean.ThemInfoBean infoBean: Constants.xmlThem.themInfoBeen) {
//                if (infoBean.getThemId() == themId){
//                    Bitmap bitmap = MyBitmapUtils.getBitmap(infoBean.getThemUrl() , Constants.THEM_IMAGE);
//                    if (bitmap != null){
//                        view.setBackground(new BitmapDrawable(bitmap));
//                    }
//                    break;
//                }
//            }
//        }
//    }

}
