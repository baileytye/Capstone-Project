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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bowtye.decisive.ui.addProject.AddProjectActivity;
import com.bowtye.decisive.ui.common.VerticalSpaceItemDecoration;
import com.bowtye.decisive.ui.optionDetails.OptionDetails;
import com.bowtye.decisive.utils.ExtraLabels;
import com.bowtye.decisive.utils.RatingUtils;
import com.bowtye.decisive.models.Option;
import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.R;
import com.bowtye.decisive.ui.addOption.AddOption;
import com.bowtye.decisive.utils.RequestCode;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;
import timber.log.Timber;

import static com.bowtye.decisive.ui.addProject.AddProjectActivity.RESULT_EDITED;

public abstract class BaseProjectDetailsActivity extends AppCompatActivity implements
        ProjectDetailsAdapter.OptionItemClickListener,
        RatingUtils.CalculateRatingsOfProjectAsyncTask.ProjectResultAsyncCallback,
        RatingUtils.CalculateRatingOfOptionAsyncTask.OptionResultAsyncCallback {

    public static final int RESULT_DELETED = 10;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.rv_details)
    RecyclerView mRecyclerView;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.floating_button)
    FloatingTextButton mFloatingButton;
    @BindView(R.id.tv_empty_options)
    TextView mEmptyOptionsTextView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.iv_empty_options)
    ImageView mEmptyOptionsImageView;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected ProjectDetailsAdapter mAdapter;
    protected ProjectWithDetails mProject;
    protected int mProjectId;
    protected ProjectDetailsViewModel mViewModel;
    protected String mFirebaseId;

    protected boolean mItemAdded = false;
    protected boolean mItemDeleted = false;
    protected int mItemSelected = -1;
    protected boolean mIsTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(ExtraLabels.EXTRA_PROJECT_ID)) {
                mProjectId = intent.getIntExtra(ExtraLabels.EXTRA_PROJECT_ID, -1);
            }
            if(intent.hasExtra(ExtraLabels.EXTRA_IS_TEMPLATE)){
                mIsTemplate = intent.getBooleanExtra(ExtraLabels.EXTRA_IS_TEMPLATE, false);
            }
            if(intent.hasExtra(ExtraLabels.EXTRA_FIREBASE_ID)){
                mFirebaseId = intent.getStringExtra(ExtraLabels.EXTRA_FIREBASE_ID);
            }
        }

        prepareViews();
        prepareViewModel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.project_details, menu);

        if(mIsTemplate){
            menu.findItem(R.id.action_edit_project).setVisible(false);
            menu.findItem(R.id.action_delete_project).setVisible(false);
        }

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
                mViewModel.getProject(mProjectId, null, false).removeObservers(this);
                mViewModel.deleteProject(mProject);
                finishAfterTransition();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == RequestCode.ADD_OPTION_REQUEST_CODE) && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra(ExtraLabels.EXTRA_OPTION)) {
                Option o = data.getParcelableExtra(ExtraLabels.EXTRA_OPTION);

                new RatingUtils.CalculateRatingOfOptionAsyncTask(this, mProject.getRequirementList()).execute(o);
            }
        } else if (requestCode == RequestCode.EDIT_OPTION_REQUEST_CODE) {
            if (data != null && data.hasExtra(ExtraLabels.EXTRA_DELETE_OPTION)) {
                if (resultCode == RESULT_DELETED) {
                    mViewModel.deleteOption(mProject.getOptionList().get(mItemSelected));
                    mItemDeleted = true;
                }
            }
        } else if (requestCode == RequestCode.EDIT_PROJECT_REQUEST_CODE) {
            if(resultCode == RESULT_EDITED) {
                if (data != null && data.hasExtra(ExtraLabels.EXTRA_NEW_PROJECT)) {
                    ProjectWithDetails projectWithDetails = data.getParcelableExtra(ExtraLabels.EXTRA_NEW_PROJECT);
                    mViewModel.resizeOptionValuesList(projectWithDetails);
                    new RatingUtils.CalculateRatingsOfProjectAsyncTask(this).execute(projectWithDetails);
                }
            }
        }
    }

    private void addTemplate(){
        Intent out = new Intent();
        out.putExtra(ExtraLabels.EXTRA_NEW_PROJECT, mProject);
        for(Option option : mProject.getOptionList()){
            option.setDateCreated(new Date());
        }
        setResult(RESULT_OK, out);
        finish();
    }

    protected void setIsLoading(boolean isLoading){
        if(isLoading){
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);

        } else {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    protected void prepareViews() {
        setIsLoading(true);

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
                (int) getResources().getDimension(R.dimen.recycler_item_separation));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        if(mIsTemplate) {
            mFloatingButton.setOnClickListener(view -> addTemplate());
            mFab.hide();
        } else {
            mFloatingButton.setVisibility(View.INVISIBLE);
            mFab.setOnClickListener(view -> {
                Intent intent = new Intent(getApplicationContext(), AddOption.class);
                intent.putExtra(ExtraLabels.EXTRA_PROJECT, mProject);

                Transition transition = new Slide(Gravity.TOP);

                getWindow().setExitTransition(transition);
                startActivityForResult(intent, RequestCode.ADD_OPTION_REQUEST_CODE,
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            });
        }
    }

    protected void setEmptyMessageVisibility() {
        if ((mProject == null) || (mProject.getOptionList().size() == 0)) {
            mEmptyOptionsTextView.setVisibility(View.VISIBLE);
            mEmptyOptionsImageView.setVisibility(View.VISIBLE);
            setIsLoading(false);
        } else {
            mEmptyOptionsImageView.setVisibility(View.INVISIBLE);
            mEmptyOptionsTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onOptionItemClicked(int position) {
        Intent intent = new Intent(getApplicationContext(), OptionDetails.class);
        intent.putExtra(ExtraLabels.EXTRA_OPTION_ID, (mIsTemplate)
                ? position
                : mProject.getOptionList().get(position).getOptionId());
        intent.putExtra(ExtraLabels.EXTRA_PROJECT, mProject);
        intent.putExtra(ExtraLabels.EXTRA_IS_TEMPLATE, mIsTemplate);

        mItemSelected = position;

        Transition transition = new Slide(Gravity.START);

        getWindow().setExitTransition(transition);
        startActivityForResult(intent, RequestCode.EDIT_OPTION_REQUEST_CODE,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    protected void startAddProjectActivity() {
        Intent intent = new Intent(getApplicationContext(), AddProjectActivity.class);
        intent.putExtra(ExtraLabels.EXTRA_EDIT_PROJECT, mProject);
        Transition transition = new Slide(Gravity.TOP);

        getWindow().setExitTransition(transition);
        startActivityForResult(intent, RequestCode.EDIT_PROJECT_REQUEST_CODE,
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
