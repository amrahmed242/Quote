package com.aa.quote.adapters;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aa.quote.R;
import com.aa.quote.databinding.QuoteItemBinding;
import com.aa.quote.model.Quote;
import com.aa.quote.viewmodels.QuotesListViewModel;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuotesAdapter extends RecyclerView.Adapter<QuotesAdapter.ViewHolder> {

    private final Context context;
    private Boolean FAV=false;
    private List<Quote> quoteList=new ArrayList<>();
    private final QuotesListViewModel viewModel;
    private final Boolean defaultSetting;

    public QuotesAdapter(Fragment fragment,List<Quote> quoteList, Context context,Boolean defaultSetting) {
        this.quoteList = quoteList;
        this.context = context;
        this.defaultSetting=defaultSetting;
        viewModel = ViewModelProviders.of(fragment).get(QuotesListViewModel.class);

    }

    public QuotesAdapter(Fragment fragment, LifecycleOwner owner, final Context context,Boolean defaultSetting) {
        FAV=true;
        this.context = context;
        this.defaultSetting=defaultSetting;
        viewModel = ViewModelProviders.of(fragment).get(QuotesListViewModel.class);

        viewModel.getLiveQuoteList().observe(owner, new Observer<List<Quote>>() {

            @Override
            public void onChanged(@Nullable List<Quote> List) {

                quoteList=List;
                notifyDataSetChanged();

            }

        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final QuoteItemBinding B;

        ViewHolder(QuoteItemBinding b) {
            super(b.getRoot());
            this.B=b;

            B.shareButton.setEventListener(new SparkEventListener(){

                @Override
                public void onEvent(ImageView button, boolean buttonState) {}
                @Override
                public void onEventAnimationEnd(ImageView button, boolean buttonState) {

                    String quote=quoteList.get(getAdapterPosition()).getQuote();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/intent/tweet?text="+quote));
                    context.startActivity(browserIntent);
                    B.shareButton.setChecked(false);

                }
                @Override
                public void onEventAnimationStart(ImageView button, boolean buttonState) {}

            });

            B.heartButton.setEventListener(new SparkEventListener(){

                @Override
                public void onEvent(ImageView button, boolean buttonState) {

                    if (buttonState){
                        quoteList.get(getAdapterPosition()).setLiked(true);
                        viewModel.addQuote(quoteList.get(getAdapterPosition()));
                    }else {
                        Quote quote=quoteList.get(getAdapterPosition());
                        quote.setLiked(false);
                        if(FAV)notifyItemRemoved(getAdapterPosition());

                        viewModel.deleteQuote(quote);
                    }

                }
                @Override
                public void onEventAnimationEnd(ImageView button, boolean buttonState) { }
                @Override
                public void onEventAnimationStart(ImageView button, boolean buttonState) {}

            });

        }

    }

    @NonNull
    @Override
    public QuotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        QuoteItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.quote_item, parent, false);
        return new ViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder H, int position) {

        Quote quote= quoteList.get(position);
        H.B.setQuote(quote);


        if(quote.getLiked()){

            H.B.heartButton.setChecked(true);

        }else{

            H.B.heartButton.setChecked(false);
        }


        //load img
        try {
            Picasso.get()
                    .load(quote.getWallpaper())
                    .error(R.drawable.splash)
                    .into(H.B.img);
        } catch (Exception e) { e.printStackTrace(); }


        //auto sized text
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(H.B.quoteContent, 15,35,2, TypedValue.COMPLEX_UNIT_SP);


        if(defaultSetting) {
            H.B.heartButton.setVisibility(View.GONE);
            H.B.shareButton.setVisibility(View.GONE);


            //region icons on&off listner
            H.B.getRoot().findViewById(R.id.root_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (H.B.heartButton.getAlpha() == 0) {
                        H.B.heartButton.setVisibility(View.VISIBLE);
                        H.B.shareButton.setVisibility(View.VISIBLE);
                        H.B.heartButton.animate().alpha(1).setDuration(500);
                        H.B.shareButton.animate().alpha(1).setDuration(500);
                    } else {
                        H.B.heartButton.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                H.B.heartButton.setVisibility(View.GONE);
                            }
                        });
                        H.B.shareButton.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                H.B.shareButton.setVisibility(View.GONE);
                            }
                        });
                    }

                }
            });
            //endregion
        }else{

            H.B.heartButton.setVisibility(View.VISIBLE);
            H.B.shareButton.setVisibility(View.VISIBLE);
            H.B.heartButton.setAlpha(1);
            H.B.shareButton.setAlpha(1);

        }

    }

    @Override
    public int getItemCount() {
        return quoteList.size();
    }

}