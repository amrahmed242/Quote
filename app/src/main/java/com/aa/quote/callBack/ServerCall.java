package com.aa.quote.callBack;

import com.aa.quote.api.API;
import com.aa.quote.model.Quote;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerCall {


    public ServerCall(Callback<ArrayList<Quote>> callback) {


        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(API.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final API api=retrofit.create(API.class);

        Call<ArrayList<Quote>> quoteCall = api.getQuotes();

        quoteCall.enqueue(callback);

    }



}