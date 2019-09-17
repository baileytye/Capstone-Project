package com.bowtye.decisive.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.bowtye.decisive.R;
import com.bowtye.decisive.models.ProjectWithDetails;

public class ProjectWidgetConfigureActivity extends BaseProjectWidgetConfigureActivity{

    @Override
    public void onProjectClicked(ProjectWithDetails project) {

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int appWidgetId;

        if(extras != null){
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

            RemoteViews views = new RemoteViews(getPackageName(), R.layout.project_widget);

            appWidgetManager.updateAppWidget(appWidgetId, views);
            ProjectWidget.updateAppWidgetPaid(this, appWidgetManager, appWidgetId, project);

            saveProjectToPref(this, appWidgetId, project);

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);

            finish();
        }
    }
}
