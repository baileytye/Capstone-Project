package com.bowtye.decisive.database;

import android.annotation.SuppressLint;
import android.app.Application;

public class ProjectRepository extends BaseRepository{

    private static ProjectRepository instance;

    public static ProjectRepository getInstance(Application application) {
        if(instance == null) {
            synchronized (ProjectRepository.class) {
                if(instance == null) {
                    instance = new ProjectRepository(application);
                }
            }
        }
        return instance;
    }

    @SuppressLint("StaticFieldLeak")
    protected ProjectRepository(Application application) {
        super(application);
    }
}
