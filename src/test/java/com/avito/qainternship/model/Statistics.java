package com.avito.qainternship.model;

public class Statistics {
    private Integer likes;
    private Integer viewCount;
    private Integer contacts;

    public Statistics() { }

    public Statistics(Integer likes, Integer viewCount, Integer contacts) {
        this.likes = likes;
        this.viewCount = viewCount;
        this.contacts = contacts;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getContacts() {
        return contacts;
    }

    public void setContacts(Integer contacts) {
        this.contacts = contacts;
    }
}
