package com.bowtye.decisive.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bowtye.decisive.Database.ProjectRepository;
import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.ProjectWithDetails;

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

    public void insertProjectWithDetails(ProjectWithDetails p) {
        if((p != null) && (p.getOptionList().size() > 0) &&
                (p.getOptionList().get(0).getRequirementValues().size() < p.getRequirementList().size())){
            for(int i = 0; i < p.getOptionList().size(); i++){

                for(int j = p.getOptionList().get(i).getRequirementValues().size();
                    j < p.getRequirementList().size(); j ++){
                    p.getOptionList().get(i).getRequirementValues().add(0.0);
                }
            }
        }
        mRepo.insertProjectWithDetails(p);
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
