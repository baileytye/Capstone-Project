package com.bowtye.decisive.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

import com.bowtye.decisive.database.ProjectRepository;
import com.bowtye.decisive.models.ProjectFirebase;
import com.bowtye.decisive.utils.RatingUtils;
import com.bowtye.decisive.models.Option;
import com.bowtye.decisive.models.Project;
import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.models.Requirement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

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

    public void insertDummyProject() {
        Timber.d("Inserting dummy project");
        Requirement r1 = new Requirement("Bedrooms",
                Requirement.Type.number, Requirement.Importance.normal, 3.0,
                "", Requirement.WEIGHT_NORMAL, true);
        Requirement r2 = new Requirement("Outside Colors",
                Requirement.Type.averaging, Requirement.Importance.normal, Requirement.AVERAGE,
                "", Requirement.WEIGHT_NORMAL, true);
        Requirement r3 = new Requirement("Garage",
                Requirement.Type.checkbox, Requirement.Importance.high, 1.0,
                "", Requirement.WEIGHT_HIGH, true);

        List<Requirement> requirements = new ArrayList<>(Arrays.asList(r1, r2, r3));

        List<Double> requirementValues = new ArrayList<>(Arrays.asList(1.0, 2.0, 0.0));

        Option option1 = new Option("House 1", 100000, (float) 0, false, requirementValues, "First test house", "");

        option1.setRating(
                RatingUtils.calculateOptionRating(
                        RatingUtils.calculateAllRequirementRatings(requirements, requirementValues),
                        requirements
                )
        );

        Project p = new Project("House", false);

        ProjectWithDetails projectWithDetails = new ProjectWithDetails(p, Collections.singletonList(option1), requirements);

        mRepo.insertProjectWithDetails(projectWithDetails);
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
