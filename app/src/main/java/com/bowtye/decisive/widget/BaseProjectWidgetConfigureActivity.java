package com.bowtye.decisive.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bowtye.decisive.R;
import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.ui.main.home.HomeViewModel;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * The configuration screen for the {@link BaseProjectWidget BaseProjectWidget} AppWidget.
 */
public abstract class BaseProjectWidgetConfigureActivity extends AppCompatActivity implements WidgetConfigurationAdapter.ProjectClickedInterfaceCallback {

    private static final String PREFS_NAME = "com.bowtye.decisive.widget.ProjectWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @BindView(R.id.rv_select_project)
    RecyclerView mRecyclerView;

    List<ProjectWithDetails> mProjects;
    WidgetConfigurationAdapter mAdapter;

    public BaseProjectWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    protected static void saveProjectToPref(Context context, int appWidgetId, ProjectWithDetails project) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        String serializedProject = project.serialize();

        prefs.putString(PREF_PREFIX_KEY + appWidgetId + "project", serializedProject);
        prefs.apply();
    }

    protected static String loadProjectFromPref(Context context, int appWidgetId){
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
        return preferences.getString(PREF_PREFIX_KEY + appWidgetId + "project", null);
    }


    static void deleteProjectPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.project_widget_configure);

        ButterKnife.bind(this);

        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new WidgetConfigurationAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(getDrawable(R.drawable.divider)));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        prepareViewModel();

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    void prepareViewModel() {
        HomeViewModel mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mViewModel.getProjects().observe(this, projectsWithDetails -> {
            mProjects = projectsWithDetails;
            Timber.d("Updating Livedata For Widget");
            mAdapter.setProjects(projectsWithDetails);
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onProjectClicked(int position) {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int appWidgetId;

        if(extras != null){
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

            RemoteViews views = new RemoteViews(getPackageName(), R.layout.project_widget);

            appWidgetManager.updateAppWidget(appWidgetId, views);
            ProjectWidget.updateAppWidget(this, appWidgetManager, appWidgetId, mProjects.get(position));

            saveProjectToPref(this, appWidgetId, mProjects.get(position));

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);

            finish();
        }
    }
}

