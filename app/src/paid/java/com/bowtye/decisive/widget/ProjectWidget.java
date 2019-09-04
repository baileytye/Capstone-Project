package com.bowtye.decisive.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.bowtye.decisive.R;
import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.ui.projectDetails.ProjectDetailsActivity;

import java.util.Locale;

import static com.bowtye.decisive.ui.main.home.HomeFragment.EXTRA_FIREBASE_ID;

public class ProjectWidget extends  BaseProjectWidget {

    public static void updateAppWidgetPaid(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, ProjectWithDetails project) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.project_widget);

        Intent intent = new Intent(context, ProjectDetailsActivity.class);
        intent.putExtra(EXTRA_FIREBASE_ID, project.getProject().getFirebaseId());

        int lowestIndex = getLowestRatedIndex(project);
        int highestIndex = getHighestRatedIndex(project);

        views.setTextViewText(R.id.appwidget_title, project.getProject().getName());
        views.setTextViewText(R.id.appwidget_tv_highest_name, project.getOptionList().get(highestIndex).getName());
        views.setTextViewText(R.id.appwidget_tv_lowest_name, project.getOptionList().get(lowestIndex).getName());
        views.setTextViewText(R.id.appwidget_tv_highest_rating,
                String.format(Locale.getDefault(), "%.2f", project.getOptionList().get(highestIndex).getRating()));
        views.setTextViewText(R.id.appwidget_tv_lowest_rating,
                String.format(Locale.getDefault(), "%.2f", project.getOptionList().get(lowestIndex).getRating()));

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_layout, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {

            String serializedProject = ProjectWidgetConfigureActivity.loadProjectFromPref(context, appWidgetId);
            if(serializedProject != null){
                ProjectWithDetails projectWithDetails = ProjectWithDetails.create(serializedProject);
                updateAppWidgetPaid(context, appWidgetManager, appWidgetId, projectWithDetails);
            }
        }
    }
}
