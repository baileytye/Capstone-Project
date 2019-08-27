package com.bowtye.decisive.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.bowtye.decisive.models.Option;
import com.bowtye.decisive.models.Project;
import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.models.Requirement;

import java.util.List;

@Dao
public abstract class ProjectListDao {

    @Transaction
    @Query("SELECT * FROM project")
    abstract LiveData<List<ProjectWithDetails>> getProjects();

    @Transaction
    @Query("SELECT * FROM project WHERE id = :id")
    abstract LiveData<ProjectWithDetails> loadProjectById(int id);

    @Query("SELECT * FROM option WHERE optionId = :id")
    abstract LiveData<Option> loadOptionById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long insertProject(Project project);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long insertOption(Option option);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertAllOptions(List<Option> options);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long insertRequirement(Requirement requirement);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertAllRequirements(List<Requirement> requirements);

    @Delete
    abstract void deleteOption(Option option);

    @Delete
    abstract void deleteRequirement(Requirement requirement);

    @Delete
    abstract void deleteProject(Project project);

    @Query("DELETE FROM option WHERE projectId = :id")
    abstract void deleteOptionsWithProjectId(int id);

    @Query("DELETE FROM requirement WHERE projectId = :id")
    abstract void deleteRequirementsWithProjectId(int id);

    @Query("DELETE FROM project")
    abstract void clearProjectTable();

    @Query("DELETE FROM option")
    abstract void clearOptionTable();

    @Query("DELETE FROM requirement")
    abstract void clearRequirementTable();

    @Transaction
    public void insertProjectWithDetails(ProjectWithDetails projectDetails){
        long id = insertProject(projectDetails.getProject());

        for(Option option: projectDetails.getOptionList()){
            option.setProjectId((int)id);
        }
        insertAllOptions(projectDetails.getOptionList());

        for(Requirement requirement: projectDetails.getRequirementList()){
            requirement.setProjectId((int)id);
        }
        insertAllRequirements(projectDetails.getRequirementList());

    }

    @Transaction
    public void deleteProjectWithDetails(ProjectWithDetails projectWithDetails){
        deleteOptionsWithProjectId(projectWithDetails.getProject().getId());
        deleteRequirementsWithProjectId(projectWithDetails.getProject().getId());
        deleteProject(projectWithDetails.getProject());
    }

    @Transaction
    public void clearAllTables(){
        clearOptionTable();
        clearProjectTable();
        clearRequirementTable();
    }

}
