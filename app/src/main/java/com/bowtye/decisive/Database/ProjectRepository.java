package com.bowtye.decisive.Database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.Project;
import com.bowtye.decisive.Models.Requirement;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class ProjectRepository {

    private static ProjectsDao projectsDao;
    private static RequirementsDao requirementsDao;
    private static OptionsDao optionsDao;

    private static ProjectRepository instance;

    private LiveData<List<Project>> projects;

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

    private ProjectRepository(Application application){
        AppDatabase database = AppDatabase.getInstance(application.getApplicationContext());
        projectsDao = database.projectsDao();
        requirementsDao = database.requirementsDao();
        optionsDao = database.optionsDao();

        projects = projectsDao.loadProjects();
    }

    public LiveData<List<Project>> getProjects(){
        Timber.d("GetProjects called");
        return projects;
    }

    public List<Project> updateProjects(){
        List<Project> temp = projects.getValue();
        for (int i = 0; i < Objects.requireNonNull(temp).size(); i++) {
            temp.get(i).setRequirements(requirementsDao.loadRequirementsWithProjectId(temp.get(i).getId()).getValue());
            temp.get(i).setOptions(optionsDao.loadOptionsWithProjectId(temp.get(i).getId()).getValue());
        }
//        Timber.d("Requirements: %d", temp.get(0).getRequirements().size());
        return temp;
    }

    public LiveData<Project> getProjectById(int id){
        LiveData<Project> data = projectsDao.loadProjectById(id);
        Objects.requireNonNull(data.getValue()).setOptions(optionsDao.loadOptionsWithProjectId(id).getValue());
        data.getValue().setRequirements(requirementsDao.loadRequirementsWithProjectId(id).getValue());
        return data;
    }

    public void insert(final Project project){
        new InsertAsyncTask().execute(project);
    }

    public void clearTable(){
        new ClearAsyncTask().execute();
    }

    private static class InsertAsyncTask extends AsyncTask<Project,Void,Void> {

        @Override
        protected Void doInBackground(Project... projects) {
            Timber.d("Inserting Project: %s", projects[0].getName());
            int id = (int) projectsDao.insertProject(projects[0]);

            Timber.d("Requirements length: %d", (projects[0].getRequirements() != null) ? projects[0].getRequirements().size() : 0);
            for (Requirement r:
                 projects[0].getRequirements()) {
                r.setProjectId(id);
                requirementsDao.insertRequirement(r);
            }
            Timber.d("Options length: %d", (projects[0].getOptions() != null) ? projects[0].getOptions().size() : 0);
            for (Option o: projects[0].getOptions()
                 ) {
                o.setProjectId(id);
                optionsDao.insertOption(o);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Timber.d("Project inserted into database");
        }
    }

    private static class ClearAsyncTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            projectsDao.deleteAll();
            return null;
        }
    }
}
