package com.example.mattkalita.photoalbum84;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

/**
 * Created by matt kalita on 12/3/2016.
 */

public class PhotoGridActivity extends Activity implements OnNavigationListener {

    private Controller ctrl;
    private String album;
    private static int RESULT_LOAD_IMAGE = 1;
    private static int CAMERA_REQUEST = 2;
    private HashMap<String, Album> albumsmap;
    private List<String> albums;
    private Cursor imageCursor = null;
    private Context ctx;
    private GridView gv;
    private PhotoGridAdapter photoGridAdapter;
    boolean first = false;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onResume() {
        super.onResume(); // Always call the superclass method first
        init();
    }

    @Override
    public void onPause() {
        super.onPause();
        ctrl = Controller.getInstance(getApplicationContext());
        ctrl.write();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (imageCursor != null) {
            imageCursor.close();
            imageCursor = null;
        }
    }

    public void init() {
        setContentView(R.layout.grid_fragment);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setActionBar(toolbar);
        ctx = this;
        ctrl = Controller.getInstance(getApplicationContext());

        this.album = getIntent().getExtras().getString(AppConstants.ALBUM_TITLE);
        gv = (GridView) findViewById(R.id.grid);
        if (!ctrl.getAlbums().get(album).getPhotos().isEmpty()) {
            int placeholderWidth = (int) (1.1 * ContextCompat.getDrawable(this, R.drawable.no_image).getIntrinsicWidth());
            gv.setColumnWidth(placeholderWidth);
            photoGridAdapter = new PhotoGridAdapter(this, album);
            gv.setAdapter(photoGridAdapter);
        } else {
            TextView tv = (TextView) findViewById(R.id.label);
            tv.setSelectAllOnFocus(false);
            tv.setGravity(Gravity.CENTER);
            tv.setText("No Photos");
        }
/// need to set this as tool bar or pull tabs

        final ActionBar actionBar = getActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        albumsmap = ctrl.getAlbums();

        if (!albumsmap.isEmpty()) {

            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

            albums = new ArrayList<String>(albumsmap.keySet());
            actionBar
                    .setListNavigationCallbacks(new ArrayAdapter<String>(actionBar
                            .getThemedContext(), android.R.layout.simple_list_item_1, android.R.id.text1, albums), this);
            actionBar.setSelectedNavigationItem(albums.indexOf(album));
        } else {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }

    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (first) {
            Intent i = new Intent(ctx, PhotoGridActivity.class);
            i.putExtra(AppConstants.ALBUM_TITLE, albumsmap.get(albums.get(itemPosition))
                    .getAlbumName());
            startActivity(i);
            finish();
            return true;
        } else {
            first = true;
            return false;
        }
    }

    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.album_menu, menu);

        if (!isIntentAvailable(this, MediaStore.ACTION_IMAGE_CAPTURE)) {
            menu.findItem(R.id.take_photo).setEnabled(false);
            menu.findItem(R.id.take_photo).setVisible(false);
            menu.findItem(R.id.take_photo).setActionView(View.GONE);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish(); // returns to calling activity, in this case MainActivity
        } else if (id == R.id.add_photo) {
            dispatchChoosePictureIntent();
        } else if (id == R.id.take_photo) {
            dispatchTakePictureIntent();
        } else if (id == R.id.rename_album) {
            renameAlbum();
        } else if (id == R.id.delete_album) {
            deleteAlbum();
        } else {
            Toast.makeText(this, "Option not recognized: " + id, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    public void dispatchChoosePictureIntent() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAMERA_REQUEST);
    }

    @Override

    //crash caused here
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMAGE && null != data) {
                Uri uri= data.getData();
                File file= new File(uri.getPath());
               String path = data.getData().getPath();
                //this method cause crash
               //  String path = getRealPath(ctx,uri);
                //  Uri imageUri = data.getData();// declare a stream to read the image data from the SD Card.
                //  InputStream inputStream;

                Context context = getApplicationContext();
                CharSequence text = path;
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();




                try {
                    //  inputStream = getContentResolver().openInputStream(imageUri);

                    // get a bitmap from the stream.
                    //   Bitmap image = BitmapFactory.decodeStream(inputStream);
                    //   Photo temp=new Photo(imageUri,null);
                    if (ctrl.addPhotoToAlbum(uri,path, this.album)) {
                        this.recreate();
                    } else {
                        Toast.makeText(getApplicationContext(), "Could not add photo.", Toast.LENGTH_LONG)
                                .show();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
                //needs to change
                Uri temp=null;
                final String[] imageColumns = {MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DATA};
                final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
                imageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
                if (imageCursor.moveToFirst()) {
                    String fullPath = imageCursor.getString(imageCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    Log.e("TAG", "getLastImageId::path " + fullPath);

                    try {
                        if (ctrl.addPhotoToAlbum(temp,fullPath, album)) {
                            Toast.makeText(getApplicationContext(), "Photo added to " + this.album
                                    + ".", Toast.LENGTH_SHORT).show();
                        }
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "Photo file not found.", Toast.LENGTH_SHORT)
                                .show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Photo file not found.", Toast.LENGTH_SHORT)
                                .show();
                        e.printStackTrace();
                    }
                    imageCursor.close();
                    imageCursor = null;
                } else {
                    Toast.makeText(getApplicationContext(), "Photo file not found.", Toast.LENGTH_SHORT)
                            .show();
                }

            } else {
                Log.e("Request not recognized", "code: " + requestCode + ", resultCode = "
                        + resultCode);
            }
        } else {
            Log.e("Request failed", "requestCode: " + requestCode);
        }
    }
    ///
    //need this reworked caused click crash
    public String getPathFromUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        return picturePath;
    }

    private void renameAlbum() {
        final InputMethodManager imgr = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        final EditText name = new EditText(this);
        name.setHint("Album Name");
        final AlertDialog d = new AlertDialog.Builder(this).setView(name)
                .setTitle(R.string.rename_album)
                .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        // Do nothing here. We override the onclick
                    }
                }).setNegativeButton(android.R.string.cancel, null).create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                imgr.showSoftInput(name, 0);
                name.requestFocus();

                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String t = name.getText().toString();
                        if (t.isEmpty()) {
                            Toast.makeText(ctx, "Album name cannot be empty.", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            if (ctrl.renameAlbum(album, t)) {
                                album = t;
                                Toast.makeText(ctx, "Renamed album to " + t, Toast.LENGTH_SHORT)
                                        .show();
                                // MainActivity.this.recreate();
                                refresh();
                                d.dismiss();
                            } else {
                                Toast.makeText(ctx, "Album already " + t + " exists", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }
                });
            }
        });
        d.show();
    }

    private void deleteAlbum() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Delete Album");
        alertDialogBuilder.setMessage("Are you sure you want to delete this album?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (ctrl.deleteAlbum(PhotoGridActivity.this.album)) {
                            finish();
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void refresh() {
        getActionBar().setTitle(this.album);
        albums = new ArrayList<String>(ctrl.getAlbums().keySet());
        getActionBar()
                .setListNavigationCallbacks(new ArrayAdapter<String>(getActionBar()
                        .getThemedContext(), android.R.layout.simple_list_item_1, android.R.id.text1, albums), this);
        getActionBar().setSelectedNavigationItem(albums.indexOf(album));
        photoGridAdapter = new PhotoGridAdapter(ctx, this.album);
        gv.setAdapter(photoGridAdapter);
        gv.invalidate();
    }

    public static String getRealPath(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


}
