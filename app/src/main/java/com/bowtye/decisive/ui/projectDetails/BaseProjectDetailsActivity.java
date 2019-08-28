package com.bowtye.decisive.ui.projectDetails;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import com.bowtye.decisive.ui.addProject.AddProjectActivity;
import com.bowtye.decisive.ui.common.VerticalSpaceItemDecoration;
import com.bowtye.decisive.ui.optionDetails.OptionDetails;
import com.bowtye.decisive.utils.RatingUtils;
import com.bowtye.decisive.models.Option;
import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.R;
import com.bowtye.decisive.ui.addOption.AddOption;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_DELETE_OPTION;
import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_EDIT_PROJECT;
import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_NEW_PROJECT;
import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_OPTION;
import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_OPTION_ID;
import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_PROJECT;
import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_PROJECT_ID;

public abstract class BaseProjectDetailsActivity extends AppCompatActivity implements
        ProjectDetailsAdapter.OptionItemClickListener,
        RatingUtils.CalculateRatingsOfProjectAsyncTask.ProjectResultAsyncCallback,
        RatingUtils.CalculateRatingOfOptionAsyncTask.OptionResultAsyncCallback {

    protected static final int ADD_OPTION_REQUEST_CODE = 877;
    protected static final int EDIT_OPTION_REQUEST_CODE = 92;

    public static final int RESULT_DELETED = 10;
    protected static final int EDIT_PROJECT_REQUEST_CODE = 12;

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

    protected RecyclerView.LayoutManager mLayoutManager;
    protected ProjectDetailsAdapter mAdapter;
    protected ProjectWithDetails mProject;
    protected int mProjectId;
    protected ProjectDetailsViewModel mViewModel;

    protected boolean mItemAdded = false;
    protected boolean mItemDeleted = false;
    protected int mItemSelected = -1;

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
            case R.id.action_delete_project:
                mViewModel.getProject(mProjectId).removeObservers(this);
                mViewModel.deleteProject(mProject);
                finishAfterTransition();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if ((requestCode == ADD_OPTION_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            if (data != null && data.hasExtra(EXTRA_OPTION)) {
                Option o = data.getParcelableExtra(EXTRA_OPTION);

                //Don't think this is needed but if a bug appears this could be why
//                if (mProject.getOptionList() == null) {
//                    mProject.setOptionList(new ArrayList<>());
//                }
                new RatingUtils.CalculateRatingOfOptionAsyncTask(this, mProject.getRequirementList()).execute(o);
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
                ProjectWithDetails projectWithDetails = data.getParcelableExtra(EXTRA_NEW_PROJECT);
                mViewModel.resizeOptionValuesList(projectWithDetails);
                new RatingUtils.CalculateRatingsOfProjectAsyncTask(this).execute(projectWithDetails);
            }
        }
    }

    protected void prepareViews() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ProjectDetailsAdapter(mProject, this);
        mRecyclerView.setAdapter(mAdapter);
        VerticalSpaceItemDecoration dividerItemDecoration = new VerticalSpaceItemDecoration(
                (int) getResources().getDimension(R.dimen.requirement_item_separation));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mFab.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AddOption.class);
            intent.putExtra(EXTRA_PROJECT, mProject);

            Transition transition = new Slide(Gravity.TOP);

            getWindow().setExitTransition(transition);
            startActivityForResult(intent, ADD_OPTION_REQUEST_CODE,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
    }

    protected void setEmptyMessageVisibility() {
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

    protected void startAddProjectActivity() {
        Intent intent = new Intent(getApplicationContext(), AddProjectActivity.class);
        intent.putExtra(EXTRA_EDIT_PROJECT, mProject);
        Transition transition = new Slide(Gravity.TOP);

        getWindow().setExitTransition(transition);
        startActivityForResult(intent, EDIT_PROJECT_REQUEST_CODE,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    abstract void prepareViewModel();

    @Override
    public void updateProjectAfterCalculatingRatings(ProjectWithDetails projectWithDetails) {
        mViewModel.insertProjectWithDetails(projectWithDetails);
        Timber.d("Project: %s updated in database", (projectWithDetails != null)
                ? projectWithDetails.getProject().getName() : "NULL");
    }


    @Override
    public void updateOptionAfterCalculatingRatings(Option option) {
        mItemAdded = true;
        mViewModel.insertOption(option, mProjectId);
    }
}
