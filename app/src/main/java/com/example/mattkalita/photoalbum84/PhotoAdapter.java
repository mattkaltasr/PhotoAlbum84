package com.example.mattkalita.photoalbum84;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by yigit on 11/27/2016.
 */

public class PhotoAdapter  extends ArrayAdapter<Photo> {
    public PhotoAdapter(Context context, ArrayList<Photo> photos) {
        super(context, 0, photos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Photo photo = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.photo_item, parent, false);
        }
        // Lookup view for data population
        TextView photoName = (TextView) convertView.findViewById(R.id.photoName);
        TextView photoDate = (TextView) convertView.findViewById(R.id.photoDate);
        TextView photoTags = (TextView) convertView.findViewById(R.id.photoTags);
        ImageView photoImg = (ImageView) convertView.findViewById(R.id.photo);
        // Populate the data into the template view using the data object
        photoName.setText(photo.getName());
        photoDate.setText(photo.getDate());
        photoTags.setText(photo.getTags());
        photoImg.setImageBitmap(photo.getBitmap());
        // Return the completed view to render on screen
        return convertView;
    }
}