package com.bowtye.decisive.ui.projectDetails;

import androidx.lifecycle.ViewModelProviders;

import timber.log.Timber;

public class ProjectDetailsActivity extends BaseProjectDetailsActivity {

    protected void prepareViewModel(){

        mViewModel = ViewModelProviders.of(this).get(ProjectDetailsViewModel.class);
        mViewModel.getProject(mProjectId).observe(this, projectWithDetails -> {
            Timber.d("Livedata Updated");
            mProject = projectWithDetails;
            mAdapter.setProject(mProject);
            if (mItemAdded) {
                mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
                mItemAdded = false;
            } else if (mItemDeleted) {
                mAdapter.notifyItemRemoved(mItemSelected);
                mItemDeleted = false;
            } else {
                mAdapter.notifyDataSetChanged();
            }

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
}