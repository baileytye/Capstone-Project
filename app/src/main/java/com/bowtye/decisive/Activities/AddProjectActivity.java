package com.bowtye.decisive.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bowtye.decisive.Adapters.AddProjectAdapter;
import com.bowtye.decisive.Helpers.ViewUtils;
import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.Project;
import com.bowtye.decisive.Models.ProjectWithDetails;
import com.bowtye.decisive.Models.Requirement;
import com.bowtye.decisive.R;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_EDIT_PROJECT;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_EDIT_REQUIREMENT;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_NEW_PROJECT;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_PROJECT;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_REQUIREMENT;

public class AddProjectActivity extends AppCompatActivity implements AddProjectAdapter.OnRequirementClickedCallback, ViewUtils.warningCallback {

    public static final int VALIDATION_OK = 1;
    public static final int VALIDATION_SAVE_REQ_ERROR = -1;
    public static final int VALIDATION_NAME_ERROR = -2;
    private static final int ADD_REQUIREMENT_REQUEST_CODE = 17;
    private static final int EDIT_REQUIREMENT_REQUEST_CODE = 18;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.label_empty_requirements)
    TextView mEmptyRequirementsLabel;
    @BindView(R.id.rv_requirements)
    RecyclerView mRecyclerView;
    @BindView(R.id.et_project_name)
    EditText mProjectNameEditText;

    private RecyclerView.LayoutManager mLayoutManager;
    private AddProjectAdapter mAdapter;
    private ProjectWithDetails mProject;
    private int mPositionClicked;
    private Boolean itemChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        ButterKnife.bind(this);

        mProject = new ProjectWithDetails(new Project("", true), new ArrayList<>(), new ArrayList<>());
        Intent intent = getIntent();

        itemChanged = false;

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(EXTRA_PROJECT)) {
                mProject = savedInstanceState.getParcelable(EXTRA_PROJECT);
            }
        }

        if (intent != null) {
            if (intent.hasExtra(EXTRA_EDIT_PROJECT)) {
                mProject = intent.getParcelableExtra(EXTRA_EDIT_PROJECT);
            }
        }
        prepareViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfEmpty();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.d("Saved project with %d requirements", (mAdapter.getRequirements() == null) ? 0 : mAdapter.getRequirements().size());
        mProject.setRequirementList(mAdapter.getRequirements());
        outState.putParcelable(EXTRA_PROJECT, mProject);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_project, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ADD_REQUIREMENT_REQUEST_CODE && data != null) {
            if (data.hasExtra(EXTRA_REQUIREMENT) && resultCode == RESULT_OK) {
                Requirement requirement = data.getParcelableExtra(EXTRA_REQUIREMENT);
                mAdapter.addRequirement(requirement);
                Timber.d("Added %s", Objects.requireNonNull(requirement).getName());
                checkIfEmpty();
                itemChanged = true;
            }
        } else if (requestCode == EDIT_REQUIREMENT_REQUEST_CODE && data != null) {
            if (data.hasExtra(EXTRA_REQUIREMENT) && resultCode == RESULT_OK) {
                Requirement requirement = data.getParcelableExtra(EXTRA_REQUIREMENT);
                for (Option option : mProject.getOptionList()) {
                    option.getRequirementValues().set(mPositionClicked, (double) 0);
                }
                mAdapter.overideRequirement(requirement, mPositionClicked);
                itemChanged = true;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (itemChanged) {
                    ViewUtils.showWarningDialog(
                            getResources().getString(R.string.leave_without_saving_dialog_message),
                            this, this);
                } else {
                    finishAfterTransition();
                }
                return true;
            case R.id.action_save:
                switch (validateEntries()) {
                    case VALIDATION_OK:
                        Intent out = new Intent();
                        out.putExtra(EXTRA_NEW_PROJECT, mProject);
                        setResult(RESULT_OK, out);
                        finishAfterTransition();
                        break;
                    case VALIDATION_NAME_ERROR:
                        ViewUtils.showErrorDialog("Save Project",
                                "Please give this project a name", this);
                        break;
                    case VALIDATION_SAVE_REQ_ERROR:
                        ViewUtils.showErrorDialog("Save Project",
                                "Please save all requirements", this);
                        break;
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Prepares views
     */
    private void prepareViews() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (!mProject.getProject().getName().equals("")) {
            mProjectNameEditText.setText(mProject.getProject().getName());
            mToolbarLayout.setTitle("Edit project");
        } else {
            mToolbarLayout.setTitle("Add project");
        }

        mProjectNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                itemChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AddProjectAdapter(this, (mProject == null) ? null : mProject.getRequirementList());
        mRecyclerView.setAdapter(mAdapter);

        checkIfEmpty();

        mFab.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddRequirement.class);
            startActivityForResult(intent, ADD_REQUIREMENT_REQUEST_CODE,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
    }

    public void checkIfEmpty() {
        if (mAdapter.getItemCount() == 0) {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mEmptyRequirementsLabel.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyRequirementsLabel.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Validates project fields
     *
     * @return error code based on missing, or invalid fields
     */
    private int validateEntries() {
        String name = mProjectNameEditText.getText().toString();

        if (name.equals("")) {
            return VALIDATION_NAME_ERROR;
        }

        mProject.getProject().setName(name);
        mProject.setRequirementList(mAdapter.getRequirements());

        return VALIDATION_OK;
    }

    @Override
    public void onRequirementClicked(Requirement requirement, int position) {
        mPositionClicked = position;
        Intent intent = new Intent(this, AddRequirement.class);
        intent.putExtra(EXTRA_EDIT_REQUIREMENT, requirement);
        startActivityForResult(intent, EDIT_REQUIREMENT_REQUEST_CODE,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void warningClicked(int result) {
        if (result == ViewUtils.DIALOG_OK) {
            finishAfterTransition();
        }
    }
}
