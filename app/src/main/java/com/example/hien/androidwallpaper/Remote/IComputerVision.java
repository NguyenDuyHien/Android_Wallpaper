package com.example.hien.androidwallpaper.Remote;

import com.example.hien.androidwallpaper.Model.ComputerVision.ComputerVision;
import com.example.hien.androidwallpaper.Model.ComputerVision.URLUpload;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Hien on 30/11/2018.
 */

public interface IComputerVision {
    @Headers({
            "Content-Type:application/json",
            "Ocp-Apim-Subscription-Key:11c1c31ba7874b399815b4f16b7aecd9"
    })

    @POST
    Call<ComputerVision> analyzeImage(@Url String apiEndpoint, @Body URLUpload url);
}
