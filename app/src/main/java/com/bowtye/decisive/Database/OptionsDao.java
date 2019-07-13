package com.bowtye.decisive.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.Requirement;

import java.util.List;

@Dao
public interface OptionsDao {

    @Query("SELECT * FROM option ORDER BY optionId")
    LiveData<List<Option>> loadOptions();

    @Query("SELECT * FROM option WHERE projectId = :selectId ORDER BY optionId")
    LiveData<List<Option>> loadOptionsWithProjectId(int selectId);

    @Query("DELETE FROM option")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOption(Option option);

    @Delete
    void deleteOption(Option option);
}
