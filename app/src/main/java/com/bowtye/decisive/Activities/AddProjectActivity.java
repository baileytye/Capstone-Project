package com.bowtye.decisive.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bowtye.decisive.Adapters.AddProjectAdapter;
import com.bowtye.decisive.Helpers.ViewUtils;
import com.bowtye.decisive.Models.Project;
import com.bowtye.decisive.Models.ProjectWithDetails;
import com.bowtye.decisive.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_EDIT_PROJECT;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_NEW_PROJECT;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_PROJECT;

public class AddProjectActivity extends AppCompatActivity {

    public static final int VALIDATION_OK = 55;
    public static final int VALIDATION_SAVE_REQ_ERROR = -1;
    public static final int VALIDATION_NAME_ERROR = -2;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.et_project_name)
    EditText mProjectNameEditText;
    @BindView(R.id.rv_add_requirements)
    RecyclerView mRecyclerView;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.label_empty_requirements)
    TextView mEmptyRequirementsLabel;

    private RecyclerView.LayoutManager mLayoutManager;
    private AddProjectAdapter mAdapter;
    private ProjectWithDetails mProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        ButterKnife.bind(this);

        mProject = new ProjectWithDetails(new Project("", true), new ArrayList<>(), new ArrayList<>());
        Intent intent = getIntent();

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(EXTRA_PROJECT)){
                mProject = savedInstanceState.getParcelable(EXTRA_PROJECT);
            }
        }

        if(intent != null){
            if(intent.hasExtra(EXTRA_EDIT_PROJECT)){
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
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
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

        mProjectNameEditText.setText(mProject.getProject().getName());

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AddProjectAdapter(this, (mProject == null) ? null : mProject.getRequirementList());
        mRecyclerView.setAdapter(mAdapter);

        checkIfEmpty();

        mFab.setOnClickListener(view ->{
            mAdapter.addRequirementCard();
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyRequirementsLabel.setVisibility(View.INVISIBLE);
        });
    }

    public void checkIfEmpty(){
        if(mAdapter.getItemCount() == 0){
            mRecyclerView.setVisibility(View.INVISIBLE);
            mEmptyRequirementsLabel.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyRequirementsLabel.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Validates project fields
     * @return error code based on missing, or invalid fields
     */
    private int validateEntries(){
        String name = mProjectNameEditText.getText().toString();

        if(name.equals("")){
            return VALIDATION_NAME_ERROR;
        }

        mProject.getProject().setName(name);

        for(int i = 0 ; i < mAdapter.getItemCount(); i++){
            AddProjectAdapter.AddRequirementViewHolder holder = (AddProjectAdapter.AddRequirementViewHolder)
                    mRecyclerView.findViewHolderForAdapterPosition(i);

            if(holder!= null && !holder.getIsSaved()){
                return VALIDATION_SAVE_REQ_ERROR;
            }
        }
        mProject.setRequirementList(mAdapter.getRequirements());

        return VALIDATION_OK;
    }
}
