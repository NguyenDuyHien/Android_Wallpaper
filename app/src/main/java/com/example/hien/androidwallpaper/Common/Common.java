package com.example.hien.androidwallpaper.Common;

import com.example.hien.androidwallpaper.Model.WallpaperItem;
import com.example.hien.androidwallpaper.Remote.IComputerVision;
import com.example.hien.androidwallpaper.Remote.RetrofitClient;

/**
 * Created by Hien on 25/03/2018.
 */

public class Common {
    public static final String STR_CATEGORY_WALLPAPER = "Category";
    public static String CATEGORY_SELECTED;
    public static String STR_WALLPAPER = "Wallpaper";
    public static String CATEGORY_ID_SELECTED;

    public static final int REQUEST_CODE = 1;
    public static final int DOWNLOAD_REQUEST_CODE = 9999;
    public static final int TWITTER_REQUEST_CODE = 1000;

    public static WallpaperItem selected_wallpaper = new WallpaperItem();

    public static String selected_wallpaper_key;
    public static int SIGN_IN_REQUEST_CODE = 1001;
    public static int PICK_IMAGE_REQUEST = 1002;

    //Computer Vision API
    public static String BASE_URL = "https://southeastasia.api.cognitive.microsoft.com/vision/v1.0/";
    public static IComputerVision getComputerVisionAPI(){
        return RetrofitClient.getClient(BASE_URL).create(IComputerVision.class);
    }
    public static String getAPIAdultEndPoint(){
        return new StringBuilder(BASE_URL).append("analyze?visualFeatures=Adult&language=en").toString();
    }
}
