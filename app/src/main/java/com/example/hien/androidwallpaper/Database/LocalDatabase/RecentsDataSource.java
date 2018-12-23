package com.example.hien.androidwallpaper.Database.LocalDatabase;

import com.example.hien.androidwallpaper.Database.DataSource.IRecentsDataSource;
import com.example.hien.androidwallpaper.Database.Recent;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Hien on 27/03/2018.
 */

public class RecentsDataSource implements IRecentsDataSource{

    private RecentsDAO recentsDAO;
    private static RecentsDataSource instance;

    public RecentsDataSource(RecentsDAO recentsDAO) {
        this.recentsDAO = recentsDAO;
    }

    public static RecentsDataSource getInstance(RecentsDAO recentsDAO){
        if(instance == null)
            instance = new RecentsDataSource((recentsDAO));
        return instance;
    }

    @Override
    public Flowable<List<Recent>> getAllRencents() {
        return recentsDAO.getAllRencents();
    }

    @Override
    public void insertRecents(Recent... recents) {
        recentsDAO.insertRecents(recents);
    }

    @Override
    public void updateRecents(Recent... recents) {
        recentsDAO.updateRecents(recents);
    }

    @Override
    public void deleteRecents(Recent... recents) {
        recentsDAO.deleteRecents(recents);
    }

    @Override
    public void deleteAllRecents() {
        recentsDAO.deleteAllRecents();
    }
}
