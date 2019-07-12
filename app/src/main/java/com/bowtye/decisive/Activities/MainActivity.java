package com.bowtye.decisive.Activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import com.bowtye.decisive.Adapters.MainAdapter;
import com.bowtye.decisive.BuildConfig;
import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.Project;
import com.bowtye.decisive.Models.Requirement;
import com.bowtye.decisive.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

    public static final String EXTRA_PROJECT = "extra_project";

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.toolbar_title) TextView mToolbarTitle;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    @BindView(R.id.nav_view) NavigationView mNavigationView;
    @BindView(R.id.rv_main) RecyclerView mRecyclerView;

    Activity activity;
    private RecyclerView.LayoutManager mLayoutManager;
    private MainAdapter mAdapter;

    private List<Project> mProjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }

        activity = this;
        ButterKnife.bind(this);

        prepareViews();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddProjectActivity.class);

                Transition transition = new Slide(Gravity.TOP);

                getWindow().setExitTransition(transition);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
            }
        });
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
        if (id == R.id.action_settings) {

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
        intent.putExtra(EXTRA_PROJECT, mProjects.get(position));

        Transition transition = new Slide(Gravity.START);

        getWindow().setExitTransition(transition);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
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

        Requirement r1 = new Requirement("Requirement 1",
                Requirement.Type.number, Requirement.Importance.normal, 0,
                "", 0, false, 0);
        Requirement r2 = new Requirement("Requirement 2",
                Requirement.Type.number, Requirement.Importance.normal, 0,
                "", 0, false, 0);
        Requirement r3 = new Requirement("Requirement 3",
                Requirement.Type.number, Requirement.Importance.normal, 0,
                "", 0, false, 0);

        List<Requirement> requirements = new ArrayList<>(Arrays.asList(r1,r2,r3));

        Option option1 = new Option("Option 1", 100000,0,false,requirements,"",null);

        Project p = new Project(requirements, Collections.singletonList(option1), "Project Test", true);

        mProjects = Collections.singletonList(p);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MainAdapter(mProjects, this);
        mRecyclerView.setAdapter(mAdapter);

        Timber.d("Number of projects: " + mProjects.size());
    }


}
