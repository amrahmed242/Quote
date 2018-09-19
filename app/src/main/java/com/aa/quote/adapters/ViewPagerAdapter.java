package com.aa.quote.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.aa.quote.R;
import com.aa.quote.fragments.FavouritesFragment;
import com.aa.quote.fragments.QuoteFeedFragment;
import com.aa.quote.fragments.SettingsFragment;
import com.aa.quote.model.Quote;
import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter{
    private final Context context;
    private final ArrayList<Quote> quoteList;



    public ViewPagerAdapter(Context context, FragmentManager fragmentManager, @Nullable ArrayList<Quote> quoteList) {
        super(fragmentManager);
        this.context = context;
        this.quoteList=quoteList;
    }



    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new FavouritesFragment();
            case 1:

                if (quoteList == null) {

                    return new QuoteFeedFragment();

                } else {

                    String KEY = context.getResources().getString(R.string.QUOTE_FEED_ARGUMENTS_KEY);

                    Bundle arguments = new Bundle();
                    arguments.putParcelableArrayList(KEY, quoteList);
                    QuoteFeedFragment quoteFeedFragment = new QuoteFeedFragment();

                    quoteFeedFragment.setArguments(arguments);

                    return quoteFeedFragment;
                }

            default:
                return new SettingsFragment();
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 3;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return context.getString(R.string.app_name);
            case 1:
                return context.getString(R.string.FIRST_USE_KEY);
            case 2:
                return "xx";
            default:
                return null;
        }
    }



}
