package com.example.hien.androidwallpaper.Model;

/**
 * Created by Hien on 26/03/2018.
 */

public class WallpaperItem {
    public String imageUrl;
    public String categoryID;
    public String userID;
    public long viewCount;

    public WallpaperItem() {
    }

    public WallpaperItem(String categoryID, String imageUrl, String userID, long viewCount) {
        this.imageUrl = imageUrl;
        this.categoryID = categoryID;
        this.userID = userID;
        this.viewCount = viewCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }
}

