package com.bowtye.decisive.database;

import android.app.Application;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bowtye.decisive.utils.FileUtils;
import com.bowtye.decisive.utils.ProjectModelConverter;
import com.bowtye.decisive.models.Option;
import com.bowtye.decisive.models.OptionFirebase;
import com.bowtye.decisive.models.ProjectFirebase;
import com.bowtye.decisive.models.ProjectWithDetails;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class ProjectRepository extends BaseRepository {

    private static ProjectRepository instance;
    private FirebaseFirestore firebaseDb;
    private MutableLiveData<List<ProjectWithDetails>> projects;
    private MutableLiveData<ProjectWithDetails> selectedProject;
    private MutableLiveData<Option> selectedOption;
    private ListenerRegistration mSelectedOptionRegestration;
    private ListenerRegistration mSelectedProjectRegestration;

    private static final String PROJECT_COLLECTION = "projects";

    public static ProjectRepository getInstance(Application application) {
        if (instance == null) {
            synchronized (ProjectRepository.class) {
                if (instance == null) {
                    instance = new ProjectRepository(application);
                }
            }
        }
        return instance;
    }

    private ProjectRepository(Application application) {
        super(application);
        firebaseDb = FirebaseFirestore.getInstance();
        projects = new MutableLiveData<>();
        selectedProject = new MutableLiveData<>();
        selectedOption = new MutableLiveData<>();
    }

    @Override
    public void insertProjectWithDetails(ProjectWithDetails projectWithDetails) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        projectWithDetails.getProject().setDateCreated(new Date());

        if (user == null || user.isAnonymous()) {
            super.insertProjectWithDetails(projectWithDetails);
        } else {
            ProjectFirebase projectFirebase =
                    ProjectModelConverter.projectWithDetailsToProjectFirebase(projectWithDetails, user.getUid());
            Timber.d("Inserting project: %s into firebase", projectWithDetails.getProject().getName());

            new InsertProjectAsyncTask().execute(projectFirebase);
        }
    }

    @Override
    public void insertOption(Option option, int projectId) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null || user.isAnonymous()) {
            super.insertOption(option, projectId);
        } else {

            Timber.d("Inserting option into firebase");
            ProjectWithDetails projectWithDetails = selectedProject.getValue();
            if (projectWithDetails != null) {
                projectWithDetails.getOptionList().add(option);
                ProjectFirebase projectFirebase = ProjectModelConverter
                        .projectWithDetailsToProjectFirebase(projectWithDetails, user.getUid());

                new InsertProjectAsyncTask().execute(projectFirebase);
            } else {
                Timber.e("Selected project is null");
            }
        }
    }


    public void deleteOptionFirebase(Option option, int position) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null || user.isAnonymous()) {
            super.deleteOption(option);
        } else {
            Timber.d("Removing option in firebase");
            if(selectedProject.getValue() != null) {

                mSelectedOptionRegestration.remove();

                List<Option> options = new ArrayList<>(selectedProject.getValue().getOptionList());

                options.remove(position);

                if(!option.getImagePath().equals("")){
                    deleteImage(option.getName());
                }

                List<OptionFirebase> optionsFirebase = ProjectModelConverter.optionToOptionFirebaseList(
                        options,
                        selectedProject.getValue().getProject().getFirebaseId());

                updateOptionList(optionsFirebase);
            }
        }
    }

    @Override
    public void updateOption(Option option, int position){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null || user.isAnonymous()){
            super.updateOption(option, position);
        } else {
            Timber.d("Updating option in firebase");
            if(selectedProject.getValue() != null) {

                List<Option> options = new ArrayList<>(selectedProject.getValue().getOptionList());

                options.set(position, option);

                List<OptionFirebase> optionsFirebase = ProjectModelConverter.optionToOptionFirebaseList(
                        options,
                        selectedProject.getValue().getProject().getFirebaseId());

                updateOptionList(optionsFirebase);
            }
        }
    }

    @Override
    public LiveData<Option> getSelectedOption(int id) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null || user.isAnonymous()) {
            return super.getSelectedOption(id);
        } else {
            if (selectedProject.getValue() != null) {

                DocumentReference projectReference = firebaseDb.collection(PROJECT_COLLECTION)
                        .document(selectedProject.getValue().getProject().getFirebaseId());

                mSelectedOptionRegestration = projectReference.addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Timber.w("Listener of firebase projects failed");
                        selectedProject.setValue(null);
                        return;
                    }

                    if (value != null) {
                        ProjectFirebase projectFirebase = value.toObject(ProjectFirebase.class);
                        ProjectWithDetails projectWithDetails = ProjectModelConverter.projectFirebaseToProjectWithDetails(projectFirebase);

                        //Not sure why I need these checks, can't figure it out, but it fixes two errors:
                        //Deleting only option crash, and seeing details after adding a requirement crash
                        if(projectWithDetails != null && !(projectWithDetails.getOptionList().size() == 0)) {
                            selectedOption.setValue(projectWithDetails.getOptionList().get(id));
                        }

                    } else {
                        Timber.w("Firestore value is null");
                    }
                });

            } else {
                Timber.e("selected project is null");
            }
        }
        return selectedOption;
    }

    @Override
    public LiveData<List<ProjectWithDetails>> getProjects() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null || user.isAnonymous()) {
            return super.getProjects();
        } else {
            Timber.d("Getting projects from firebase");

            CollectionReference projectsReference = firebaseDb.collection(PROJECT_COLLECTION);


            Query query = projectsReference.whereEqualTo("userId", user.getUid()).orderBy("dateCreated", Query.Direction.DESCENDING);

            query.addSnapshotListener((value, e) -> {
                if (e != null) {
                    Timber.w("Listener of firebase projects failed");
                    projects.setValue(null);
                }

                List<ProjectWithDetails> temp = new ArrayList<>();
                if (value != null) {
                    for (QueryDocumentSnapshot doc : value) {

                        ProjectFirebase projectFirebase = doc.toObject(ProjectFirebase.class);
                        ProjectWithDetails projectWithDetails = ProjectModelConverter.projectFirebaseToProjectWithDetails(projectFirebase);
                        temp.add(projectWithDetails);
                    }
                    projects.setValue(temp);
                } else {
                    Timber.w("Firestore value is null");
                }
            });
        }
        return projects;
    }

    public LiveData<ProjectWithDetails> getSelectedProjectFirebase(int id, String firebaseId) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null || user.isAnonymous()){
            return super.getSelectedProject(id);
        } else {
            Timber.d("Getting project from Firebase with id %s", firebaseId);

            DocumentReference projectReference = firebaseDb.collection(PROJECT_COLLECTION).document(firebaseId);

            mSelectedProjectRegestration = projectReference.addSnapshotListener((value, e) -> {
                if (e != null) {
                    Timber.w("Listener of firebase projects failed");
                    selectedProject.setValue(null);
                }

                if (value != null) {
                    ProjectFirebase projectFirebase = value.toObject(ProjectFirebase.class);
                    ProjectWithDetails projectWithDetails = ProjectModelConverter.projectFirebaseToProjectWithDetails(projectFirebase);

                    selectedProject.setValue(projectWithDetails);

                } else {
                    Timber.w("Firestore value is null");
                }
            });
        }

        return selectedProject;
    }

    @Override
    public void clearTables() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null || user.isAnonymous()) {
            super.clearTables();
        } else {
            CollectionReference projectsReference = firebaseDb.collection(PROJECT_COLLECTION);

            Query query = projectsReference.whereEqualTo("userId", user.getUid());

            //TODO add delete all
        }
    }

    @Override
    public void deleteProjectWithDetails(ProjectWithDetails project) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null || user.isAnonymous()) {
            super.deleteProjectWithDetails(project);
        } else {
            if(mSelectedProjectRegestration != null){
                mSelectedProjectRegestration.remove();
            }

            ProjectFirebase projectFirebase =
                    ProjectModelConverter.projectWithDetailsToProjectFirebase(project, user.getUid());

            Timber.d("Deleting project from firebase");
            new DeleteProjectAsyncTask().execute(projectFirebase);
            selectedProject.setValue(null);
        }
    }

    private void updateOptionList(List<OptionFirebase> options){
        DocumentReference projectDocumentRef;

        Timber.d("Option list being updated");
        projectDocumentRef =
                FirebaseFirestore.getInstance().collection(PROJECT_COLLECTION).document(
                        Objects.requireNonNull(selectedProject.getValue()).getProject().getFirebaseId());

        projectDocumentRef.update("options", options)
                .addOnSuccessListener(d -> Timber.d("Successfully updated options"))
                .addOnFailureListener(e -> Timber.e("Failed to update options %s", e.getMessage()));
    }

    private static void deleteImage(String name){
        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child("images/users/" +
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
                + "/" + name + ".jpg");

        imageReference.delete();
    }

    public void uploadImagesToFirebase(ProjectFirebase projectFirebase){
        new UploadImageUrlsAsyncTask().execute(projectFirebase);
    }

    private static class UploadImageUrlsAsyncTask extends AsyncTask<ProjectFirebase, Void, Void>{

        @Override
        protected Void doInBackground(ProjectFirebase... projectFirebases) {

            Timber.d("Uploading images to firebase");

            ProjectFirebase projectFirebase = projectFirebases[0];

            List<OptionFirebase> tempOptions = new ArrayList<>(projectFirebase.getOptions());

            List<Task<Uri>> tasks = new ArrayList<>();
            List<StorageTask<UploadTask.TaskSnapshot>> uploadTasks = new ArrayList<>();

            for(OptionFirebase option : tempOptions) {

                if(!option.getImagePath().equals("")) {

                    StorageReference imageReference = FirebaseStorage.getInstance().getReference().child("images/users/" +
                            Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
                            + "/" + option.getName() + ".jpg");

                    if (!option.getImagePath().substring(0, 4).equals("http")) {
                        FileUtils.rotateFile(null, Uri.parse(option.getImagePath()));

                        uploadTasks.add(imageReference.putFile(Uri.parse(option.getImagePath())).addOnSuccessListener(taskSnapshot -> {
                            tasks.add(imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                option.setImagePath(uri.toString());
                                Timber.d("Set firebase image url to %s", uri.toString());
                            }));
                        }));
                    }
                }
            }

            Tasks.whenAll(uploadTasks).addOnSuccessListener(aVoid -> Tasks.whenAll(tasks).addOnSuccessListener(v -> {
                Timber.d("Image paths finished updating, updating options");
                DocumentReference projectDocumentRef;

                projectDocumentRef =
                        FirebaseFirestore.getInstance().collection(PROJECT_COLLECTION).document(
                                projectFirebase.getProjectId());

                projectDocumentRef.update("options", tempOptions)
                        .addOnSuccessListener(d -> Timber.d("Successfully updated options"))
                        .addOnFailureListener(e -> Timber.e("Failed to update options %s", e.getMessage()));
            }));

            return null;
        }
    }


    private static class DeleteProjectAsyncTask extends AsyncTask<ProjectFirebase, Void, Void> {

        @Override
        protected Void doInBackground(ProjectFirebase... projectFirebases) {
            ProjectFirebase projectFirebase = projectFirebases[0];
            String projectId = projectFirebases[0].getProjectId();

            DocumentReference projectRef = FirebaseFirestore.getInstance().collection(PROJECT_COLLECTION).document(projectId);
            projectRef.delete()
                    .addOnFailureListener(
                            e -> Timber.d("Failed to delete %s from firebase error: %s",
                                    projectId, e.getMessage()))
                    .addOnSuccessListener(
                            documentReference -> Timber.d("Successfully deleted %s from firebase", projectId));

            for(OptionFirebase option : projectFirebase.getOptions()){

                if(!option.getImagePath().equals("")) {
                    deleteImage(option.getName());
                }
            }
            return null;
        }
    }

    private static class InsertProjectAsyncTask extends AsyncTask<ProjectFirebase, Void, Void> {

        @Override
        protected Void doInBackground(ProjectFirebase... projectFirebases) {
            ProjectFirebase projectFirebase = projectFirebases[0];

            DocumentReference projectDocumentRef;

            if (projectFirebase.getProjectId() == null) {
                Timber.d("New project being inserted into firebase");
                projectDocumentRef =
                        FirebaseFirestore.getInstance().collection(PROJECT_COLLECTION).document();

                projectFirebase.setProjectId(projectDocumentRef.getId());
            } else {
                Timber.d("Project being updated in firebase");
                projectDocumentRef =
                        FirebaseFirestore.getInstance().collection(PROJECT_COLLECTION)
                                .document(projectFirebase.getProjectId());
            }

            projectDocumentRef.set(projectFirebase)
                    .addOnFailureListener(
                            e -> Timber.d("Failed to add %s to firebase error: %s",
                                    projectFirebase.getName(), e.getMessage()))
                    .addOnSuccessListener(
                            documentReference -> {
                                Timber.d("Successfully added %s to firebase", projectFirebase.getName());
                            });
            return null;
        }
    }

}
