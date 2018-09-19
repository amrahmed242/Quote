package com.aa.quote.fragments;

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
import com.aa.quote.adapters.QuotesAdapter;
import com.aa.quote.databinding.FragmentFavouritesBinding;
import android.view.View;
import android.view.ViewGroup;
import com.aa.quote.R;

public class FavouritesFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    private Context context;
    private SharedPreferences pref;
    private Parcelable RecyclerState;
    private FragmentFavouritesBinding B;
    private String RECYCLER_SETTING_KEY,RECYCLER_DEFAULT_SETTING;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        B = DataBindingUtil.inflate(inflater, R.layout.fragment_favourites, container, false);

        initPrefrence();
        initRecycler(checkPrefrence());
        return B.getRoot();
    }

    //overriding onSaveInstanceState to save the state of the recycler
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if(B != null) {

            RecyclerState = B.quoteFavRecycler.getLayoutManager().onSaveInstanceState();
            outState.putParcelable(getResources().getString(R.string.FAV_RECYCLER_STATE_KEY), RecyclerState);

        }
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {

            RecyclerState= savedInstanceState.getParcelable(getResources().getString(R.string.FAV_RECYCLER_STATE_KEY));

        }

    }


    @Override
    public void onResume() {
        super.onResume();


        if (RecyclerState != null) {


            B.quoteFavRecycler.getLayoutManager().onRestoreInstanceState(RecyclerState);

        }

    }


    private void initRecycler(Boolean defaultSetting){

        QuotesAdapter quotesAdapter = new QuotesAdapter(this, FavouritesFragment.this, getContext(),defaultSetting);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());

        B.quoteFavRecycler.setLayoutManager(manager);
        B.quoteFavRecycler.setAdapter(quotesAdapter);

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


    //overriding onSharedPreferenceChanged to detect Preference changes
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(RECYCLER_SETTING_KEY)) {


            if (pref.getString(key, RECYCLER_DEFAULT_SETTING).equals(RECYCLER_DEFAULT_SETTING)) {

                initRecycler(true);

            }else{

                initRecycler(false);
            }

        }
    }

}