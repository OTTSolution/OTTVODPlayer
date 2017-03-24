package com.xugaoxiang.ott.player.utils;

/**
 * Created by user on 2016/8/30.
 */
public class AppNetConfig {

    public static String BASE_URL = "http://10.10.10.200:8080";

    public static String VIDEO_TYPES = "/api/video/types";

    public static final String VIDEO_TYPE = "/api/video/type";

    public static final String VIDEO_DETAIL = "/api/video/info";

    public static final String VIDEO_IS_PAY = "/api/video/payinfo";//?video_id=1&user_id=2";

    public static final String VIDEO_PAY = "/api/video/pay+";//video_id=1&user_id=1";

    public static final String VIDEO_TEST_TYPES = "http://192.168.191.1:8080/Video/types.json";

    public static final String VIDEO_TEST_TYPE = "http://192.168.191.1:8080/Video/";

    public static final String VIDEO_TEST_DETAIL = "http://192.168.191.1:8080/Video/1.json";

    public static final String VIDEO_TEST_IS_PAY = "http://192.168.191.1:8080/Video/isPay.json";

}
