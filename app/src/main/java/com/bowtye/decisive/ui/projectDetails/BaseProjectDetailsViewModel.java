package com.bowtye.decisive.ui.projectDetails;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bowtye.decisive.database.BaseRepository;
import com.bowtye.decisive.database.ProjectRepository;
import com.bowtye.decisive.models.Option;
import com.bowtye.decisive.models.ProjectWithDetails;

public class BaseProjectDetailsViewModel extends AndroidViewModel {
    LiveData<ProjectWithDetails> mProject;
    ProjectRepository mRepo;

    BaseProjectDetailsViewModel(@NonNull Application application) {
        super(application);
        mRepo = ProjectRepository.getInstance(application);
    }

    public LiveData<ProjectWithDetails> getProject(int id, String firebaseId, boolean isTemplate){
        if(mProject == null){
            mProject = new MutableLiveData<>();
            loadProject(id, firebaseId, isTemplate);
        }
        return mProject;
    }

    public void insertProjectWithDetails(ProjectWithDetails p) {
        mRepo.insertProjectWithDetails(p);
    }


    public void resizeOptionValuesList(ProjectWithDetails p){
        if((p != null) && (p.getOptionList().size() > 0) &&
                (p.getOptionList().get(0).getRequirementValues().size() < p.getRequirementList().size())){
            for(int i = 0; i < p.getOptionList().size(); i++){

                for(int j = p.getOptionList().get(i).getRequirementValues().size();
                    j < p.getRequirementList().size(); j ++){
                    p.getOptionList().get(i).getRequirementValues().add(0.0);
                }
            }
        }
    }

    public void insertOption(Option option, int projectId){
        mRepo.insertOption(option, projectId);
    }

    private void loadProject(int id, String firebaseId, boolean isTemplate){
        if(isTemplate){
            mProject = mRepo.getSelectedTemplate(id, firebaseId);
        } else {
            mProject = mRepo.getSelectedProject(id);
        }
    }

    public void deleteOption(Option option, Context context){
        mRepo.deleteOption(option, context);
    }

    public void deleteProject(ProjectWithDetails p, Context context) {
        mRepo.deleteProjectWithDetails(p);
        new DeleteImagesAsyncTask(p).execute(context);
    }

    public static class DeleteImagesAsyncTask extends AsyncTask<Context, Void, Void> {

        ProjectWithDetails mProjectWithDetails;

        DeleteImagesAsyncTask(ProjectWithDetails projectWithDetails){
            mProjectWithDetails = projectWithDetails;
        }

        @Override
        protected Void doInBackground(Context... contexts) {
            for (Option option : mProjectWithDetails.getOptionList()) {
                BaseRepository.deleteImage(option, contexts[0]);
            }
            return null;
        }
    }
}
