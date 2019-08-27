package com.bowtye.decisive.ui.projectDetails;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.bowtye.decisive.ui.optionDetails.OptionDetails;
import com.google.firebase.auth.FirebaseAuth;

import timber.log.Timber;

import static com.bowtye.decisive.ui.main.home.HomeFragment.EXTRA_FIREBASE_ID;
import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_DELETE_OPTION;
import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_OPTION_ID;
import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_PROJECT;

public class ProjectDetailsActivity extends BaseProjectDetailsActivity{

    private String mFirebaseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if(intent != null){
            if(intent.hasExtra(EXTRA_FIREBASE_ID)){
                mFirebaseId = intent.getStringExtra(EXTRA_FIREBASE_ID);
            }
        }

        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == EDIT_OPTION_REQUEST_CODE) {
            if (data != null && data.hasExtra(EXTRA_DELETE_OPTION)) {
                switch (resultCode) {
                    case RESULT_DELETED:
                        mViewModel.deleteOptionFirebase(mProject.getOptionList().get(mItemSelected), mItemSelected);
                        mItemDeleted = true;
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            mViewModel.uploadImagesToFirebase(mProject);
        }
    }

    @Override
    void prepareViewModel() {
        Timber.d("Preparing view model project details");
        mViewModel = ViewModelProviders.of(this).get(ProjectDetailsViewModel.class);
        mViewModel.getProjectFirebase(mProjectId, mFirebaseId).observe(this, projectWithDetails -> {
            Timber.d("Livedata Updated");
            mProject = projectWithDetails;
            mAdapter.clearRecyclerPool();
            mRecyclerView.getRecycledViewPool().clear();
            mAdapter.setProject(mProject);
//            if (mItemAdded) {
//                mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
//                mItemAdded = false;
//            } else if (mItemDeleted) {
//                mAdapter.notifyItemRemoved(mItemSelected);
//                mItemDeleted = false;
//            } else {
                mAdapter.notifyDataSetChanged();
//            }

            setEmptyMessageVisibility();

            if(mProject != null) {
                mToolbarLayout.setTitle(mProject.getProject().getName());

                if (mProject.getOptionList() != null && mProject.getRequirementList() != null) {
                    Timber.d("Number of requirements loaded: %d",
                            ((this.mProject.getRequirementList() != null) ? this.mProject.getRequirementList().size() : 0));
                    Timber.d("Number of options loaded: %d",
                            ((this.mProject.getOptionList() != null) ? this.mProject.getOptionList().size() : 0));
                }
            }
        });
    }


    @Override
    public void onOptionItemClicked(int position) {
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            super.onOptionItemClicked(position);
            return;
        }
        Intent intent = new Intent(getApplicationContext(), OptionDetails.class);

        intent.putExtra(EXTRA_OPTION_ID, position);
        intent.putExtra(EXTRA_PROJECT, mProject);

        mItemSelected = position;

        Transition transition = new Slide(Gravity.START);

        getWindow().setExitTransition(transition);
        startActivityForResult(intent, EDIT_OPTION_REQUEST_CODE,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}