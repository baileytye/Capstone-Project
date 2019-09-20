package com.bowtye.decisive.ui.optionDetails;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bowtye.decisive.database.ProjectRepository;
import com.bowtye.decisive.models.Option;

public abstract class BaseOptionDetailsViewModel extends AndroidViewModel {

    protected ProjectRepository mRepo;
    private LiveData<Option> mOption;

    public BaseOptionDetailsViewModel(@NonNull Application application) {
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

    public void updateOption(Option option, int position){
        mRepo.updateOption(option, position);
    }

    public abstract void deleteImage(Option option, Context context);
}
