package com.bowtye.decisive.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bowtye.decisive.Adapters.DetailsAdapter;
import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.Project;
import com.bowtye.decisive.R;
import com.bowtye.decisive.ViewModels.DetailsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.bowtye.decisive.Activities.MainActivity.EXTRA_PROJECT_ID;

public class ProjectDetails extends AppCompatActivity implements DetailsAdapter.OptionItemClickListener {

    private static final int ADD_OPTION_REQUEST_CODE = 877;
    private static final int EDIT_OPTION_REQUEST_CODE = 92;

    public static final int RESULT_DELETED = 10;

    public static final String EXTRA_PROJECT = "extra_project";
    public static final String EXTRA_NEW_OPTION = "extra_new_option";
    public static final String EXTRA_OPTION = "extra_option";
    public static final String EXTRA_EDIT_OPTION = "extra_edit_option";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.rv_details)
    RecyclerView mRecyclerView;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.tv_empty_options)
    TextView mEmptyOptionsTextView;

    private RecyclerView.LayoutManager mLayoutManager;
    private DetailsAdapter mAdapter;
    private Project mProject;
    private int mProjectId;
    private DetailsViewModel mViewModel;

    private boolean mItemAdded = false;
    private int mItemDeleted = -1;
    private int mItemSelected = -1;
    //TODO: implement deleting, notify adapter only of deleted item


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_PROJECT_ID)) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == ADD_OPTION_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            if (data != null && data.hasExtra(EXTRA_NEW_OPTION)) {
                Option o = data.getParcelableExtra(EXTRA_NEW_OPTION);
                if (mProject.getOptions() == null) {
                    mProject.setOptions(new ArrayList<>());
                }
                mProject.getOptions().add(o);
                mItemAdded = true;
                mViewModel.insertOption(mProject);
                Timber.d("Project: %s inserted into the database", (o != null) ? o.getName() : "NULL");
            }
        } else if(requestCode == EDIT_OPTION_REQUEST_CODE){
            if(data != null && data.hasExtra(EXTRA_EDIT_OPTION)) {
                switch (resultCode) {
                    case RESULT_DELETED:
                        mViewModel.deleteOption(mProject, mItemSelected);
                }
            }
        }
    }

    private void prepareViews() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DetailsAdapter(mProject, this);
        mRecyclerView.setAdapter(mAdapter);

        setEmptyMessageVisability();

        mFab.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AddOption.class);
            intent.putExtra(EXTRA_PROJECT, mProject);

            Transition transition = new Slide(Gravity.TOP);

            getWindow().setExitTransition(transition);
            startActivityForResult(intent, ADD_OPTION_REQUEST_CODE,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
    }

    private void prepareViewModel() {
        mViewModel = ViewModelProviders.of(this).get(DetailsViewModel.class);
        mViewModel.getProject(mProjectId).observe(this, mProject -> {
            this.mProject = mProject;
            mAdapter.setProject(mProject);
            if(mItemAdded){
                mAdapter.notifyItemInserted(mProject.getOptions().size() - 1);
            } else {
                mAdapter.notifyDataSetChanged();
            }

            setEmptyMessageVisability();

            mToolbarTitle.setText(mProject.getName());
            if (mProject.getOptions() != null && mProject.getRequirements() != null) {
                Timber.d("Number of requirements loaded: %d",
                        ((this.mProject.getRequirements() != null) ? this.mProject.getRequirements().size() : 0));
                Timber.d("Number of options loaded: %d",
                        ((this.mProject.getOptions() != null) ? this.mProject.getOptions().size() : 0));
            }
        });
    }

    void setEmptyMessageVisability(){
        if(mAdapter.getItemCount() == 0) {
            mEmptyOptionsTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyOptionsTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onOptionItemClicked(int position) {
        Intent intent = new Intent(getApplicationContext(), OptionDetails.class);
        intent.putExtra(EXTRA_OPTION, mProject.getOptions().get(position));
        mItemSelected = position;

        Transition transition = new Slide(Gravity.START);

        getWindow().setExitTransition(transition);
        startActivityForResult(intent, EDIT_OPTION_REQUEST_CODE,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}
