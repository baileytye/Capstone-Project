package com.bowtye.decisive.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

import com.bowtye.decisive.database.ProjectRepository;
import com.bowtye.decisive.models.ProjectWithDetails;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<ProjectWithDetails>> mProjects;
    private final ProjectRepository mRepo;
    private LiveData<List<ProjectWithDetails>> mTemplates;


    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepo = ProjectRepository.getInstance(application);
    }


    public LiveData<List<ProjectWithDetails>> getProjects() {
        if (mProjects == null) {
            mProjects = new MutableLiveData<>();
            loadProjects();
        }
        return mProjects;
    }

    public LiveData<List<ProjectWithDetails>> getTemplates() {
        if(mTemplates == null) {
            mTemplates = new MutableLiveData<>();
            loadTemplates();
        }
        return mTemplates;
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

    public void insertProjectWithDetails(ProjectWithDetails p) {
        mRepo.insertProjectWithDetails(p);
    }

    private void loadProjects() {
        mProjects = mRepo.getProjects();
    }

    private void loadTemplates(){
        mTemplates = mRepo.getTemplates();
    }

    public void clearProjects() {
        mRepo.clearTables();
    }

    public void clearOptions() {
        mRepo.clearOptionTable();
    }

    public void clearRequirements() {
        mRepo.clearRequirementsTable();
    }

    public void deleteProject(ProjectWithDetails p) {
        mRepo.deleteProjectWithDetails(p);
    }
}
