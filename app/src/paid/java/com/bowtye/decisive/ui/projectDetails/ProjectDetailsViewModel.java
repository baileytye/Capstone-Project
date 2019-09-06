package com.bowtye.decisive.ui.projectDetails;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bowtye.decisive.utils.ProjectModelConverter;
import com.bowtye.decisive.models.Option;
import com.bowtye.decisive.models.ProjectFirebase;
import com.bowtye.decisive.models.ProjectWithDetails;

public class ProjectDetailsViewModel extends BaseProjectDetailsViewModel {

    public ProjectDetailsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<ProjectWithDetails> getProjectFirebase(int id, String firebaseId, boolean isTemplate){
        if(isTemplate){
            return getProject(id, firebaseId, isTemplate);
        }
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

    public void uploadImagesToFirebase(ProjectWithDetails projectWithDetails){
        mRepo.uploadImagesToFirebase(ProjectModelConverter.projectWithDetailsToProjectFirebase(projectWithDetails,
                projectWithDetails.getProject().getFirebaseId()));
    }
}
