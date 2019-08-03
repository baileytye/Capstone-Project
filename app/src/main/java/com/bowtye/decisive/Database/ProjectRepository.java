package com.bowtye.decisive.Database;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.ProjectWithDetails;

import java.util.List;

import timber.log.Timber;

public class ProjectRepository {

    private static ProjectListDao projectListDao;

    private static ProjectRepository instance;

    private LiveData<List<ProjectWithDetails>> projects;
    private MutableLiveData<ProjectWithDetails> selectedProject;

    public static ProjectRepository getInstance(Application application) {
        if(instance == null) {
            synchronized (ProjectRepository.class) {
                if(instance == null) {
                    instance = new ProjectRepository(application);
                }
            }
        }
        return instance;
    }

    @SuppressLint("StaticFieldLeak")
    private ProjectRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application.getApplicationContext());

        projectListDao = database.projectListDao();

        projects = projectListDao.getProjects();
        selectedProject = new MutableLiveData<>();
    }

    public void insertProjectWithDetails(ProjectWithDetails projectWithDetails){
        new InsertAsyncTask().execute(projectWithDetails);
    }

    public void insertOption(Option option, int projectId){
        option.setProjectId(projectId);
        new InsertOptionAsyncTask().execute(option);
    }

    public void clearTables(){
        new ClearAsyncTask().execute();
    }

    public void clearOptionTable(){
        new ClearOptionsAsyncTask().execute();
    }

    public void clearRequirementsTable(){
        new ClearRequirementsAsyncTask().execute();
    }

    public LiveData<List<ProjectWithDetails>> getProjects(){
        Timber.d("GetProjects called");
        return projects;
    }

    public LiveData<ProjectWithDetails> getSelectedProject(int id){
        return projectListDao.loadProjectById(id);
    }

    private static class InsertAsyncTask extends AsyncTask<ProjectWithDetails,Void,Void> {

        @Override
        protected Void doInBackground(ProjectWithDetails... projectWithDetails) {
            projectListDao.insertProjectWithDetails(projectWithDetails[0]);
            return null;
        }
    }

    private static class InsertOptionAsyncTask extends AsyncTask<Option,Void,Void> {

        @Override
        protected Void doInBackground(Option... options) {
            projectListDao.insertOption(options[0]);
            return null;
        }
    }

    public void insert(final ProjectWithDetails project){
        new InsertAsyncTask().execute(project);
    }

    public void delete(final ProjectWithDetails project){
        new DeleteAsyncTask().execute(project);
    }

    public void deleteOption(final Option option){
        new DeleteOptionAsyncTask().execute(option);
    }

    private static class DeleteAsyncTask extends AsyncTask<ProjectWithDetails, Void, Void>{

        @Override
        protected Void doInBackground(ProjectWithDetails... projects) {
            projectListDao.deleteProjectWithDetails(projects[0]);
            Timber.d("Deleted project: %s", projects[0].getProject().getName());
            return null;
        }
    }

    private static class DeleteOptionAsyncTask extends AsyncTask<Option, Void, Void>{

        @Override
        protected Void doInBackground(Option... options) {
            projectListDao.deleteOption(options[0]);
            Timber.d("Deleted option: %s, from project: %s", options[0].getName(), options[0].getProjectId());
            return null;
        }
    }

    private static class ClearAsyncTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            projectListDao.clearAllTables();
            return null;
        }
    }

    private static class ClearOptionsAsyncTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            projectListDao.clearOptionTable();
            return null;
        }
    }

    private static class ClearRequirementsAsyncTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            projectListDao.clearRequirementTable();
            return null;
        }
    }
}
