package com.example.mattkalita.photoalbum84;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    GridView gridView;
    ArrayList<String> albums;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        albums = new ArrayList<>();
        gridView = (GridView) findViewById(R.id.albums_gridview);

        /*
        for(Album album : READALLALBUMS) {
            albums.add(album.getNamet());
        }
        */

        albums.add("Album1");
        albums.add("Album2");
        albums.add("Album3");
        albums.add("Album4");
        albums.add("Album5");


        ArrayAdapter<String> adapter=new ArrayAdapter<> (this,
                R.layout.support_simple_spinner_dropdown_item,albums);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(),AlbumPhotos.class);
                startActivity(intent);
                setContentView(R.layout.album_grid);
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast.makeText(getBaseContext(), "You long-clicked on " + albums.get(i),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });


    }
}
