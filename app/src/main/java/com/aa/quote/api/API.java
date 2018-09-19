package com.aa.quote.api;

import com.aa.quote.model.Quote;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.GET;

public interface API {


    String BASE_URL = "https://api.jsonbin.io/b/";

    @GET("5b71c2bc2878011e8d6a81b4")
    Call<ArrayList<Quote>> getQuotes();


}