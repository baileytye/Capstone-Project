package com.bowtye.decisive.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.ProjectWithDetails;

public class DetailsViewModel extends BaseDetailsViewModel {

    public DetailsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<ProjectWithDetails> getProjectFirebase(int id, String firebaseId){
        if(mProject == null){
            mProject = new MutableLiveData<>();
            loadProjectFirebase(id, firebaseId);
        }
        return mProject;
    }

    private void loadProjectFirebase(int id, String firebaseId){
        mProject = mRepo.getSelectedProjectFirebase(id, firebaseId);
    }

    public void deleteOptionFirebase(Option option,int position){
        mRepo.deleteOptionFirebase(option, position);
    }
}
