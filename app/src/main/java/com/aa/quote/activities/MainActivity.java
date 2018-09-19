package com.aa.quote.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.aa.quote.R;
import com.aa.quote.adapters.ViewPagerAdapter;
import com.aa.quote.databinding.ActivityMainBinding;
import com.aa.quote.model.Quote;
import com.eftimoff.viewpagertransformers.ZoomOutTranformer;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding B;
    private static ArrayList<Quote> quotesList=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        B= DataBindingUtil.setContentView(this, R.layout.activity_main);

        Intent intent=getIntent();

        if (intent.getExtras() != null) {

            String KEY=getApplicationContext().getResources().getString(R.string.QUOTE_FEED_ARGUMENTS_KEY);
            quotesList=intent.getParcelableArrayListExtra(KEY);

        }

        initiate_UI();


    }

    private void initiate_UI(){

        setSupportActionBar(B.toolbar);
        if(getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.bebas);
        B.TapLayout.setTypeface(typeface);

        ViewPagerAdapter pagerAdapter=new ViewPagerAdapter(this,getSupportFragmentManager(),quotesList);

        B.ViewPager.setAdapter(pagerAdapter);
        B.ViewPager.setPageTransformer(true,new ZoomOutTranformer()); //TabletTransformer
        B.TapLayout.setViewPager(B.ViewPager,1);

    }

}