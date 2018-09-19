package com.aa.quote.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import com.aa.quote.db.AppDatabase;
import com.aa.quote.callBack.DbCallback;
import com.aa.quote.fragments.QuoteFeedFragment;
import com.aa.quote.model.Quote;
import java.util.List;

public class QuotesListViewModel extends AndroidViewModel {

    private final LiveData<List<Quote>> quoteList ;
    private final LiveData<Integer> count;
    private final AppDatabase appDatabase;

    public QuotesListViewModel(Application application) {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        quoteList = appDatabase.QuoteModel().getLiveQuotes();
        count = appDatabase.QuoteModel().getLiveCount();
    }


    public LiveData<Integer> getLiveCount(){ return count; }

    public LiveData<List<Quote>> getLiveQuoteList() {
        return quoteList;
    }

    public void addQuote(Quote quote){
        new addAsyncTask(appDatabase).execute(quote);
    }

    public void deleteQuote(Quote quote) {
        new deleteAsyncTask(appDatabase).execute(quote);
    }

    public void dropQuotes(){ new dropAsyncTask(appDatabase).execute();}

    public void checkQuote(List<Quote> quotesList,QuoteFeedFragment fragment){
        //noinspection unchecked
        new checkAsyncTask(appDatabase,fragment).execute(quotesList);
    }


    private static class checkAsyncTask extends AsyncTask<List<Quote>, Void, List<Quote>> {

        private final AppDatabase db;
        private final DbCallback dbCallback;

        checkAsyncTask(AppDatabase appDatabase,QuoteFeedFragment fragment) {

            db = appDatabase;
            dbCallback= fragment;

        }

        @Override
        @SafeVarargs
        @SuppressWarnings("unchecked")
        protected final List<Quote> doInBackground(List<Quote>... quotesList) {


            for(int i=0;i<quotesList[0].size();i++){

                Quote quote=db.QuoteModel().getQuote(quotesList[0].get(i).getQuote());
                if(quote!=null)quotesList[0].get(i).setLiked(true);

            }

            return quotesList[0];
        }

        @Override
        protected void onPostExecute(List<Quote> quoteList) {

            dbCallback.onCheckComplete(quoteList);

        }

    }

    private static class addAsyncTask extends AsyncTask<Quote, Void, Void> {

        private final AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Quote... params) {
            db.QuoteModel().addQuote(params[0]);
            return null;
        }

    }

    private static class deleteAsyncTask extends AsyncTask<Quote, Void, Void> {

        private final AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Quote... params) {
            db.QuoteModel().deleteQuote(params[0]);
            return null;
        }

    }

    private static class dropAsyncTask extends AsyncTask<Void, Void, Void> {

        private final AppDatabase db;

        dropAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            db.QuoteModel().dropQuotes();
            return null;
        }

    }

}