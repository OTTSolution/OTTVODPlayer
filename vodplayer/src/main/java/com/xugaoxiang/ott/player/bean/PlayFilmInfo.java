package com.xugaoxiang.ott.player.bean;

import java.util.List;

/**
 * Created by user on 2016/9/21.
 */
public class PlayFilmInfo {

    /**
     * id : 1
     * name : 愤怒的小鸟
     * photo : NULL
     */

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String id;
        private String name;
        private String photo;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }
    }
}
