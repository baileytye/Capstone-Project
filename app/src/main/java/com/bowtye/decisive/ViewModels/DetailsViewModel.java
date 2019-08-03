package com.bowtye.decisive.ViewModels;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bowtye.decisive.Database.ProjectRepository;
import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.ProjectWithDetails;

import timber.log.Timber;

public class DetailsViewModel extends AndroidViewModel {
    private LiveData<ProjectWithDetails> mProject;
    private ProjectRepository mRepo;

    public DetailsViewModel(@NonNull Application application) {
        super(application);
        mRepo = ProjectRepository.getInstance(application);
    }

    public LiveData<ProjectWithDetails> getProject(int id){
        if(mProject == null){
            mProject = new MutableLiveData<>();
            loadProject(id);
        }
        return mProject;

    }

    public void insertOption(Option option, int projectId){
        mRepo.insertOption(option, projectId);
    }

    private void loadProject(int id){
        mProject = mRepo.getSelectedProject(id);
    }

    public void deleteOption(Option option){
        mRepo.deleteOption(option);
    }

}
