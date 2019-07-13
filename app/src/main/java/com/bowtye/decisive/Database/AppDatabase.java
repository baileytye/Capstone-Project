package com.bowtye.decisive.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.Project;
import com.bowtye.decisive.Models.Requirement;

import timber.log.Timber;


@Database(entities = {Project.class, Requirement.class, Option.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "projects_database";
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK){
                Timber.d("Creating new instance of database");
                sInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                        AppDatabase.DATABASE_NAME).build();
            }
        }
        Timber.d("Getting database instance");
        return sInstance;
    }


    public abstract ProjectsDao projectsDao();
    public abstract RequirementsDao requirementsDao();
    public abstract OptionsDao optionsDao();
}
