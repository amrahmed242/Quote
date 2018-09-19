package com.aa.quote.widgets;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.RemoteViews;
import com.aa.quote.R;
import com.aa.quote.callBack.ServerCall;
import com.aa.quote.db.AppDatabase;
import com.aa.quote.model.Quote;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Implementation of App Widget functionality.
 */
public class QuoteWidget extends AppWidgetProvider{


    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Quote quote) {
        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.quote_widget);


            try {
                Picasso.get()
                        .load(quote.getWallpaper())
                        .error(R.drawable.splash)
                        .into(views, R.id.widget_img, new int[]{appWidgetId});
            } catch (Exception e) {
                e.printStackTrace();
            }
            views.setTextViewText(R.id.widget_quote_content, quote.getQuote());
            views.setTextViewText(R.id.widget_author, quote.getAuthor());




        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/tweet?text="+quote.getQuote()+"  #"+quote.getAuthor()));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, browserIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_share_button, pendingIntent);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        if(checkPrefrence(context)){

            callRetrofit(context,appWidgetManager,appWidgetIds);

        }else{

            new favouritesAsyncTask(context,appWidgetManager,appWidgetIds).execute();

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


    @NonNull
    private Boolean checkPrefrence(Context context){

        try{
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            Resources r = context.getResources();

            String WIDGET_SETTING_KEY = r.getString(R.string.WIDGET_SETTING_KEY);
            String WIDGET_DEFAULT_SETTING = r.getString(R.string.WIDGET_DEFAULT_SETTING);

            return pref.getString(WIDGET_SETTING_KEY, WIDGET_DEFAULT_SETTING).equals(WIDGET_DEFAULT_SETTING);

        }catch (Exception e){
            return true;
        }

    }

    private void callRetrofit(final Context context,final AppWidgetManager AppWidgetManager,final int[] AppWidgetIds){


        new ServerCall(new Callback<ArrayList<Quote>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Quote>> call, Response<ArrayList<Quote>> response) {
                List<Quote> quoteList= response.body();

                if(quoteList != null){

                    final Random random=new Random();
                    for (int appWidgetId : AppWidgetIds) {
                    updateAppWidget(context, AppWidgetManager, appWidgetId, quoteList.get(random.nextInt(quoteList.size())));
                    }

                }

            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Quote>> call, @NonNull Throwable t) { }
        });


    }

    private static class favouritesAsyncTask extends AsyncTask<Void, Void, List<Quote>> {

        @SuppressLint("StaticFieldLeak")
        final Context context;
        final AppWidgetManager appWidgetManager;
        final int[] appWidgetIds;


        favouritesAsyncTask(final Context context,final AppWidgetManager appWidgetManager,final int[] appWidgetIds) {
            this.context=context;
            this.appWidgetManager=appWidgetManager;
            this.appWidgetIds=appWidgetIds;
        }

        @Override
        protected List<Quote> doInBackground(Void... voids) {

            try {

                AppDatabase db = AppDatabase.getDatabase(context);
                return db.QuoteModel().getQuotes();

            }catch (Exception e){

                return null;
            }

        }

        @Override
        protected void onPostExecute(List<Quote> List) {
            super.onPostExecute(List);

            if(List != null && List.size() > 0){

                final Random random=new Random();
                for (int appWidgetId : appWidgetIds) {

                    updateAppWidget(context, appWidgetManager, appWidgetId, List.get(random.nextInt(List.size())));
                }

            }

        }
    }


}