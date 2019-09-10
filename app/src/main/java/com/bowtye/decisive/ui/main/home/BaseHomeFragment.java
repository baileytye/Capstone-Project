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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.R;
import com.bowtye.decisive.ui.main.MainAdapter;
import com.bowtye.decisive.ui.main.MainViewModel;
import com.bowtye.decisive.ui.settings.SettingsActivity;
import com.bowtye.decisive.utils.ExtraLabels;
import com.bowtye.decisive.utils.RatingUtils;
import com.bowtye.decisive.ui.addProject.AddProjectActivity;
import com.bowtye.decisive.ui.projectDetails.ProjectDetailsActivity;
import com.bowtye.decisive.utils.RequestCode;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static android.app.Activity.RESULT_OK;
import static com.bowtye.decisive.ui.addProject.AddProjectActivity.RESULT_TEMPLATE;

public class BaseHomeFragment extends Fragment implements MainAdapter.ProjectItemClickCallback,
        HomeAdapter.ContextMenuClickCallback,
        RatingUtils.CalculateRatingsOfProjectAsyncTask.ProjectResultAsyncCallback {

    public static final String HOME_ID = "home";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.fab_from_template)
    FloatingActionButton mFabFromTemplate;
    @BindView(R.id.fab_new_project)
    FloatingActionButton mFabNewProject;
    @BindView(R.id.rv_main)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.label_from_template)
    TextView mFromTemplateLabel;
    @BindView(R.id.label_new_project)
    TextView mNewProjectLabel;
    @BindView(R.id.outside_layout)
    View mOutsideLayout;

    private RecyclerView.LayoutManager mLayoutManager;
    private HomeAdapter mAdapter;
    private MainViewModel mViewModel;

    private Animation fab_open, fab_close, fab_rotate, fab_anti_rotate, fade_in, fade_out;
    //TODO:REMOVE INSERTINGTEMPLATE WHEN DONE TEMPLATES
    private Boolean isOpen = false, insertingTemplate = false;

    List<ProjectWithDetails> mProjects;

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

        switch (id) {
            case R.id.action_insert_dummy:
                mViewModel.insertDummyProject();
                return true;
            case R.id.action_settings:
                Objects.requireNonNull(getActivity()).startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            case R.id.action_reset_tutorial:
                MaterialShowcaseView.resetAll(Objects.requireNonNull(getActivity()));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == RequestCode.ADD_PROJECT_REQUEST_CODE)) {
            if(resultCode == RESULT_OK) {
                if (data != null && data.hasExtra(ExtraLabels.EXTRA_NEW_PROJECT)) {
                    ProjectWithDetails projectWithDetails = data.getParcelableExtra(ExtraLabels.EXTRA_NEW_PROJECT);
                    mViewModel.resizeOptionValuesList(projectWithDetails);
                    new RatingUtils.CalculateRatingsOfProjectAsyncTask(this).execute(projectWithDetails);
                }
                //TODO: FIX WHEN DONE TEMPLATES
            } else if(resultCode == RESULT_TEMPLATE){
                insertingTemplate = true;
                ProjectWithDetails projectWithDetails = Objects.requireNonNull(data).getParcelableExtra(ExtraLabels.EXTRA_NEW_PROJECT);
                mViewModel.resizeOptionValuesList(projectWithDetails);
                new RatingUtils.CalculateRatingsOfProjectAsyncTask(this).execute(projectWithDetails);
            }
            closeFabMenu();
        }
    }

    private void setIsLoading(boolean isLoading) {
        if (isLoading) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);

        } else {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void prepareViews() {
        setIsLoading(true);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        (Objects.requireNonNull(activity)).setSupportActionBar(mToolbar);
        Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(true);
        mToolbarLayout.setTitle(getString(R.string.title_projects));

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new HomeAdapter(null, this, this);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(activity.getDrawable(R.drawable.divider)));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        fab_close = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fab_open);
        fab_rotate = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fab_rotate);
        fab_anti_rotate = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fab_anti_rotate);
        fade_in = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(activity.getApplicationContext(), R.anim.fade_out);

        isOpen = false;

        mOutsideLayout.setOnClickListener(view -> closeFabMenu());

        mFab.setOnClickListener(view -> {
            if (isOpen) {
                closeFabMenu();
            } else {
                openFabMenu();
            }
        });

        mFabNewProject.setOnClickListener(view -> startAddProjectActivity(-1));

        mFabFromTemplate.setOnClickListener(view -> {
                NavHostFragment.findNavController(this).navigate(R.id.action_navigation_home_to_navigation_templates);
                closeFabMenu();
        });

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), HOME_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(   new MaterialShowcaseView.Builder(this.getActivity())
                .setDismissOnTouch(true)
                .setMaskColour(getResources().getColor(R.color.colorPrimaryDark))
                .setShapePadding(128)
                .setTitleText("New project")
                .setTarget(mFab)
                .setDismissText("Got it")
                .setContentText("Add some projects using this button!")
                .build());

        sequence.addSequenceItem( new MaterialShowcaseView.Builder(this.getActivity())
                .setDismissOnTouch(true)
                .setMaskColour(getResources().getColor(R.color.colorPrimaryDark))
                .setTarget(getActivity().findViewById(R.id.navigation_templates))
                .setTitleText("Templates")
                .setDismissText("Got it")
                .setContentText("Check out some templates here!")
                .setShapePadding(0)
                .build());

        sequence.addSequenceItem( new MaterialShowcaseView.Builder(this.getActivity())
                .setDismissOnTouch(true)
                .setMaskColour(getResources().getColor(R.color.colorPrimaryDark))
                .setTarget(getActivity().findViewById(R.id.navigation_home))
                .setTitleText("Home")
                .setDismissText("Got it")
                .setContentText("Projects you create will show up here!")
                .build());

        sequence.start();

        Timber.d("Number of projects: %d", ((mProjects != null) ? mProjects.size() : 0));
    }

    private void closeFabMenu(){
        mOutsideLayout.startAnimation(fade_out);
        mOutsideLayout.setClickable(false);
        mFromTemplateLabel.startAnimation(fade_out);
        mNewProjectLabel.startAnimation(fade_out);
        mFabNewProject.startAnimation(fab_close);
        mFabFromTemplate.startAnimation(fab_close);
        mFab.startAnimation(fab_anti_rotate);
        mFabFromTemplate.setClickable(false);
        mFabNewProject.setClickable(false);
        mFab.setColorFilter(getResources().getColor(R.color.white));
        isOpen = false;
    }

    private void openFabMenu(){
        mOutsideLayout.startAnimation(fade_in);
        mOutsideLayout.setClickable(true);
        mFromTemplateLabel.startAnimation(fade_in);
        mNewProjectLabel.startAnimation(fade_in);
        mFabNewProject.startAnimation(fab_open);
        mFabFromTemplate.startAnimation(fab_open);
        mFab.startAnimation(fab_rotate);
        mFabNewProject.setClickable(true);
        mFabFromTemplate.setClickable(true);
        mFab.setColorFilter(getResources().getColor(R.color.grey100));
        isOpen = true;
    }

    void prepareViewModel() {
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getProjects().observe(this, projects -> {
            mProjects = projects;
            Timber.d("Updating Livedata");
            mAdapter.setProjects(projects);
            mAdapter.notifyDataSetChanged();
            setIsLoading(false);
        });
    }

    @Override
    public void onProjectItemClicked(int position) {
        Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), ProjectDetailsActivity.class);
        intent.putExtra(ExtraLabels.EXTRA_PROJECT_ID, mProjects.get(position).getProject().getId());

        getActivity().getWindow().setExitTransition(new Slide(Gravity.START));
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }

    @Override
    public void onProjectDeleteMenuClicked(int position) {
        mViewModel.deleteProject(mProjects.get(position));
    }

    @Override
    public void onProjectEditMenuClicked(int position) {
        startAddProjectActivity(position);
    }


    void startAddProjectActivity(int position) {
        Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), AddProjectActivity.class);
        if (position >= 0) {
            intent.putExtra(ExtraLabels.EXTRA_EDIT_PROJECT, mProjects.get(position));
        }
        Transition transition = new Slide(Gravity.TOP);

        getActivity().getWindow().setExitTransition(transition);
        startActivityForResult(intent, RequestCode.ADD_PROJECT_REQUEST_CODE,
                ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }


    @Override
    public void updateProjectAfterCalculatingRatings(ProjectWithDetails projectWithDetails) {

        //TODO: FIX WHEN DONE TEMPLATES
        if(insertingTemplate){
            mViewModel.insertTemplate(projectWithDetails);
            insertingTemplate = false;
        } else {
            mViewModel.insertProjectWithDetails(projectWithDetails);
        }
        Timber.d("Project: %s inserted into the database", (projectWithDetails != null)
                ? projectWithDetails.getProject().getName() : "NULL");
    }
}