package com.example.tyc.osmtest.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.tyc.osmtest.MapsActivity.GOOGLE_MAP;
import static com.example.tyc.osmtest.MapsActivity.OPEN_STREET_MAP;
import static com.example.tyc.osmtest.MapsActivity.RUDY_MAP;
import static com.example.tyc.osmtest.MapsActivity.WMTS_MAP;
import static com.example.tyc.osmtest.utils.GoogleMapOfflineFileController.GOOGLE_UPPER_ZOOM_TILE_URL;
import static com.example.tyc.osmtest.utils.GoogleMapOfflineFileController.OSM_UPPER_ZOOM_TILE_URL;
import static com.example.tyc.osmtest.utils.GoogleMapOfflineFileController.RUDY_UPPER_ZOOM_TILE_URL;
import static com.example.tyc.osmtest.utils.GoogleMapOfflineFileController.WMTS_UPPER_ZOOM_TILE_URL;

/**
 * Created by biji on 2018/8/2.
 */

public class OnlineTileProvider implements TileProvider {

    private static final String TAG = "GoogleMapOfflineTilePro";

    private static final int TILE_WIDTH = 256;
    private static final int TILE_HEIGHT = 256;
    private static final int BUFFER_SIZE = 16 * 1024;
    private int mapType;

    public OnlineTileProvider(int mapType){
        this.mapType = mapType;
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {
        Tile tile;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        getBitmapFromURL(x, y, zoom).compress(Bitmap.CompressFormat.JPEG, 100, stream);
        tile = new Tile(256, 256, stream.toByteArray());
        return tile;
    }

    private Bitmap getBitmapFromURL(int x, int y, int zoom)
    {
        URL url;
        Log.d(TAG, String.format(GOOGLE_UPPER_ZOOM_TILE_URL, x, y, zoom));
        try
        {
            if (mapType == GOOGLE_MAP) {
                url = new URL(String.format(GOOGLE_UPPER_ZOOM_TILE_URL, x, y, zoom));
                Log.d(TAG, String.format(GOOGLE_UPPER_ZOOM_TILE_URL, x, y, zoom));
            } else if (mapType == OPEN_STREET_MAP) {
                url = new URL(String.format(OSM_UPPER_ZOOM_TILE_URL, zoom, x, y));
                Log.d(TAG, String.format(OSM_UPPER_ZOOM_TILE_URL, zoom, x, y));
            } else if (mapType == RUDY_MAP) {
                url = new URL(String.format(RUDY_UPPER_ZOOM_TILE_URL, zoom, x, y));
                Log.d(TAG, String.format(RUDY_UPPER_ZOOM_TILE_URL, zoom, x, y));
            } else if (mapType == WMTS_MAP) {
                url = new URL(String.format(WMTS_UPPER_ZOOM_TILE_URL, zoom, y, x));
                Log.d(TAG, String.format(WMTS_UPPER_ZOOM_TILE_URL, zoom, y, x));
            } else {
                url = new URL(String.format(GOOGLE_UPPER_ZOOM_TILE_URL, x, y, zoom));
            }
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