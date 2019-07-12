package com.bowtye.decisive.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.bowtye.decisive.Models.Requirement;

import java.util.List;

@Dao
public interface RequirementsDao {

    @Query("SELECT * FROM requirement ORDER BY reqId")
    LiveData<List<Requirement>> loadRequirements();

    @Query("SELECT * FROM requirement WHERE projectId = :selectId ORDER BY reqId")
    LiveData<List<Requirement>> loadRequirementsWithProjectId(int selectId);

    @Query("DELETE FROM requirement")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRequirement(Requirement requirement);

    @Delete
    void deleteRequirement(Requirement requirement);
}
