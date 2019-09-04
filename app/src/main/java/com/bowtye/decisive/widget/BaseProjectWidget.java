package com.bowtye.decisive.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.bowtye.decisive.R;
import com.bowtye.decisive.models.Option;
import com.bowtye.decisive.models.ProjectWithDetails;
import com.bowtye.decisive.ui.projectDetails.ProjectDetailsActivity;

import java.util.Locale;

import static com.bowtye.decisive.utils.ExtraLabels.EXTRA_PROJECT_ID;


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link BaseProjectWidgetConfigureActivity BaseProjectWidgetConfigureActivity}
 */
public abstract class BaseProjectWidget extends AppWidgetProvider {

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, ProjectWithDetails project) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.project_widget);

        Intent intent = new Intent(context, ProjectDetailsActivity.class);
        intent.putExtra(EXTRA_PROJECT_ID, project.getProject().getId());

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

    protected static int getHighestRatedIndex(ProjectWithDetails project){
        int index = 0;
        Float highest = (float) 0;
        for(int i = 0; i < project.getOptionList().size() ; i++){
            Option option = project.getOptionList().get(i);
            if(option.getRating() > highest){
                index = i;
            }
        }
        return index;
    }

    protected static int getLowestRatedIndex(ProjectWithDetails project){
        int index = 0;
        Float lowest = (float) 5;
        for(int i = 0; i < project.getOptionList().size() ; i++){
            Option option = project.getOptionList().get(i);
            if(option.getRating() < lowest){
                index = i;
            }
        }
        return index;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {

            String serializedProject = ProjectWidgetConfigureActivity.loadProjectFromPref(context, appWidgetId);
            if(serializedProject != null){
                ProjectWithDetails projectWithDetails = ProjectWithDetails.create(serializedProject);
                updateAppWidget(context, appWidgetManager, appWidgetId, projectWithDetails);
            }
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            ProjectWidgetConfigureActivity.deleteProjectPref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

