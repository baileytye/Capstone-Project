package com.bowtye.decisive.Database;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.bowtye.decisive.Models.Project;

import java.util.List;

@Dao
public interface ProjectsDao {

    @Query("SELECT * FROM project ORDER BY id")
    LiveData<List<Project>> loadProjects();

    @Query("DELETE FROM project")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertProject(Project project);

    @Delete
    void deleteProject(Project project);

    @Query("SELECT * FROM project WHERE id = :id")
    LiveData<Project> loadProjectById(int id);

}
