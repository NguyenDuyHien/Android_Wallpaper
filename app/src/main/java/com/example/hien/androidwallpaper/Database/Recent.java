package com.example.hien.androidwallpaper.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import android.support.annotation.NonNull;

/**
 * Created by Hien on 27/03/2018.
 */

@Entity(tableName = "recent", primaryKeys = {"imageUrl","categoryID"})
public class Recent {

    @ColumnInfo(name = "imageUrl")
    @NonNull
    private String imageUrl;

    @ColumnInfo(name = "categoryID")
    @NonNull
    private String categoryID;

    @ColumnInfo(name = "saveTime")
    private String saveTime;

    @ColumnInfo(name = "key")
    private String key;

    public Recent(@NonNull String imageUrl, @NonNull String categoryID, String saveTime, String key) {
        this.imageUrl = imageUrl;
        this.categoryID = categoryID;
        this.saveTime = saveTime;
        this.key = key;
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

    public String getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(String saveTime) {
        this.saveTime = saveTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
