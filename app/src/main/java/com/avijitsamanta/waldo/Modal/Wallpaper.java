package com.avijitsamanta.waldo.Modal;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class Wallpaper implements Parcelable {
    @Exclude
    public String id;
    private String res;
    private String size;
    private String title;
    private String url;
    @Exclude
    public String category;
    @Exclude
    public boolean isFavourite = false;

    public Wallpaper(String id, String res, String size, String title, String url, String category) {
        this.id = id;
        this.res = res;
        this.size = size;
        this.title = title;
        this.url = url;
        this.category = category;
    }

    protected Wallpaper(Parcel in) {
        id = in.readString();
        res = in.readString();
        size = in.readString();
        title = in.readString();
        url = in.readString();
        category = in.readString();
        isFavourite = in.readByte() != 0;
    }

    public static final Creator<Wallpaper> CREATOR = new Creator<Wallpaper>() {
        @Override
        public Wallpaper createFromParcel(Parcel in) {
            return new Wallpaper(in);
        }

        @Override
        public Wallpaper[] newArray(int size) {
            return new Wallpaper[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(res);
        parcel.writeString(size);
        parcel.writeString(title);
        parcel.writeString(url);
        parcel.writeString(category);
        parcel.writeByte((byte) (isFavourite ? 1 : 0));
    }
}
