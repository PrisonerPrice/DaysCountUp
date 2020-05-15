package com.prisonerprice.dayscountup.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.prisonerprice.dayscountup.R;
import com.prisonerprice.dayscountup.database.Task;
import com.prisonerprice.dayscountup.view.MainActivity;
import com.prisonerprice.dayscountup.view.TaskAdapter;

public class EventWidget extends AppWidgetProvider {

    private static final String GOING_TO_MAIN_SCREEN = "going_to_main_screen";

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        String text = context.getResources().getString(R.string.widget_text_default);

        if (TaskAdapter.getTasks() != null && TaskAdapter.getTasks().size() != 0) {
            text = TaskAdapter.getTask(0).getDescription();
            text = context.getResources().getString(R.string.widget_text_prefix) + "\n" + text;
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.event_widget);
        views.setTextViewText(R.id.widget_text, text);

        Intent homeIntent = new Intent(context, MainActivity.class);
        homeIntent.setData(Uri.parse(homeIntent.toUri(Intent.URI_INTENT_SCHEME)));
        homeIntent.setAction(GOING_TO_MAIN_SCREEN);
        PendingIntent homePendingIntent = PendingIntent.getActivity(context, 0, homeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.widget_select_btn, homePendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), EventWidget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }
}
