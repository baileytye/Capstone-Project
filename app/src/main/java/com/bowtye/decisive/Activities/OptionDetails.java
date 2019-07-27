package com.bowtye.decisive.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bowtye.decisive.Activities.ProjectDetails.EXTRA_EDIT_OPTION;
import static com.bowtye.decisive.Activities.ProjectDetails.EXTRA_OPTION;
import static com.bowtye.decisive.Activities.ProjectDetails.RESULT_DELETED;

public class OptionDetails extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitleTextView;

    Option mOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_details);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if(intent != null){
            if(intent.hasExtra(EXTRA_OPTION)){
                mOption = intent.getParcelableExtra(EXTRA_OPTION);
            }
        }

        prepareViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.option_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                return true;
            case R.id.action_delete:
                Intent out = new Intent();
                out.putExtra(EXTRA_EDIT_OPTION, mOption);
                setResult(RESULT_DELETED, out);
                finishAfterTransition();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareViews() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbarTitleTextView.setText(mOption.getName());
    }
}
