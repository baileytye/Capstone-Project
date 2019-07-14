package com.bowtye.decisive.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.bowtye.decisive.Adapters.DetailsAdapter;
import com.bowtye.decisive.Models.Project;
import com.bowtye.decisive.R;
import com.bowtye.decisive.ViewModels.DetailsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.bowtye.decisive.Activities.MainActivity.EXTRA_PROJECT_ID;

public class ProjectDetails extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.toolbar_title) TextView mToolbarTitle;
    @BindView(R.id.rv_details) RecyclerView mRecyclerView;

    private RecyclerView.LayoutManager mLayoutManager;
    private DetailsAdapter mAdapter;
    private Project mProject;
    private int mProjectId;
    private DetailsViewModel mViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if(intent != null){
            if(intent.hasExtra(EXTRA_PROJECT_ID)){
                mProjectId = intent.getIntExtra(EXTRA_PROJECT_ID, -1);
            }
        }

        prepareViewModel();
        prepareViews();
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finishAfterTransition();
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareViews() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DetailsAdapter(mProject);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void prepareViewModel(){
        mViewModel = ViewModelProviders.of(this).get(DetailsViewModel.class);
        mViewModel.getProject(mProjectId).observe(this, mProject -> {
            this.mProject = mProject;
            mAdapter.setProject(mProject);
            mAdapter.notifyDataSetChanged();
            mToolbarTitle.setText(mProject.getName());
            if(mProject.getOptions() != null && mProject.getRequirements() != null) {
                Timber.d("Number of requirements loaded: %d", ((this.mProject.getRequirements() != null) ? this.mProject.getRequirements().size() : 0));
                Timber.d("Number of options loaded: %d", ((this.mProject.getOptions() != null) ? this.mProject.getOptions().size() : 0));
            }
        });
    }
}
