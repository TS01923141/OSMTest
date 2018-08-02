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
 * Created by biji on 2018/8/2.
 */

public class OfflineTileProvider implements TileProvider {

    private static final String TAG = "GoogleMapOfflineTilePro";

//    private static final String UPPER_ZOOM_TILE_URL = "http://mt0.google.com/vt/lyrs=m&hl=ru&x=%d&y=%d&z=%d&scale=1&s=Galileo";

    private static final int TILE_WIDTH = 256;
    private static final int TILE_HEIGHT = 256;
    private static final int BUFFER_SIZE = 16 * 1024;
    private String offlineMapName;
    private int mapType;

    public OfflineTileProvider(String offlineMapName, int mapType){
        this.offlineMapName = offlineMapName;
        this.mapType = mapType;
    }

    @Override
    public Tile getTile(int x, int y, int zoom) {
        byte[] image = readTileImage(x, y, zoom);
        return image == null ? null : new Tile(TILE_WIDTH, TILE_HEIGHT, image);
    }

    private byte[] readTileImage(int x, int y, int zoom) {
        FileInputStream in = null;
        ByteArrayOutputStream buffer = null;

//        try { in = new FileInputStream(getTileFile(x, y, zoom));
        try { in = new FileInputStream(GoogleMapOfflineFileController.getTileFile(offlineMapName,x,y,zoom, mapType));
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
