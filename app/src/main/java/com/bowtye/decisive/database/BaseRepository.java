package com.bowtye.decisive.database;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bowtye.decisive.models.Option;
import com.bowtye.decisive.models.OptionFirebase;
import com.bowtye.decisive.models.ProjectFirebase;
import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.models.Requirement;
import com.bowtye.decisive.utils.ProjectModelConverter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class BaseRepository {

    private static final String TEMPLATES_COLLECTION = "templates";

    private static ProjectListDao projectListDao;

    private LiveData<List<ProjectWithDetails>> projects;
    private MutableLiveData<List<ProjectWithDetails>> templates;
    private MutableLiveData<ProjectWithDetails> selectedTemplate;

    @SuppressLint("StaticFieldLeak")
    BaseRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application.getApplicationContext());

        projectListDao = database.projectListDao();

        projects = projectListDao.getProjects();
        selectedTemplate = new MutableLiveData<>();
    }

    public void insertOption(Option option, int projectId){
        option.setProjectId(projectId);
        Timber.d("Project: %s inserted into Room database", option.getName());
        new InsertOptionAsyncTask().execute(option);
    }

    public void updateOption(Option option, int position){
        Timber.d("Project: %s updating into Room database", option.getName());
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
        Timber.d("Getting projects from Room database");
        return projects;
    }

    public LiveData<List<ProjectWithDetails>> getTemplates() {

        if(templates == null) {
            templates = new MutableLiveData<>();
        }

        Timber.d("Getting templates from firebase");

        CollectionReference templatesReference = FirebaseFirestore.getInstance().collection(TEMPLATES_COLLECTION);

        Query query = templatesReference.orderBy("name");

        query.addSnapshotListener((value, e) -> {
            if (e != null) {
                Timber.w("Listener of firebase projects failed");
                templates.setValue(null);
            }

            List<ProjectWithDetails> temp = new ArrayList<>();
            if (value != null) {
                for (QueryDocumentSnapshot doc : value) {

                    ProjectFirebase projectFirebase = doc.toObject(ProjectFirebase.class);
                    ProjectWithDetails projectWithDetails = ProjectModelConverter.projectFirebaseToProjectWithDetails(projectFirebase);
                    temp.add(projectWithDetails);
                }
                templates.setValue(temp);
            } else {
                Timber.w("Firestore value is null");
            }
        });

        return templates;
    }

    public LiveData<ProjectWithDetails> getSelectedProject(int id){
        Timber.d("Getting selected project from Room database");
        return projectListDao.loadProjectById(id);
    }

    public LiveData<ProjectWithDetails> getSelectedTemplate(int id, String firebaseId) {

        Timber.d("Getting template from Firebase with id %s", firebaseId);

        DocumentReference templateReference = FirebaseFirestore.getInstance().collection(TEMPLATES_COLLECTION).document(firebaseId);

        templateReference.addSnapshotListener((value, e) -> {
            if (e != null) {
                Timber.w("Listener of firebase projects failed");
                selectedTemplate.setValue(null);
            }

            if (value != null) {
                ProjectFirebase projectFirebase = value.toObject(ProjectFirebase.class);
                ProjectWithDetails projectWithDetails = ProjectModelConverter.projectFirebaseToProjectWithDetails(projectFirebase);

                selectedTemplate.setValue(projectWithDetails);

            } else {
                Timber.w("Firestore value is null");
            }
        });

        return selectedTemplate;
    }

    public LiveData<Option> getSelectedOption(int id){
        Timber.d("Getting selected option from Room database");
        return projectListDao.loadOptionById(id);
    }

    public void insertProjectWithDetails(final ProjectWithDetails project){
        Timber.d("Inserting Project: %s into Room database", project.getProject().getName());
        new InsertProjectWithDetailsAsyncTask().execute(project);
    }

    public void deleteProjectWithDetails(final ProjectWithDetails project){
        Timber.d("Deleting project from Room database");
        new DeleteProjectWithDetailsAsyncTask().execute(project);
    }

    public void deleteOption(final Option option){
        new DeleteOptionAsyncTask().execute(option);
    }

    private static class InsertProjectWithDetailsAsyncTask extends AsyncTask<ProjectWithDetails,Void,Void> {

        @Override
        protected Void doInBackground(ProjectWithDetails... projectWithDetails) {
            projectListDao.deleteRequirementsWithProjectId(projectWithDetails[0].getProject().getId());
            projectListDao.insertProjectWithDetails(projectWithDetails[0]);
            return null;
        }
    }

    private static class InsertOptionAsyncTask extends AsyncTask<Option,Void,Void> {

        @Override
        protected Void doInBackground(Option... options) {
            options[0].setDateCreated(new Date());
            projectListDao.insertOption(options[0]);
            return null;
        }
    }

    private static class DeleteProjectWithDetailsAsyncTask extends AsyncTask<ProjectWithDetails, Void, Void>{

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

    //TODO: REMOVE EVERYTHING BELOW WHEN TEMPLATES DONE

    private static class InsertTemplateAsyncTask extends AsyncTask<ProjectFirebase, Void, Void> {

        @Override
        protected Void doInBackground(ProjectFirebase... projectFirebases) {
            ProjectFirebase projectFirebase = projectFirebases[0];

            DocumentReference projectDocumentRef;

            if (projectFirebase.getProjectId() == null) {
                Timber.d("New project being inserted into firebase");
                projectDocumentRef =
                        FirebaseFirestore.getInstance().collection(TEMPLATES_COLLECTION).document();

                projectFirebase.setProjectId(projectDocumentRef.getId());
            } else {
                Timber.d("Project being updated in firebase");
                projectDocumentRef =
                        FirebaseFirestore.getInstance().collection(TEMPLATES_COLLECTION)
                                .document(projectFirebase.getProjectId());
            }

            projectDocumentRef.set(projectFirebase)
                    .addOnFailureListener(
                            e -> Timber.d("Failed to add %s to firebase error: %s",
                                    projectFirebase.getName(), e.getMessage()))
                    .addOnSuccessListener(
                            documentReference -> Timber.d("Successfully added %s to firebase", projectFirebase.getName()));
            return null;
        }
    }

    public void insertTemplate(ProjectWithDetails projectWithDetails){
        projectWithDetails.getProject().setDateCreated(new Date());

        ProjectFirebase projectFirebase =
                ProjectModelConverter.projectWithDetailsToProjectFirebase(projectWithDetails, "0");

        new InsertTemplateAsyncTask().execute(projectFirebase);
    }

    public void insertTemplateOption(Option option, int projectId) {

        Timber.d("Inserting option into firebase");
        ProjectWithDetails projectWithDetails = selectedTemplate.getValue();
        if (projectWithDetails != null) {
            projectWithDetails.getOptionList().add(option);
            ProjectFirebase projectFirebase = ProjectModelConverter
                    .projectWithDetailsToProjectFirebase(projectWithDetails, "0");

            new InsertTemplateAsyncTask().execute(projectFirebase);
        } else {
            Timber.e("Selected project is null");
        }

    }

    public void deleteOptionTemplate(Option option, int position) {

            Timber.d("Removing option in firebase");
        if(selectedTemplate.getValue() != null) {

            List<Option> options = new ArrayList<>(selectedTemplate.getValue().getOptionList());

            options.remove(position);

            if(!option.getImagePath().equals("")){
                deleteImage(option.getName());
            }

            List<OptionFirebase> optionsFirebase = ProjectModelConverter.optionToOptionFirebaseList(
                    options,
                    selectedTemplate.getValue().getProject().getFirebaseId());

            updateOptionList(optionsFirebase);
        }
    }

    private static void deleteImage(String name){
        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child("images/users/0"
                + "/" + name + ".jpg");

        imageReference.delete();
    }

    private void updateOptionList(List<OptionFirebase> options){
        DocumentReference projectDocumentRef;

        Timber.d("Option list being updated");
        projectDocumentRef =
                FirebaseFirestore.getInstance().collection(TEMPLATES_COLLECTION).document(
                        Objects.requireNonNull(selectedTemplate.getValue()).getProject().getFirebaseId());

        projectDocumentRef.update("options", options)
                .addOnSuccessListener(d -> Timber.d("Successfully updated options"))
                .addOnFailureListener(e -> Timber.e("Failed to update options %s", e.getMessage()));
    }
}
