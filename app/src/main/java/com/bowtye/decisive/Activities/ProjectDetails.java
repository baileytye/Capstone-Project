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
import com.bowtye.decisive.Models.ProjectWithDetails;
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
                if (mProject.getOptionList() == null) {
                    mProject.setOptionList(new ArrayList<>());
                }
                mItemAdded = true;
                mViewModel.insertOption(o, mProjectId);
                Timber.d("Project: %s inserted into the database", (o != null) ? o.getName() : "NULL");
            }
        } else if(requestCode == EDIT_OPTION_REQUEST_CODE){
            if(data != null && data.hasExtra(EXTRA_EDIT_OPTION)) {
                switch (resultCode) {
                    case RESULT_DELETED:
                        mViewModel.deleteOption(mProject.getOptionList().get(mItemSelected));
                        mItemDeleted = true;
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
            if(mItemAdded) {
                mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
                mItemAdded = false;
            } else if(mItemDeleted){
                mAdapter.notifyItemRemoved(mItemSelected);
                mItemDeleted = false;
            } else {
                mAdapter.notifyDataSetChanged();
            }

            setEmptyMessageVisibility();

            mToolbarTitle.setText(mProject.getProject().getName());
            if (mProject.getOptionList() != null && mProject.getRequirementList() != null) {
                Timber.d("Number of requirements loaded: %d",
                        ((this.mProject.getRequirementList() != null) ? this.mProject.getRequirementList().size() : 0));
                Timber.d("Number of options loaded: %d",
                        ((this.mProject.getOptionList() != null) ? this.mProject.getOptionList().size() : 0));
            }
        });
    }

    void setEmptyMessageVisibility(){
        if((mProject == null) || (mProject.getOptionList().size() == 0)) {
            mEmptyOptionsTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyOptionsTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onOptionItemClicked(int position) {
        Intent intent = new Intent(getApplicationContext(), OptionDetails.class);
        intent.putExtra(EXTRA_OPTION, mProject.getOptionList().get(position));
        mItemSelected = position;

        Transition transition = new Slide(Gravity.START);

        getWindow().setExitTransition(transition);
        startActivityForResult(intent, EDIT_OPTION_REQUEST_CODE,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}
