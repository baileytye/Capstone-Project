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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bowtye.decisive.Adapters.DetailsAdapter;
import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.ProjectWithDetails;
import com.bowtye.decisive.R;
import com.bowtye.decisive.ViewModels.DetailsViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_DELETE_OPTION;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_EDIT_PROJECT;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_NEW_PROJECT;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_OPTION;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_OPTION_ID;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_PROJECT;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_PROJECT_ID;

public class ProjectDetails extends AppCompatActivity implements DetailsAdapter.OptionItemClickListener {

    private static final int ADD_OPTION_REQUEST_CODE = 877;
    private static final int EDIT_OPTION_REQUEST_CODE = 92;

    public static final int RESULT_DELETED = 10;
    private static final int EDIT_PROJECT_REQUEST_CODE = 12;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.rv_details)
    RecyclerView mRecyclerView;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.tv_empty_options)
    TextView mEmptyOptionsTextView;

    private RecyclerView.LayoutManager mLayoutManager;
    private DetailsAdapter mAdapter;
    private ProjectWithDetails mProject;
    private int mProjectId;
    private DetailsViewModel mViewModel;

    private boolean mItemAdded = false;
    private boolean mItemDeleted = false;
    private int mItemSelected = -1;

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

        prepareViews();
        prepareViewModel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.project_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                return true;
            case R.id.action_edit_project:
                startAddProjectActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == ADD_OPTION_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            if (data != null && data.hasExtra(EXTRA_OPTION)) {
                Option o = data.getParcelableExtra(EXTRA_OPTION);
                if (mProject.getOptionList() == null) {
                    mProject.setOptionList(new ArrayList<>());
                }
                mItemAdded = true;
                mViewModel.insertOption(o, mProjectId);

                //TODO: Add calculate ratings
                Timber.d("Project: %s inserted into the database", (o != null) ? o.getName() : "NULL");
            }
        } else if (requestCode == EDIT_OPTION_REQUEST_CODE) {
            if (data != null && data.hasExtra(EXTRA_DELETE_OPTION)) {
                switch (resultCode) {
                    case RESULT_DELETED:
                        mViewModel.deleteOption(mProject.getOptionList().get(mItemSelected));
                        mItemDeleted = true;
                }
            }
        } else if (requestCode == EDIT_PROJECT_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra(EXTRA_NEW_PROJECT)) {
                ProjectWithDetails p = data.getParcelableExtra(EXTRA_NEW_PROJECT);
                mViewModel.insertProjectWithDetails(p);
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
        mViewModel.getProject(mProjectId).observe(this, projectWithDetails -> {
            Timber.d("Livedata Updated");
            mProject = projectWithDetails;
            mAdapter.setProject(mProject);
            if (mItemAdded) {
                mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
                mItemAdded = false;
            } else if (mItemDeleted) {
                mAdapter.notifyItemRemoved(mItemSelected);
                mItemDeleted = false;
            } else {
                mAdapter.notifyDataSetChanged();
            }

            setEmptyMessageVisibility();

            mToolbarLayout.setTitle(mProject.getProject().getName());

            if (mProject.getOptionList() != null && mProject.getRequirementList() != null) {
                Timber.d("Number of requirements loaded: %d",
                        ((this.mProject.getRequirementList() != null) ? this.mProject.getRequirementList().size() : 0));
                Timber.d("Number of options loaded: %d",
                        ((this.mProject.getOptionList() != null) ? this.mProject.getOptionList().size() : 0));
            }
        });
    }

    void setEmptyMessageVisibility() {
        if ((mProject == null) || (mProject.getOptionList().size() == 0)) {
            mEmptyOptionsTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyOptionsTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onOptionItemClicked(int position) {
        Intent intent = new Intent(getApplicationContext(), OptionDetails.class);
        intent.putExtra(EXTRA_OPTION_ID, mProject.getOptionList().get(position).getOptionId());
        intent.putExtra(EXTRA_PROJECT, mProject);

        mItemSelected = position;

        Transition transition = new Slide(Gravity.START);

        getWindow().setExitTransition(transition);
        startActivityForResult(intent, EDIT_OPTION_REQUEST_CODE,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    void startAddProjectActivity() {
        Intent intent = new Intent(getApplicationContext(), AddProjectActivity.class);
        intent.putExtra(EXTRA_EDIT_PROJECT, mProject);
        Transition transition = new Slide(Gravity.TOP);

        getWindow().setExitTransition(transition);
        startActivityForResult(intent, EDIT_PROJECT_REQUEST_CODE,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}
