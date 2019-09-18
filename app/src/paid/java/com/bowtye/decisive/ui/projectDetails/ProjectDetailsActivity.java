package com.bowtye.decisive.ui.projectDetails;

import android.app.ActivityOptions;
import android.content.Intent;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.bowtye.decisive.ui.optionDetails.OptionDetails;
import com.bowtye.decisive.utils.ExtraLabels;
import com.bowtye.decisive.utils.RequestCode;
import com.google.firebase.auth.FirebaseAuth;

import timber.log.Timber;

public class ProjectDetailsActivity extends BaseProjectDetailsActivity {

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == RequestCode.EDIT_OPTION_REQUEST_CODE) {
            if (data != null && data.hasExtra(ExtraLabels.EXTRA_DELETE_OPTION)) {
                if (resultCode == RESULT_DELETED) {
                    mViewModel.deleteOptionFirebase(mProject.getOptionList().get(mItemSelected), mItemSelected, this);
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
        if (FirebaseAuth.getInstance().getCurrentUser() != null && !mIsTemplate && !FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
            mViewModel.uploadImagesToFirebase(mProject, this);
        }
    }

    @Override
    void prepareViewModel() {
        Timber.d("Preparing view model project details");
        mViewModel = ViewModelProviders.of(this).get(ProjectDetailsViewModel.class);
        mViewModel.getProjectFirebase(mProjectId, mFirebaseId, mIsTemplate).observe(this, projectWithDetails -> {
            Timber.d("Livedata Updated");

            if (mProject != null && mProject.getRequirementList().size() != projectWithDetails.getRequirementList().size()) {
                mRecyclerView.setAdapter(null);
                mAdapter = new ProjectDetailsAdapter(projectWithDetails, this);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.clearRecyclerPool();
                mRecyclerView.getRecycledViewPool().clear();
                mAdapter.setProject(projectWithDetails);
            }

            mProject = projectWithDetails;

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

            if (mProject != null) {
                setIsLoading(false);
                mToolbarLayout.setTitle((mIsTemplate)
                        ? "Template: " + mProject.getProject().getName()
                        : mProject.getProject().getName());

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
        if (FirebaseAuth.getInstance().getCurrentUser() == null || mIsTemplate || FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
            super.onOptionItemClicked(position);
            return;
        }
        Intent intent = new Intent(getApplicationContext(), OptionDetails.class);

        intent.putExtra(ExtraLabels.EXTRA_OPTION_ID, position);
        intent.putExtra(ExtraLabels.EXTRA_PROJECT, mProject);

        mItemSelected = position;

        Transition transition = new Slide(Gravity.START);

        getWindow().setExitTransition(transition);
        startActivityForResult(intent, RequestCode.EDIT_OPTION_REQUEST_CODE,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}