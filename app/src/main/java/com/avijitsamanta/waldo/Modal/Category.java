package com.avijitsamanta.waldo.Modal;

public class Category {
    private String categoryName;
    private String url;
    private String title;

    public Category(String categoryName, String url, String title) {
        this.categoryName = categoryName;
        this.url = url;
        this.title = title;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
