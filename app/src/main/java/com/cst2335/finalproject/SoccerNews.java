package com.cst2335.finalproject;

import java.io.Serializable;

public class SoccerNews implements Serializable {

    /**
     * the image, date, news article URL and description text
     */
    private long id;
    private String title;
    private String image;
    private String date;
    private String articleUrl;
    private String description;

    public SoccerNews(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SoccerNews{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", date='" + date + '\'' +
                ", articleUrl='" + articleUrl + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}