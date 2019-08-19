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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bowtye.decisive.Adapters.RequirementsAdapter;
import com.bowtye.decisive.Models.Option;
import com.bowtye.decisive.Models.ProjectWithDetails;
import com.bowtye.decisive.Models.Requirement;
import com.bowtye.decisive.R;
import com.bowtye.decisive.VerticalSpaceItemDecoration;
import com.bowtye.decisive.ViewModels.OptionDetailsViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_DELETE_OPTION;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_OPTION;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_OPTION_ID;
import static com.bowtye.decisive.Helpers.ExtraLabels.EXTRA_PROJECT;

public class OptionDetails extends AppCompatActivity {

    public static final int EDIT_OPTION_REQUEST_CODE = 234;

    public static final int RESULT_DELETED = 10;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.tv_rating)
    TextView mRatingTextView;
    @BindView(R.id.rb_option_rating)
    RatingBar mRatingBar;
    @BindView(R.id.text_input_et_notes)
    EditText mNotesTextInputEditText;
    @BindView(R.id.rv_requirements)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_price)
    TextView mPriceTextView;
    @BindView(R.id.iv_option_image)
    ImageView mOptionImageView;
    @BindView(R.id.layout_rating_with_number)
    View mRatingWithNumberView;

    private int mOptionId;
    Option mOption;
    ProjectWithDetails mProject;
    List<Requirement> mRequirements;
    RequirementsAdapter mAdapter;
    OptionDetailsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_details);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        if(intent != null){
            if(intent.hasExtra(EXTRA_OPTION_ID)){
                mOptionId = intent.getIntExtra(EXTRA_OPTION_ID, -1);
            }
            if(intent.hasExtra(EXTRA_PROJECT)){
                mProject = intent.getParcelableExtra(EXTRA_PROJECT);
                if(mProject != null) {
                    mRequirements = mProject.getRequirementList();
                }
            }
        }
        prepareViews();
        prepareViewModel();
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
                out.putExtra(EXTRA_DELETE_OPTION, mOption);
                setResult(RESULT_DELETED, out);
                finishAfterTransition();
                return true;
            case R.id.action_edit:
                Intent intent = new Intent(getApplicationContext(), AddOption.class);
                intent.putExtra(EXTRA_PROJECT, mProject);
                intent.putExtra(EXTRA_OPTION, mOption);

                Transition transition = new Slide(Gravity.TOP);

                getWindow().setExitTransition(transition);
                startActivityForResult(intent, EDIT_OPTION_REQUEST_CODE,
                        ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == EDIT_OPTION_REQUEST_CODE){
            if(data != null && data.hasExtra(EXTRA_OPTION)) {
                switch (resultCode) {
                    case RESULT_OK:
                        Timber.d("Received option from add ");
                        mOption = data.getParcelableExtra(EXTRA_OPTION);
                        mViewModel.updateOption(mOption, mOptionId);
                        //TODO: add calculate ratings
                        break;
                }
            }
        }
    }

    private void prepareViews() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setInitialPrefetchItemCount(mRequirements.size());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RequirementsAdapter(mRequirements, null, true);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration((int)
            getResources().getDimension(R.dimen.recycler_item_separation)));
        mRecyclerView.setAdapter(mAdapter);

        mRatingWithNumberView.setOnClickListener(view -> {
            Intent intent = new Intent(this.getApplicationContext(), RatingsActivity.class);
            intent.putExtra(EXTRA_OPTION, mOption);
            intent.putExtra(EXTRA_PROJECT, mProject);
            startActivity(intent);
        });
    }

    private void fillData(){
        mToolbarLayout.setTitle(mOption.getName());

        mPriceTextView.setText("$" + mOption.getPrice());
        mRatingTextView.setText(String.valueOf(mOption.getRating()));
        mRatingBar.setRating(mOption.getRating());
        mNotesTextInputEditText.setText(mOption.getNotes());

        if(mOption.getImagePath().equals("")){
            mOptionImageView.setVisibility(View.GONE);
        } else {
            mOptionImageView.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(mOption.getImagePath())
                    .fit()
                    .centerCrop()
                    .into(mOptionImageView);
        }
    }

    private void prepareViewModel(){
        mViewModel = ViewModelProviders.of(this).get(OptionDetailsViewModel.class);
        mViewModel.getOption(mOptionId).observe(this, option -> {
            Timber.d("Option livedata updated");
            if(option != null) {
                mOption = option;
                mAdapter.setRequirementValues(mOption.getRequirementValues());
                mAdapter.notifyDataSetChanged();
                fillData();
            }
        });
    }
}
