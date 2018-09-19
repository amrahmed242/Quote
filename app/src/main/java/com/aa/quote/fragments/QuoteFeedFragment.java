package com.aa.quote.fragments;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import com.aa.quote.R;
import com.aa.quote.adapters.QuotesAdapter;
import com.aa.quote.callBack.ServerCall;
import com.aa.quote.databinding.FragmentQuoteFeedBinding;
import com.aa.quote.callBack.DbCallback;
import com.aa.quote.model.Quote;
import com.aa.quote.viewmodels.QuotesListViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuoteFeedFragment extends Fragment implements DbCallback,SharedPreferences.OnSharedPreferenceChangeListener{

    private Context context;
    private SharedPreferences pref;
    private Parcelable RecyclerState;
    private FragmentQuoteFeedBinding B;
    private static List<Quote> quotesList=null;
    private String RECYCLER_SETTING_KEY,RECYCLER_DEFAULT_SETTING;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        B = DataBindingUtil.inflate(inflater, R.layout.fragment_quote_feed, container, false);

        if(getArguments() != null) {
            quotesList = Objects.requireNonNull(getArguments())
                    .getParcelableArrayList(context.getResources().getString(R.string.QUOTE_FEED_ARGUMENTS_KEY));
        }

        initPrefrence();

        if(quotesList==null){

            B.refreshButton.setVisibility(View.VISIBLE);
            B.refreshButtonRippleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refreshData();
                }
            });
            animateView(B.refreshLogo);

        }else{
            manageQuotes();
        }


        pref.registerOnSharedPreferenceChangeListener(this);
        return B.getRoot();
    }

    //overriding onDestroy to unRegister the SharedPreferences Listener
    @Override
    public void onDestroy() {
        super.onDestroy();

        pref.unregisterOnSharedPreferenceChangeListener(this);

    }

    //overriding onSaveInstanceState to save the state of the recycler
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if(B.quoteFeedRecycler.getLayoutManager() != null) {

            RecyclerState = B.quoteFeedRecycler.getLayoutManager().onSaveInstanceState();
            outState.putParcelable(getResources().getString(R.string.FEED_RECYCLER_STATE_KEY), RecyclerState);

        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {

            RecyclerState= savedInstanceState.getParcelable(getResources().getString(R.string.FEED_RECYCLER_STATE_KEY));

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (RecyclerState != null && B.quoteFeedRecycler.getLayoutManager() != null) {

            B.quoteFeedRecycler.getLayoutManager().onRestoreInstanceState(RecyclerState);

        }

    }

    //overriding onSharedPreferenceChanged to detect Preference changes
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(RECYCLER_SETTING_KEY)) {



            if (pref.getString(RECYCLER_SETTING_KEY, RECYCLER_DEFAULT_SETTING).equals(RECYCLER_DEFAULT_SETTING)) {

                initRecycler(true);

            }else{

                initRecycler(false);
            }

        }
    }

    @Override
    public void onCheckComplete(List<Quote> quoteList) {

        quotesList=quoteList;
        initRecycler(checkPrefrence());
        B.quoteFeedRecycler.animate().alpha(1).setDuration(1500);

    }

    private void animateView(ImageView view){

        Animation rotation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        rotation.setFillAfter(true);
        view.startAnimation(rotation);

    }

    private void manageQuotes(){

        ViewModelProviders.of(this).get(QuotesListViewModel.class).checkQuote(quotesList,this);

    }

    private void initRecycler(Boolean defaultSetting){

        final QuotesAdapter quotesAdapter= new QuotesAdapter(this,quotesList,context,defaultSetting);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());

        B.quoteFeedRecycler.setLayoutManager(manager);
        B.quoteFeedRecycler.setAdapter(quotesAdapter);

    }

    private void refreshData(){

        new ServerCall(new Callback<ArrayList<Quote>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Quote>> call, @NonNull Response<ArrayList<Quote>> response) {
                quotesList=response.body();
                manageQuotes();
                B.refreshButton.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Quote>> call, @NonNull Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.Toast_check_connection), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initPrefrence() {

        pref = PreferenceManager.getDefaultSharedPreferences(context);
        Resources r = getResources();

        RECYCLER_SETTING_KEY = r.getString(R.string.RECYCLER_SETTING_KEY);
        RECYCLER_DEFAULT_SETTING = r.getString(R.string.RECYCLER_DEFAULT_SETTING);

    }

    private Boolean checkPrefrence(){


        return pref.getString(RECYCLER_SETTING_KEY, RECYCLER_DEFAULT_SETTING).equals(RECYCLER_DEFAULT_SETTING);

    }

}