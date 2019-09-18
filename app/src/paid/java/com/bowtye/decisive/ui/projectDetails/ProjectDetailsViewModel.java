package com.bowtye.decisive.ui.projectDetails;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bowtye.decisive.database.ProjectRepository;
import com.bowtye.decisive.utils.ProjectModelConverter;
import com.bowtye.decisive.models.Option;
import com.bowtye.decisive.models.ProjectWithDetails;

public class ProjectDetailsViewModel extends BaseProjectDetailsViewModel {

    public ProjectDetailsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<ProjectWithDetails> getProjectFirebase(int id, String firebaseId, boolean isTemplate){
        if(isTemplate){
            return getProject(id, firebaseId, true);
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

    public void deleteOptionFirebase(Option option, int position, Context context){
        mRepo.deleteOptionFirebase(option, position, context);
    }

    public void uploadImageToFirebase(int index, ProjectWithDetails projectWithDetails, Context context, ProjectRepository.DoneUploadingImageCallback callback){
        mRepo.uploadImageToFirebase(index,
                ProjectModelConverter.projectWithDetailsToProjectFirebase(projectWithDetails, projectWithDetails.getProject().getFirebaseId()),
                context, callback);
    }

    public void uploadImagesToFirebase(ProjectWithDetails projectWithDetails, Context context){
        mRepo.uploadImagesToFirebase(ProjectModelConverter.projectWithDetailsToProjectFirebase(projectWithDetails,
                projectWithDetails.getProject().getFirebaseId()), context);
    }
}
