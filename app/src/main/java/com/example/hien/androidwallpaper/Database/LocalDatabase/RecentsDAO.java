package com.example.hien.androidwallpaper.Database.LocalDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.hien.androidwallpaper.Database.Recent;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by Hien on 27/03/2018.
 */

@Dao
public interface RecentsDAO {
    @Query("SELECT * FROM Recent ORDER BY saveTime DESC LIMIT 10")
    Flowable<List<Recent>> getAllRencents();

    @Insert
    void insertRecents(Recent... recents);

    @Update
    void updateRecents(Recent... recents);

    @Delete
    void deleteRecents(Recent... recents);

    @Query("DELETE FROM Recent")
    void deleteAllRecents();
}
