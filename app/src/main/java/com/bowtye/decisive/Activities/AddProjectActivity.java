package com.bowtye.decisive.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bowtye.decisive.Adapters.AddProjectAdapter;
import com.bowtye.decisive.Adapters.DetailsAdapter;
import com.bowtye.decisive.Models.Project;
import com.bowtye.decisive.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bowtye.decisive.Activities.MainActivity.EXTRA_NEW_PROJECT;

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

    private RecyclerView.LayoutManager mLayoutManager;
    private AddProjectAdapter mAdapter;
    private Project mProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        ButterKnife.bind(this);

        prepareViews();
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
                        showErrorDialog("Save Project", "Please give this project a name");
                        break;
                    case VALIDATION_SAVE_REQ_ERROR:
                        showErrorDialog("Save Project", "Please save all requirements");
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

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AddProjectAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        mFab.setOnClickListener(view ->{
            mAdapter.addRequirementCard();
        });
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

        mProject = new Project(null, null, name, true);

        for(int i = 0 ; i < mAdapter.getItemCount(); i++){
            AddProjectAdapter.AddRequirementViewHolder holder = (AddProjectAdapter.AddRequirementViewHolder)
                    mRecyclerView.findViewHolderForAdapterPosition(i);

            if(holder!= null && !holder.getIsSaved()){
                return VALIDATION_SAVE_REQ_ERROR;
            }
        }
        mProject.setRequirements(mAdapter.getRequirements());

        return VALIDATION_OK;
    }

    /**
     * Displays error dialog
     * @param title title of dialog
     * @param message message of dialog
     */
    private void showErrorDialog(String title, String message){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
}
