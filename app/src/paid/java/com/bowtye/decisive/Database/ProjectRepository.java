package com.bowtye.decisive.Database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bowtye.decisive.Helpers.ProjectModelConverter;
import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.ProjectFirebase;
import com.bowtye.decisive.Models.ProjectWithDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class ProjectRepository extends BaseRepository {

    private static ProjectRepository instance;
    private FirebaseFirestore firebaseDb;
    private MutableLiveData<List<ProjectWithDetails>> projects;
    private MutableLiveData<ProjectWithDetails> selectedProject;
    private MutableLiveData<Option> selectedOption;

    private static final String PROJECT_COLLECTION = "projects";
    private static final String OPTION_COLLECTION = "options";
    private static final String REQUIREMENT_COLLECTION = "requirements";

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

        if (user == null) {
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
        if(user == null) {
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

        if(user == null) {
            super.deleteOption(option);
        } else {
            Timber.d("Removing option in firebase");
            if(selectedProject.getValue() != null) {

                List<Option> options = selectedProject.getValue().getOptionList();
                options.remove(position);

                new UpdateOptionAsyncTask(selectedProject.getValue().getProject().getFirebaseId())
                        .execute(options);
            }
        }
    }

    @Override
    public void updateOption(Option option, int position){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            super.updateOption(option, position);
        } else {
            Timber.d("Updating option in firebase");
            if(selectedProject.getValue() != null) {

                List<Option> options = selectedProject.getValue().getOptionList();
                options.set(position, option);

                new UpdateOptionAsyncTask(selectedProject.getValue().getProject().getFirebaseId())
                        .execute(options);
            }
        }
    }

    @Override
    public LiveData<Option> getSelectedOption(int id) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) {
            return super.getSelectedOption(id);
        } else {
            if (selectedProject.getValue() != null) {
                DocumentReference projectReference = firebaseDb.collection(PROJECT_COLLECTION)
                        .document(selectedProject.getValue().getProject().getFirebaseId());
                projectReference.addSnapshotListener((value, e) -> {
                    if (e != null) {
                        Timber.w("Listener of firebase projects failed");
                        selectedProject.setValue(null);
                    }

                    if (value != null) {
                        ProjectFirebase projectFirebase = value.toObject(ProjectFirebase.class);
                        ProjectWithDetails projectWithDetails = ProjectModelConverter.projectFirebaseToProjectWithDetails(projectFirebase);

                        selectedOption.setValue(Objects.requireNonNull(projectWithDetails).getOptionList().get(id));
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

        if (user == null) {
            return super.getProjects();
        } else {
            Timber.d("Getting projects from firebase");

            CollectionReference projectsReference = firebaseDb.collection(PROJECT_COLLECTION);


            Query query = projectsReference.whereEqualTo("userId", user.getUid());

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

        if(user == null){
            return super.getSelectedProject(id);
        } else {
            Timber.d("Getting project from Firebase with id %s", firebaseId);

            DocumentReference projectReference = firebaseDb.collection(PROJECT_COLLECTION).document(firebaseId);

            projectReference.addSnapshotListener((value, e) -> {
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

        if (user == null) {
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

        if (user == null) {
            super.deleteProjectWithDetails(project);
        } else {
            Timber.d("Deleting project from firebase");
            new DeleteProjectAsyncTask().execute(project.getProject().getFirebaseId());
            selectedProject.setValue(null);
        }
    }

    private static class UpdateOptionAsyncTask extends AsyncTask<List<Option>,Void,Void >{

        private String mProjectId;

        public UpdateOptionAsyncTask(String projectId){
            mProjectId = projectId;
        }

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Option>... lists) {

            DocumentReference projectDocumentRef;
            List<Option> options = lists[0];

            Timber.d("Option being updated");
            projectDocumentRef =
                    FirebaseFirestore.getInstance().collection(PROJECT_COLLECTION).document(mProjectId);
            projectDocumentRef.update("options", options)
                .addOnSuccessListener(d -> Timber.d("Successfully updated options"))
                .addOnFailureListener(e -> Timber.e("Failed to update options %s", e.getMessage()));

            return null;
        }
    }

    private static class DeleteProjectAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String projectId = strings[0];

            DocumentReference projectRef = FirebaseFirestore.getInstance().collection(PROJECT_COLLECTION).document(projectId);
            projectRef.delete()
                    .addOnFailureListener(
                            e -> Timber.d("Failed to delete %s from firebase error: %s",
                                    projectId, e.getMessage()))
                    .addOnSuccessListener(
                            documentReference -> Timber.d("Successfully deleted %s from firebase", projectId));
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
