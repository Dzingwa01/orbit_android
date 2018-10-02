package com.carefulcollections.gandanga.mishift.Helpers;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.carefulcollections.gandanga.mishift.Models.Comment;
import com.carefulcollections.gandanga.mishift.Models.Message;
import com.carefulcollections.gandanga.mishift.Models.Shift;
import com.carefulcollections.gandanga.mishift.Models.ShiftRoom;
import com.carefulcollections.gandanga.mishift.Models.TaskRoom;
import com.carefulcollections.gandanga.mishift.Models.Team;

/**
 * Created by Gandanga on 2018-09-07.
 */
@Database(entities = {ShiftRoom.class, TaskRoom.class, Team.class, Shift.class,Comment.class, Message.class}, version = 6, exportSchema = false)

@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract DaoAccess mishiftDao();
    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "mishift-database")
                            // allow queries on the main thread.
                            // Don't do this on a real app! See PersistenceBasicSample for an example.
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
