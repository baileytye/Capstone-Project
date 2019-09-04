package com.bowtye.decisive.ui.projectDetails;

import androidx.lifecycle.ViewModelProviders;

import timber.log.Timber;

public class ProjectDetailsActivity extends BaseProjectDetailsActivity {

    protected void prepareViewModel(){

        mViewModel = ViewModelProviders.of(this).get(ProjectDetailsViewModel.class);
        mViewModel.getProject(mProjectId).observe(this, projectWithDetails -> {
            Timber.d("Livedata Updated");
            //TODO: add load screens here

            if(mProject != null && mProject.getRequirementList().size() != projectWithDetails.getRequirementList().size()){
                mRecyclerView.setAdapter(null);
                mAdapter = new ProjectDetailsAdapter(projectWithDetails, this);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            } else {
                mAdapter.clearRecyclerPool();
                mRecyclerView.getRecycledViewPool().clear();
                mAdapter.setProject(projectWithDetails);

                if (mItemAdded) {
                    mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
                    mItemAdded = false;
                } else if (mItemDeleted) {
                    mAdapter.notifyItemRemoved(mItemSelected);
                    mItemDeleted = false;
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }

            mProject = projectWithDetails;


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