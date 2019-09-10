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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.R;
import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.ui.main.MainAdapter;
import com.bowtye.decisive.ui.main.MainViewModel;
import com.bowtye.decisive.ui.projectDetails.ProjectDetailsActivity;
import com.bowtye.decisive.utils.ExtraLabels;
import com.bowtye.decisive.utils.RequestCode;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static android.app.Activity.RESULT_OK;

public abstract class BaseTemplatesFragment extends Fragment implements MainAdapter.ProjectItemClickCallback {

    private static final String TEMPLATES_ID = "templates";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_main)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    private RecyclerView.LayoutManager mLayoutManager;
    private MainAdapter mAdapter;
    private MainViewModel mViewModel;

    private List<ProjectWithDetails> mTemplates;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_templates, container, false);
        setHasOptionsMenu(true);

        ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.templates, menu);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prepareViewModel();
        prepareViews();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == RequestCode.ADD_TEMPLATE_REQUEST_CODE) && (resultCode == RESULT_OK)) {
            if (data != null && data.hasExtra(ExtraLabels.EXTRA_NEW_PROJECT)) {
                ProjectWithDetails projectWithDetails = data.getParcelableExtra(ExtraLabels.EXTRA_NEW_PROJECT);
                if (projectWithDetails != null) {
                    projectWithDetails.getProject().setDateCreated(new Date());
                    Timber.d("Setting date before inserting template");
                }
                mViewModel.insertProjectWithDetails(projectWithDetails);

                NavHostFragment.findNavController(this).navigate(R.id.action_navigation_templates_to_navigation_home);
            }
        }
    }

    private void prepareViews() {

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        (Objects.requireNonNull(activity)).setSupportActionBar(mToolbar);
        Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(true);
        mToolbarLayout.setTitle(getString(R.string.title_templates));

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MainAdapter(mTemplates, this);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(activity.getDrawable(R.drawable.divider)));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), TEMPLATES_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(new MaterialShowcaseView.Builder(getActivity())
                .setDismissOnTouch(true)
                .setMaskColour(getResources().getColor(R.color.colorPrimaryDark))
                .setShapePadding(128)
                .setTarget(this.getView())
                .setTitleText("Templates")
                .withoutShape()
                .setDismissText("Got it")
                .setContentText("Templates are pre-made projects to give you some ideas on how to structure your own. " +
                        "You can also add these to your projects and customize them to your liking!")
                .build());

        sequence.start();
    }

    private void setIsLoading(boolean isLoading) {
        if (isLoading) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);

        } else {
            mProgressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void prepareViewModel() {
        setIsLoading(true);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

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
        intent.putExtra(ExtraLabels.EXTRA_PROJECT_ID, mTemplates.get(position).getProject().getId());
        intent.putExtra(ExtraLabels.EXTRA_FIREBASE_ID, mTemplates.get(position).getProject().getFirebaseId());
        intent.putExtra(ExtraLabels.EXTRA_IS_TEMPLATE, true);

        getActivity().getWindow().setExitTransition(new Slide(Gravity.START));
        startActivityForResult(intent, RequestCode.ADD_TEMPLATE_REQUEST_CODE,
                ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }
}