package com.bowtye.decisive.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.bowtye.decisive.Adapters.AddOptionAdapter;
import com.bowtye.decisive.Helpers.DialogHelper;
import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.Project;
import com.bowtye.decisive.Models.Requirement;
import com.bowtye.decisive.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bowtye.decisive.Activities.AddProjectActivity.EXTRA_PROJECT;
import static com.bowtye.decisive.Activities.ProjectDetails.EXTRA_NEW_OPTION;

public class AddOption extends AppCompatActivity {

    public static final int VALIDATION_OK = 55;
    public static final int VALIDATION_NAME_ERROR = -2;
    public static final int VALIDATION_HOLDER_ERROR = -1;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitleTextView;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.rv_add_option)
    RecyclerView mRecyclerView;
    @BindView(R.id.ti_option_name)
    TextInputEditText mOptionNameEditText;
    @BindView(R.id.ti_price)
    TextInputEditText mPriceEditText;

    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    List<Requirement> mRequirements;
    Option mOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_option);

        ButterKnife.bind(this);
        mOption = new Option("", 0, 0, false, new ArrayList<>(), "", new ArrayList<>());

        Intent intent = getIntent();
        if(intent != null){
            if(intent.hasExtra(EXTRA_PROJECT)){
                Project p = intent.getParcelableExtra(EXTRA_PROJECT);
                if(p != null) {
                    mRequirements = p.getRequirements();
                }
            }
        }

        prepareViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finishAfterTransition();
                return true;
            case R.id.action_save:
                switch(validateAndSave()){
                    case VALIDATION_NAME_ERROR:
                        DialogHelper.showErrorDialog("Save Option",
                                "Please give this option a name", this);
                        break;
                    case VALIDATION_HOLDER_ERROR:
                        DialogHelper.showErrorDialog("Save Option",
                                "Please fill the requirement values", this);
                        break;
                    case VALIDATION_OK:
                        Intent out = new Intent();
                        out.putExtra(EXTRA_NEW_OPTION, mOption);
                        setResult(RESULT_OK, out);
                        finishAfterTransition();
                        return true;
                }

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

        mToolbarTitleTextView.setText("Add Option");

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new AddOptionAdapter(mRequirements);
        mRecyclerView.setAdapter(mAdapter);

        mFab.setOnClickListener(view -> Snackbar.make(view,
                "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    private int validateAndSave(){
        String name = Objects.requireNonNull(mOptionNameEditText.getText()).toString();
        if(name.equals("")){
            return VALIDATION_NAME_ERROR;
        } else {
            mOption.setName(name);
        }

        double price;
        String priceString = Objects.requireNonNull(mPriceEditText.getText()).toString();
        if(priceString.equals("")){
            price = 0;
        } else {
            price = Double.parseDouble(priceString);
        }
        mOption.setPrice(price);

        for(int i = 0; i < mRequirements.size(); i ++){
            AddOptionAdapter.AddOptionRequirementViewHolder holder =
                    (AddOptionAdapter.AddOptionRequirementViewHolder)
                            mRecyclerView.findViewHolderForAdapterPosition(i);
            if(holder != null) {
                mOption.getRequirementValues().add(holder.getRequirementValue());
            } else {
                return VALIDATION_HOLDER_ERROR;
            }
        }

        return VALIDATION_OK;
    }
}
