package com.bowtye.decisive.ui.main.templates;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.R;
import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.ui.main.home.HomeAdapter;
import com.bowtye.decisive.ui.main.MainViewModel;
import com.bowtye.decisive.ui.projectDetails.ProjectDetailsActivity;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_FIREBASE_ID;
import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_IS_TEMPLATE;
import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_PROJECT_ID;

public abstract class BaseTemplatesFragment extends Fragment implements HomeAdapter.ProjectItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_main)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    private RecyclerView.LayoutManager mLayoutManager;
    private HomeAdapter mAdapter;
    private MainViewModel mViewModel;

    List<ProjectWithDetails> mTemplates;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_templates, container, false);
        setHasOptionsMenu(true);

        ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.templates, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prepareViewModel();
        prepareViews();
    }

    private void prepareViews() {

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        (Objects.requireNonNull(activity)).setSupportActionBar(mToolbar);
        Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(true);
        mToolbarLayout.setTitle(getString(R.string.title_templates));

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new HomeAdapter(mTemplates, this);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(activity.getDrawable(R.drawable.divider)));
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setIsLoading(boolean isLoading){
        if(isLoading){
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);

        } else {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    void prepareViewModel() {
        setIsLoading(true);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){

            return;
        }
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getTemplates().observe(this, templates -> {
            mTemplates = templates;
            Timber.d("Updating Livedata");
            mAdapter.setProjects(templates);
            mAdapter.notifyDataSetChanged();
            setIsLoading(false);
        });
    }

    @Override
    public void onProjectItemClicked(int position) {
        Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), ProjectDetailsActivity.class);
        intent.putExtra(EXTRA_PROJECT_ID, mTemplates.get(position).getProject().getId());
        intent.putExtra(EXTRA_FIREBASE_ID, mTemplates.get(position).getProject().getFirebaseId());
        intent.putExtra(EXTRA_IS_TEMPLATE, true);

        getActivity().getWindow().setExitTransition(new Slide(Gravity.START));
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }
    @Override
    public void onProjectDeleteMenuClicked(int position) {

    }

    @Override
    public void onProjectEditMenuCLicked(int position) {

    }
}