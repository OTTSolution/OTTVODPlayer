package com.xugaoxiang.ott.player.bean;

/**
 * Created by user on 2016/9/21.
 */
public class PlayFilmDetail {

    /**
     * id : 1
     * name : 愤怒的小鸟
     * photo : NULL
     * type : 电影
     * detail : NULL
     * score : 0
     * url : NULL
     * introduce : NULL
     * price : 7
     */

    private String id;
    private String name;
    private String photo;
    private String type;
    private String detail;
    private String score;
    private String url;
    private String introduce;
    private String price;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "PlayFilmDetail{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", type='" + type + '\'' +
                ", detail='" + detail + '\'' +
                ", score='" + score + '\'' +
                ", url='" + url + '\'' +
                ", introduce='" + introduce + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
