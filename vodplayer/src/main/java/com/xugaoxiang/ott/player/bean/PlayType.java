package com.xugaoxiang.ott.player.bean;

import java.util.List;

/**
 * Created by user on 2016/9/21.
 */
public class PlayType {

    /**
     * id : 1
     * type_name : 其他
     */

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private int id;
        private String type_name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getType_name() {
            return type_name;
        }

        public void setType_name(String type_name) {
            this.type_name = type_name;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "id=" + id +
                    ", type_name='" + type_name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PlayType{" +
                "data=" + data +
                '}';
    }
}
