package com.example.smit.fbpage.model;

import android.graphics.Bitmap;

public class Post {
    private String pageId, pageName, message;
    private Bitmap postImage;

    public Post(String pageId, String pageName, String message, Bitmap postPicture) {
        this.pageId = pageId;
        this.pageName = pageName;
        this.message = message;
        this.postImage = postPicture;
    }

    public Post() {}

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Bitmap getPostImage() {
        return postImage;
    }

    public void setPostImage(Bitmap postImage) {
        this.postImage = postImage;
    }
}
