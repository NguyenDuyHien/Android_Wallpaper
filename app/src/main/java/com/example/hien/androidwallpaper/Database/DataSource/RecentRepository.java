package com.example.hien.androidwallpaper.Database.DataSource;

import com.example.hien.androidwallpaper.Database.Recent;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Hien on 27/03/2018.
 */

public class RecentRepository implements IRecentsDataSource{

    private IRecentsDataSource mLocalDataSource;
    private static RecentRepository instance;

    public RecentRepository(IRecentsDataSource mLocalDataSource) {
        this.mLocalDataSource = mLocalDataSource;
    }

    public static RecentRepository getInstance(IRecentsDataSource mLocalDataSource){
        if(instance == null)
            instance = new RecentRepository(mLocalDataSource);
        return  instance;
    }

    @Override
    public Flowable<List<Recent>> getAllRencents() {
        return mLocalDataSource.getAllRencents();
    }

    @Override
    public void insertRecents(Recent... recents) {
        mLocalDataSource.insertRecents(recents);
    }

    @Override
    public void updateRecents(Recent... recents) {
        mLocalDataSource.updateRecents(recents);
    }

    @Override
    public void deleteRecents(Recent... recents) {
        mLocalDataSource.deleteRecents(recents);
    }

    @Override
    public void deleteAllRecents() {
        mLocalDataSource.deleteAllRecents();
    }
}
