package com.example.mattkalita.photoalbum84;

import android.graphics.Bitmap;

/**
 * Created by yigit on 11/27/2016.
 */

public class Photo {

    private String date;
    private String tags;
    private String name;
    private Bitmap bitmap;

    public Photo(Bitmap bitmap, String name, String date, String tags) {
        this.bitmap = bitmap;
        this.name = name;
        this.date = date;
        this.tags = tags;
    }

    public String getName(){
        return name;
    }
    public String getDate(){
        return date;
    }
    public String getTags(){
        return tags;
    }
    public Bitmap getBitmap(){
        return bitmap;
    }
}
