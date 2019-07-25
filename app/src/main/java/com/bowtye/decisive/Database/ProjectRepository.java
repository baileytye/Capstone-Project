package com.bowtye.decisive.Database;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.Project;
import com.bowtye.decisive.Models.Requirement;

import java.util.List;

import timber.log.Timber;

public class ProjectRepository {

    private static ProjectsDao projectsDao;
    private static RequirementsDao requirementsDao;
    private static OptionsDao optionsDao;

    private static ProjectRepository instance;

    private LiveData<List<Project>> projects;
    private LiveData<Project> selectedProject;

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
    private ProjectRepository(Application application){
        AppDatabase database = AppDatabase.getInstance(application.getApplicationContext());
        projectsDao = database.projectsDao();
        requirementsDao = database.requirementsDao();
        optionsDao = database.optionsDao();

        projects = projectsDao.loadProjects();
        projects = Transformations.switchMap(projects, (List<Project> input) -> {
            final MutableLiveData<List<Project>> projectsMutable = new MutableLiveData<>();
            new AsyncTask<List<Project>, Void, List<Project>>(){

                @Override
                protected List<Project> doInBackground(List<Project>... lists) {
                    for(int i = 0; i < lists[0].size(); i++){
                        lists[0].get(i).setOptions(optionsDao.loadOptionsWithProjectId(lists[0].get(i).getId()));
                        lists[0].get(i).setRequirements(requirementsDao.loadRequirementsWithProjectId(lists[0].get(i).getId()));
                    }
                    return lists[0];
                }

                @Override
                protected void onPostExecute(List<Project> projects) {
                    super.onPostExecute(projects);
                    projectsMutable.postValue(projects);
                }
            }.execute(input);
            return projectsMutable;
        });
    }

    public LiveData<List<Project>> getProjects(){
        Timber.d("GetProjects called");
        return projects;
    }

    public LiveData<Project> updateSelectedProject(int id){
        selectedProject = projectsDao.loadProjectById(id);
        selectedProject = Transformations.switchMap(selectedProject, input -> {
            final MutableLiveData<Project> projectMutable = new MutableLiveData<>();
            new AsyncTask<Project, Void, Project> (){

                @Override
                protected Project doInBackground(Project... projects) {
                    projects[0].setOptions(optionsDao.loadOptionsWithProjectId(projects[0].getId()));
                    projects[0].setRequirements(requirementsDao.loadRequirementsWithProjectId(projects[0].getId()));
                    return projects[0];
                }

                @Override
                protected void onPostExecute(Project project) {
                    super.onPostExecute(project);
                    projectMutable.postValue(project);
                }
            }.execute(input);
            return projectMutable;
        });
        return selectedProject;
    }

    public void insert(final Project project){
        new InsertAsyncTask().execute(project);
    }

    public void delete(final Project project){
        new DeleteAsyncTask().execute(project);
    }

    public void clearTable(){
        new ClearAsyncTask().execute();
    }

    private static class InsertAsyncTask extends AsyncTask<Project,Void,Void> {

        @Override
        protected Void doInBackground(Project... projects) {
            Timber.d("Inserting Project: %s", projects[0].getName());
            int id = (int) projectsDao.insertProject(projects[0]);

            Timber.d("Requirements length: %d", (projects[0].getRequirements() != null) ? projects[0].getRequirements().size() : null);
            for (Requirement r: projects[0].getRequirements()) {
                r.setProjectId(id);
                requirementsDao.insertRequirement(r);
            }
            Timber.d("Options length: %d", (projects[0].getOptions() != null) ? projects[0].getOptions().size() : null);
            for (Option o: projects[0].getOptions()) {
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

    private static class DeleteAsyncTask extends AsyncTask<Project, Void, Void>{

        @Override
        protected Void doInBackground(Project... projects) {
            projectsDao.deleteProject(projects[0]);
            Timber.d("Deleted project: %s", projects[0].getName());
            return null;
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
