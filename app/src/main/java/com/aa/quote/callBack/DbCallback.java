package com.aa.quote.callBack;

import com.aa.quote.model.Quote;

import java.util.List;

public interface DbCallback {

    // Define data to return from AysncTask
    void onCheckComplete(List<Quote> quoteList);
}