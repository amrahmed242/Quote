package com.aa.quote.db;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.aa.quote.model.Quote;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface QuoteModelDao {



    @Query("select COUNT(*) FROM Quote")
    LiveData<Integer> getLiveCount();

    @Query("select * from Quote")
    List<Quote> getQuotes();

    @Query("select * from Quote")
    LiveData<List<Quote>> getLiveQuotes();

    @Query("select * from Quote where quote = :quote")
    Quote getQuote(String quote);

    @Insert(onConflict = REPLACE)
    void addQuote(Quote quote);

    @Delete
    void deleteQuote(Quote quote);

    @Query("DELETE FROM quote")
    void dropQuotes();

}
