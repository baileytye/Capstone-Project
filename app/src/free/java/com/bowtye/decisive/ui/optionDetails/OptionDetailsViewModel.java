package com.bowtye.decisive.ui.optionDetails;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import com.bowtye.decisive.database.BaseRepository;
import com.bowtye.decisive.models.Option;

public class OptionDetailsViewModel extends BaseOptionDetailsViewModel{

    public OptionDetailsViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void deleteImage(Option option, Context context) {
        BaseRepository.deleteImage(option, context);
    }

}
