package com.bowtye.decisive.Activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import com.bowtye.decisive.Adapters.MainAdapter;
import com.bowtye.decisive.BuildConfig;
import com.bowtye.decisive.Models.Project;
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

    private Activity activity;
    private RecyclerView.LayoutManager mLayoutManager;
    private MainAdapter mAdapter;
    private MainViewModel mViewModel;

    private List<Project> mProjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        activity = this;
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {
            mViewModel.clearProjects();
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
        intent.putExtra(EXTRA_PROJECT_ID, mProjects.get(position).getId());

        Transition transition = new Slide(Gravity.START);

        getWindow().setExitTransition(transition);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == ADD_PROJECT_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            mViewModel.insertDummyProject();
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
            Intent intent = new Intent(getApplicationContext(), AddProjectActivity.class);

            Transition transition = new Slide(Gravity.TOP);

            getWindow().setExitTransition(transition);
            startActivityForResult(intent, ADD_PROJECT_REQUEST_CODE, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
        });

        Timber.d("Number of projects: %d", ((mProjects != null) ? mProjects.size() : 0));
    }

    void prepareViewModel() {
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getProjects().observe(this, mProjects -> {
            Timber.d("Livedata updated");
            this.mProjects = mViewModel.updateProjects();
            mAdapter.setProjects(this.mProjects);
            mAdapter.notifyDataSetChanged();
            if(mProjects.size() > 0) {
                Timber.d("Number of requirements loaded: %d", ((this.mProjects.get(0).getRequirements() != null) ? this.mProjects.get(0).getRequirements().size() : 0));
                Timber.d("Number of options loaded: %d", ((this.mProjects.get(0).getOptions() != null) ? this.mProjects.get(0).getOptions().size() : 0));
            }
        });
    }


}
