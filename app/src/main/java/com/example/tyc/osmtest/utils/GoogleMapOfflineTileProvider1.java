package com.example.tyc.osmtest.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by biji on 2018/7/25.
 */

public class GoogleMapOfflineTileProvider1 implements TileProvider
{

    private static final String UPPER_ZOOM_TILE_URL;


    static
    {

        UPPER_ZOOM_TILE_URL = "http://mt0.google.com/vt/lyrs=m&hl=ru&x=%d&y=%d&z=%d&scale=1&s=Galileo";
    }


    private static final String TAG = GoogleMapOfflineTileProvider1.class.getName();

//    private SQLiteMapDatabase sqLiteMapDatabase;


//    public GoogleMapOfflineTileProvider1(File file)
//    {
//        this(file.getAbsolutePath());
//    }


//    public GoogleMapOfflineTileProvider1(String pathToFile)
//    {
//        sqLiteMapDatabase = new SQLiteMapDatabase();
//        sqLiteMapDatabase.setFile(new File(pathToFile));
//    }


    @Override
    public Tile getTile(int x, int y, int z)
    {
        Tile tile = NO_TILE;

//        if ( sqLiteMapDatabase.existsTile(x, y, z) )
//        {
//            tile = new Tile(256, 256, sqLiteMapDatabase.getTile(x, y, z));
//        }
//        else if ( z < 11 )
//        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            getBitmapFromURL(x, y, z).compress(Bitmap.CompressFormat.JPEG, 100, stream);
            tile = new Tile(256, 256, stream.toByteArray());
//        }

        return tile;
    }


    public static Bitmap getBitmapFromURL(int x, int y, int z)
    {
        Log.d(TAG, String.format(UPPER_ZOOM_TILE_URL, x, y, z));
        try
        {
            URL url = new URL(String.format(UPPER_ZOOM_TILE_URL, x, y, z));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            return BitmapFactory.decodeStream(connection.getInputStream());
        }
        catch (IOException e)
        {
            Log.d(TAG, "exception when retrieving bitmap from internet" + e.toString());
            return null;
        }
    }
}