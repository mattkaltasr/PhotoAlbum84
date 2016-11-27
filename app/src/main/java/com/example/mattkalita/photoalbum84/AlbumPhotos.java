package com.example.mattkalita.photoalbum84;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.R.attr.name;

public class AlbumPhotos extends AppCompatActivity {

    private static int PICK_IMAGE_REQUEST = 1;
    PhotoAdapter adapter;
    GridView gridView;
    Button addButton;
    ArrayList<Photo> imageViewArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_photos);


        gridView = (GridView) findViewById(R.id.photos_gridview);
        imageViewArrayList = new ArrayList<>();
        adapter= new PhotoAdapter(this,imageViewArrayList);
        gridView.setAdapter(adapter);

        addButton = (Button) findViewById(R.id.add_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Add Photo"), PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(bitmap);

                String name = "New Photo";
                String date = "xx/xx/xxxx";
                String tags = "Tag1, Tag2, Tag3";
                Photo p = new Photo(bitmap,name,date,tags);
                imageViewArrayList.add(p);
            } catch (IOException e) {
            e.printStackTrace();
            }
        }


        adapter.notifyDataSetChanged();


    }

}
