package com.bowtye.decisive.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.Adapters.RatingsAdapter;
import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.ProjectWithDetails;
import com.bowtye.decisive.Models.Requirement;
import com.bowtye.decisive.R;
import com.bowtye.decisive.VerticalSpaceItemDecoration;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_OPTION;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_PROJECT;

public class RatingsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.rv_ratings)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_option_title)
    TextView mOptionTitleTextView;
    @BindView(R.id.rb_option_rating)
    RatingBar mOptionRatingBar;
    @BindView(R.id.tv_rating)
    TextView mRatingTextView;

    private Option mOption;
    private List<Requirement> mRequirements;
    private RatingsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratings);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_OPTION)) {
                mOption = intent.getParcelableExtra(EXTRA_OPTION);
            }
            if (intent.hasExtra(EXTRA_PROJECT)) {
                ProjectWithDetails project = intent.getParcelableExtra(EXTRA_PROJECT);
                if (project != null) {
                    mRequirements = project.getRequirementList();
                }
            }
        }

        prepareViews();
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {


        if (item.getItemId() == android.R.id.home) {
            finishAfterTransition();
        }

        return super.onOptionsItemSelected(item);
    }

    private void prepareViews() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Timber.d("Rating: %f", mOption.getRating());

        mOptionTitleTextView.setText(mOption.getName());
        mRatingTextView.setText(String.valueOf(mOption.getRating()));
        mOptionRatingBar.setRating(mOption.getRating());

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(24));
        mAdapter = new RatingsAdapter(mRequirements, mOption);
        mRecyclerView.setAdapter(mAdapter);
    }

}
