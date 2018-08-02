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

/**
 * Created by biji on 2018/7/25.
 */

public class GoogleMapOfflineTileProvider implements TileProvider {

    private static final String TAG = "GoogleMapOfflineTilePro";

    private static final String UPPER_ZOOM_TILE_URL = "http://mt0.google.com/vt/lyrs=m&hl=ru&x=%d&y=%d&z=%d&scale=1&s=Galileo";

    private static final int TILE_WIDTH = 256;
    private static final int TILE_HEIGHT = 256;
    private static final int BUFFER_SIZE = 16 * 1024;
    private String offlineMapName;

    public GoogleMapOfflineTileProvider(String offlineMapName){
        this.offlineMapName = offlineMapName;
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {
        byte[] image = readTileImage(x, y, zoom);
        return image == null ? null : new Tile(TILE_WIDTH, TILE_HEIGHT, image);
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

    private byte[] readTileImage(int x, int y, int zoom) {
        FileInputStream in = null;
        ByteArrayOutputStream buffer = null;

//        try { in = new FileInputStream(getTileFile(x, y, zoom));
        try { in = new FileInputStream(GoogleMapOfflineFileController.getTileFile(offlineMapName,x,y,zoom));
            buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[BUFFER_SIZE];

            while ((nRead = in .read(data, 0, BUFFER_SIZE)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        } finally {
            if ( in != null)
                try { in .close();
                } catch (Exception ignored) {}
            if (buffer != null)
                try {
                    buffer.close();
                } catch (Exception ignored) {}
        }
    }

//    private File getTileFile(int x, int y, int zoom) {
//        File sdcard = Environment.getExternalStorageDirectory();
//        String tileFile = "/TILES_FOLDER/" + zoom + '/' + x + '/' + y + ".png";
//        File file = new File(sdcard, tileFile);
//        return file;
//    }
}
