package com.bowtye.decisive.Database;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bowtye.decisive.Helpers.ProjectModelConverter;
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

import timber.log.Timber;

public class ProjectRepository extends BaseRepository {

    private static ProjectRepository instance;
    private FirebaseFirestore firebaseDb;
    private MutableLiveData<List<ProjectWithDetails>> projects;

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
            new DeleteProjectAsyncTask().execute(project.getProject().getFirebaseId());
        }
    }

    private static class DeleteProjectAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String projectId = strings[0];

            DocumentReference projectRef = FirebaseFirestore.getInstance().collection(PROJECT_COLLECTION).document(projectId);
            projectRef.delete()
                    .addOnFailureListener(
                            e -> Timber.d("Failed to delete %s to firebase error: %s",
                                    projectId, e.getMessage()))
                    .addOnSuccessListener(
                            documentReference -> {
                                Timber.d("Successfully deleted %s to firebase", projectId);
                            });
            return null;
        }
    }

    private static class InsertProjectAsyncTask extends AsyncTask<ProjectFirebase, Void, Void> {

        @Override
        protected Void doInBackground(ProjectFirebase... projectFirebases) {
            ProjectFirebase projectFirebase = projectFirebases[0];

            DocumentReference projectDocumentRef;

            if (projectFirebase.getProjectId() == null) {
                Timber.d("New project being inserted");
                projectDocumentRef =
                        FirebaseFirestore.getInstance().collection(PROJECT_COLLECTION).document();

                projectFirebase.setProjectId(projectDocumentRef.getId());
            } else {
                Timber.d("Project being updated");
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
