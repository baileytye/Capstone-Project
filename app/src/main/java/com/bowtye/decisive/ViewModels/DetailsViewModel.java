package com.bowtye.decisive.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.bowtye.decisive.Database.ProjectRepository;
import com.bowtye.decisive.Models.Project;

public class DetailsViewModel extends AndroidViewModel {
    LiveData<Project> mProject;
    ProjectRepository mRepo;

    public DetailsViewModel(@NonNull Application application) {
        super(application);
        mRepo = ProjectRepository.getInstance(application);
    }

    private void loadProject(int id){
        mProject = mRepo.updateSelectedProject(id);
    }

    public LiveData<Project> getProject(int id){
        if(mProject == null){
            loadProject(id);
        }
        return mProject;
    }

    public void insertOption(Project p){
        mRepo.insert(p);
    }

}
