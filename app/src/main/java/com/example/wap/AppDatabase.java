package com.example.wap;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {com.example.wap.ImageLink.class}, version = 1, exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    public abstract com.example.wap.ImageLinkDao imageLinkDao();
}
