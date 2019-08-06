package com.bowtye.decisive.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bowtye.decisive.Database.ProjectRepository;
import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.Requirement;

import java.util.List;

public class OptionDetailsViewModel extends AndroidViewModel {

    private ProjectRepository mRepo;
    private List<Requirement> mRequirements;
    private LiveData<Option> mOption;

    public OptionDetailsViewModel(@NonNull Application application) {
        super(application);
        mRepo = ProjectRepository.getInstance(application);
    }

    public LiveData<Option> getOption(int id){
        if(mOption == null){
            mOption = new MutableLiveData<>();
            loadOption(id);
        }
        return mOption;
    }

    private void loadOption(int id){
        mOption = mRepo.getSelectedOption(id);
    }

    public void insertOption(Option option, int projectId){
        mRepo.insertOption(option, projectId);
    }

    public void deleteOption(Option option){
        mRepo.deleteOption(option);
    }

}
