package com.bowtye.decisive.Database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bowtye.decisive.Models.Project;
import com.bowtye.decisive.Models.Requirement;

import java.util.List;

import timber.log.Timber;

public class ProjectRepository {

    private static ProjectsDao projectsDao;
    private static RequirementsDao requirementsDao;

    private LiveData<List<Project>> projects;

    public ProjectRepository(Application application){
        AppDatabase database = AppDatabase.getInstance(application.getApplicationContext());
        projectsDao = database.projectsDao();
        requirementsDao = database.requirementsDao();
        projects = getProjects();
    }

    public LiveData<List<Project>> getProjects(){
        if(projects == null ){
            projects = projectsDao.loadProjects();
            if(projects.getValue() != null){
                for (Project p: projects.getValue()
                ) {
                    p.setRequirements(requirementsDao.loadRequirementsWithProjectId(p.getId()).getValue());
                    Timber.d("Number of requirements: %d",((p.getRequirements() != null) ? p.getRequirements().size() : 0));
                }
            }
        }
        return projects;
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

            Timber.d("Requirements length: %d", projects[0].getRequirements().size());
            for (Requirement r:
                 projects[0].getRequirements()) {
                r.setProjectId(id);
                requirementsDao.insertRequirement(r);
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
