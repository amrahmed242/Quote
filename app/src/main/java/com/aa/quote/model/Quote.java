package com.aa.quote.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class Quote implements Parcelable {


    @SuppressWarnings("NullableProblems")
    @PrimaryKey
    @NonNull
    @Expose
    @SerializedName("quote")
    private String quote;
    @Expose
    @SerializedName("author")
    private String author;
    @Expose
    @SerializedName("wallpaper")
    private String wallpaper;
    private Boolean liked=false;


    public Quote() { }

    protected Quote(Parcel in) {
        quote = in.readString();
        author = in.readString();
        wallpaper = in.readString();
        byte tmpLiked = in.readByte();
        liked = tmpLiked == 0 ? null : tmpLiked == 1;
    }

    public static final Creator<Quote> CREATOR = new Creator<Quote>() {
        @Override
        public Quote createFromParcel(Parcel in) {
            return new Quote(in);
        }

        @Override
        public Quote[] newArray(int size) {
            return new Quote[size];
        }
    };

    @NonNull
    public String getQuote() {
        return quote;
    }

    public String getAuthor() {
        return author;
    }

    public String getWallpaper() {
        return wallpaper;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public void setQuote(@NonNull String quote) {
        this.quote = quote;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setWallpaper(String wallpaper) {
        this.wallpaper = wallpaper;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(quote);
        dest.writeString(author);
        dest.writeString(wallpaper);
        dest.writeByte((byte) (liked == null ? 0 : liked ? 1 : 2));
    }
}