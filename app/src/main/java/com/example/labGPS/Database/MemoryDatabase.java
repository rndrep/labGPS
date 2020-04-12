package com.example.labGPS.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {Memory.class}, version = 1 , exportSchema = false)
public abstract class MemoryDatabase extends RoomDatabase {
//основной класс по работе с базой данных

    private static MemoryDatabase INSTANCE;

    public abstract MemoryDao memoryDao();

    public static MemoryDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context,
                    MemoryDatabase.class,"memory")
                    .allowMainThreadQueries().fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
