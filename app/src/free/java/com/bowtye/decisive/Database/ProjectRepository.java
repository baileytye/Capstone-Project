package com.bowtye.decisive.Database;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.ProjectWithDetails;

import java.util.List;

import timber.log.Timber;

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
