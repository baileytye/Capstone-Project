package com.bowtye.decisive.ui.main.home;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.R;
import com.bowtye.decisive.utils.RatingUtils;
import com.bowtye.decisive.ui.addProject.AddProjectActivity;
import com.bowtye.decisive.ui.projectDetails.ProjectDetailsActivity;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_EDIT_PROJECT;
import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_NEW_PROJECT;
import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_PROJECT_ID;

public class BaseHomeFragment extends Fragment implements HomeAdapter.ProjectItemClickListener,
        RatingUtils.CalculateRatingsOfProjectAsyncTask.ProjectResultAsyncCallback {

    public static final int ADD_PROJECT_REQUEST_CODE = 3423;


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.rv_main)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;

    private RecyclerView.LayoutManager mLayoutManager;
    private HomeAdapter mAdapter;
    private HomeViewModel mViewModel;

    protected List<ProjectWithDetails> mProjects;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        setHasOptionsMenu(true);

        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prepareViewModel();
        prepareViews();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_insert_dummy){
            mViewModel.insertDummyProject();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == ADD_PROJECT_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            if (data != null && data.hasExtra(EXTRA_NEW_PROJECT)) {
                ProjectWithDetails projectWithDetails = data.getParcelableExtra(EXTRA_NEW_PROJECT);
                mViewModel.resizeOptionValuesList(projectWithDetails);
                new RatingUtils.CalculateRatingsOfProjectAsyncTask(this).execute(projectWithDetails);
            }
        }
    }

    private void prepareViews() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        (Objects.requireNonNull(activity)).setSupportActionBar(mToolbar);
        Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(true);
        mToolbarLayout.setTitle("Projects");

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new HomeAdapter(null, this);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(activity.getDrawable(R.drawable.divider)));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mFab.setOnClickListener(view -> {
            startAddProjectActivity(-1);

        });

        Timber.d("Number of projects: %d", ((mProjects != null) ? mProjects.size() : 0));
    }



    void prepareViewModel() {
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mViewModel.getProjects().observe(this, projectWithDetails -> {
            mProjects = projectWithDetails;
            Timber.d("Updating Livedata");
            mAdapter.setProjects(projectWithDetails);
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onProjectItemClicked(int position) {
        Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), ProjectDetailsActivity.class);
        intent.putExtra(EXTRA_PROJECT_ID, mProjects.get(position).getProject().getId());

        getActivity().getWindow().setExitTransition(new Slide(Gravity.START));
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }

    @Override
    public void onProjectDeleteMenuClicked(int position) {
        mViewModel.deleteProject(mProjects.get(position));
    }

    @Override
    public void onProjectEditMenuCLicked(int position) {
        startAddProjectActivity(position);
    }


    void startAddProjectActivity(int position) {
        Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), AddProjectActivity.class);
        if (position >= 0) {
            intent.putExtra(EXTRA_EDIT_PROJECT, mProjects.get(position));
        }
        Transition transition = new Slide(Gravity.TOP);

        getActivity().getWindow().setExitTransition(transition);
        startActivityForResult(intent, ADD_PROJECT_REQUEST_CODE,
                ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }


    @Override
    public void updateProjectAfterCalculatingRatings(ProjectWithDetails projectWithDetails) {
        mViewModel.insertProjectWithDetails(projectWithDetails);

        Timber.d("Project: %s inserted into the database", (projectWithDetails != null)
                ? projectWithDetails.getProject().getName() : "NULL");
    }
}