package com.example.hien.androidwallpaper.Database.LocalDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.hien.androidwallpaper.Database.Recent;

import static com.example.hien.androidwallpaper.Database.LocalDatabase.LocalDatabase.DATABASE_VERSION;

/**
 * Created by Hien on 27/03/2018.
 */

@Database(entities = Recent.class, version = DATABASE_VERSION)
public abstract class LocalDatabase extends RoomDatabase{
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "AndroidWallpaper";

    public abstract RecentsDAO recentsDAO();

    private static LocalDatabase instance;

    public  static LocalDatabase getInstance(Context content){
        if(instance == null){
            instance = Room.databaseBuilder(content, LocalDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();

        }
        return instance;
    }


}
