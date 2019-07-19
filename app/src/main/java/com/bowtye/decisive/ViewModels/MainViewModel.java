package com.bowtye.decisive.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

import com.bowtye.decisive.Database.ProjectRepository;
import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.Project;
import com.bowtye.decisive.Models.Requirement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Project>> mProjects;
    private final ProjectRepository mRepo;


    public MainViewModel(@NonNull Application application){
        super(application);
        mRepo = ProjectRepository.getInstance(application);
        insertDummyProject();
    }


    public LiveData<List<Project>> getProjects(){
        if(mProjects == null){
            mProjects = new MutableLiveData<>();
            loadProjects();
        }
        return mProjects;
    }

    public void insertProject(Project p){
        mRepo.insert(p);
    }

    public void insertDummyProject(){
        Requirement r1 = new Requirement("Requirement 1",
                Requirement.Type.number, Requirement.Importance.normal, 0,
                "", 0, false, 0);
        Requirement r2 = new Requirement("Requirement 2",
                Requirement.Type.number, Requirement.Importance.normal, 0,
                "", 0, false, 0);
        Requirement r3 = new Requirement("Requirement 3",
                Requirement.Type.number, Requirement.Importance.normal, 0,
                "", 0, false, 0);

        List<Requirement> requirements = new ArrayList<>(Arrays.asList(r1,r2,r3));

        List<Double> requirementValues = new ArrayList<>(Arrays.asList(1.0, 2.0, 3.0));

        Option option1 = new Option("Option 1", 100000,0,false,requirementValues,"",null);

        Project p = new Project(requirements, Collections.singletonList(option1), "Project Test", true);

        mRepo.insert(p);
    }

    private void loadProjects(){
        mProjects = mRepo.getProjects();
    }

    public void clearProjects(){
        mRepo.clearTable();
    }
}
