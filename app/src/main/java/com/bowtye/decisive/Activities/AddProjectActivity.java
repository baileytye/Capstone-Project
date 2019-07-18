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
import android.widget.TextView;

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

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
//    @BindView(R.id.toolbar_title)
//    TextView mToolbarTitle;
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
                Intent out = new Intent();
                out.putExtra(EXTRA_NEW_PROJECT, mProject);
                setResult(RESULT_OK, out);
                finishAfterTransition();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareViews() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //mToolbarTitle.setText("Add Project");

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AddProjectAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mFab.setOnClickListener(view ->{
            mAdapter.addRequirementCard();
            mAdapter.notifyItemInserted(mAdapter.getItemCount()-1);
        });
    }

    private boolean validateEntries(){
        return true;
    }
}
