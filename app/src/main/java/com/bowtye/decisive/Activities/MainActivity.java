package com.bowtye.decisive.Activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import com.bowtye.decisive.Adapters.MainAdapter;
import com.bowtye.decisive.BuildConfig;
import com.bowtye.decisive.Models.ProjectWithDetails;
import com.bowtye.decisive.R;
import com.bowtye.decisive.ViewModels.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MainAdapter.ProjectItemClickListener {

    public static final String EXTRA_PROJECT_ID = "extra_project_id";
    public static final String EXTRA_NEW_PROJECT = "extra_new_project";
    public static final String EXTRA_EDIT_PROJECT = "extra_edit_project";

    public static final int ADD_PROJECT_REQUEST_CODE = 3423;


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.rv_main)
    RecyclerView mRecyclerView;

    private RecyclerView.LayoutManager mLayoutManager;
    private MainAdapter mAdapter;
    private MainViewModel mViewModel;

    private List<ProjectWithDetails> mProjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        ButterKnife.bind(this);

        prepareViewModel();
        prepareViews();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_clear) {
            mViewModel.clearProjects();
            return true;
        } else if(id == R.id.action_delete_option){
            mViewModel.clearOptions();
            return true;
        } else if (id == R.id.action_delete_requirement){
            mViewModel.clearRequirements();
            return true;
        } else if(id == R.id.action_insert_dummy){
            mViewModel.insertDummyProject();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onProjectItemClicked(int position) {
        Intent intent = new Intent(getApplicationContext(), ProjectDetails.class);
        intent.putExtra(EXTRA_PROJECT_ID, mProjects.get(position).getProject().getId());

        Transition transition = new Slide(Gravity.START);

        getWindow().setExitTransition(transition);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void onProjectDeleteMenuClicked(int position) {
        mViewModel.deleteProject(mProjects.get(position));
    }

    @Override
    public void onProjectEditMenuCLicked(int position) {
        startAddProjectActivity(position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == ADD_PROJECT_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            if (data != null && data.hasExtra(EXTRA_NEW_PROJECT)) {
                ProjectWithDetails p = data.getParcelableExtra(EXTRA_NEW_PROJECT);
                if((p != null) && (p.getOptionList().size() > 0) &&
                        (p.getOptionList().get(0).getRequirementValues().size() < p.getRequirementList().size())){
                    for(int i = 0; i < p.getOptionList().size(); i++){
                        p.getOptionList().get(i).setRequirementValues(new ArrayList<>());
                        ArrayList<Double> values = new ArrayList<>(Arrays.asList(new Double[p.getRequirementList().size()]));
                        Collections.fill(values, 0.0);
                        p.getOptionList().get(i).setRequirementValues(values);
                    }
                }
                mViewModel.insertProjectWithDetails(p);
                Timber.d("Project: %s inserted into the database", (p != null) ? p.getProject().getName() : "NULL");
            }
        }
    }

    private void prepareViews() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        mToolbarTitle.setText(R.string.app_name);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MainAdapter(null, this);
        mRecyclerView.setAdapter(mAdapter);

        mFab.setOnClickListener(view -> {
            startAddProjectActivity(-1);

        });

        Timber.d("Number of projects: %d", ((mProjects != null) ? mProjects.size() : 0));
    }

    void prepareViewModel() {
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getProjects().observe(this, projectWithDetails -> {
            mProjects = projectWithDetails;
            Timber.d("Updating Livedata");
            mAdapter.setProjects(projectWithDetails);
            mAdapter.notifyDataSetChanged();
        });
    }

    void startAddProjectActivity(int position) {
        Intent intent = new Intent(getApplicationContext(), AddProjectActivity.class);
        if (position >= 0) {
            intent.putExtra(EXTRA_EDIT_PROJECT, mProjects.get(position));
        }
        Transition transition = new Slide(Gravity.TOP);

        getWindow().setExitTransition(transition);
        startActivityForResult(intent, ADD_PROJECT_REQUEST_CODE,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }


}
