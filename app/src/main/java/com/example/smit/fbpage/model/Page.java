package com.example.smit.fbpage.model;

public class Page {

    private String id, name, category, accessToken;

    public Page(String id, String name, String token)
    {
        this.id = id;
        this.name = name;
        this.accessToken = token;
    }

    public Page() {}

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
