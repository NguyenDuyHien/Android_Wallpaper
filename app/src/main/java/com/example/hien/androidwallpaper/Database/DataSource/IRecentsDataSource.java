package com.example.hien.androidwallpaper.Database.DataSource;

import com.example.hien.androidwallpaper.Database.Recent;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Hien on 27/03/2018.
 */

public interface IRecentsDataSource {
    Flowable<List<Recent>> getAllRencents();
    void insertRecents(Recent... recents);
    void updateRecents(Recent... recents);
    void deleteRecents(Recent... recents);
    void deleteAllRecents();
}
