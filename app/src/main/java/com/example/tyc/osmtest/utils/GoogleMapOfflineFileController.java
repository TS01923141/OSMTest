package com.example.tyc.osmtest.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.example.tyc.osmtest.data.OfflineMapData;
import com.example.tyc.osmtest.data.TileRegionEntity;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

import static com.example.tyc.osmtest.MapsActivity.GOOGLE_MAP;
import static com.example.tyc.osmtest.MapsActivity.OPEN_STREET_MAP;
import static com.example.tyc.osmtest.MapsActivity.RUDY_MAP;
import static com.example.tyc.osmtest.MapsActivity.WMTS_MAP;

/**
 * Created by biji on 2018/7/25.
 */

public class GoogleMapOfflineFileController {
    public static final String GOOGLE_UPPER_ZOOM_TILE_URL = "http://mt0.google.com/vt/lyrs=m&hl=ru&x=%d&y=%d&z=%d&scale=1&s=Galileo";
    public static final String OSM_UPPER_ZOOM_TILE_URL = "https://tile.openstreetmap.org/%d/%d/%d.png";
    public static final String RUDY_UPPER_ZOOM_TILE_URL = "http://rudy.tile.basecamp.tw/%d/%d/%d.png";
    public static final String WMTS_UPPER_ZOOM_TILE_URL = "http://gis.sinica.edu.tw/tileserver/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=TM25K_2001&TILEMATRIXSET=GoogleMapsCompatible&TILEMATRIX=%d&TILEROW=%d&TILECOL=%d&FORMAT=image/jpeg";
    private static final String TILE_DIRECTORY_PATH = "/HIKING_BIJI/TILES_FOLDER/";
    private static final String GOOGLE_MAP_TILE_FILE_DIRECTORY_PATH = "/HIKING_BIJI/TILES_FOLDER/GOOGLE_MAP/";
    public static final String OSM_MAP_TILE_FILE_DIRECTORY_PATH = "/HIKING_BIJI/TILES_FOLDER/OSM/";
    public static final String RUDY_MAP_TILE_FILE_DIRECTORY_PATH = "/HIKING_BIJI/TILES_FOLDER/RUDY/";
    public static final String WMTS_MAP_TILE_FILE_DIRECTORY_PATH = "/HIKING_BIJI/TILES_FOLDER/WMTS/";
    private static final String DIRECTORY_PATH = "/HIKING_BIJI/";
    public static final String TEMP_TILE_PATH = DIRECTORY_PATH + "TEMP/";
    public static final String TEMP_TILE_FILE_NAME = "TEMP_TILE.jpg";
    private static final String TAG = "GoogleMapOfflineFileCon";
//    private static final int GOOGLE_MAP = 0;
//    private static final int OPEN_STREET_MAP = 1;

    public static void saveTileRegionFile(final String offlineMapName, List<TileRegionEntity> tileRegionList, int mapType) {

        for (TileRegionEntity tileRegionEntity : tileRegionList) {
            for (int x = tileRegionEntity.getMinX(); x <= tileRegionEntity.getMaxX(); x++) {
                for (int y = tileRegionEntity.getMinY(); y <= tileRegionEntity.getMaxY(); y++) {
                    saveTileFile(offlineMapName, x, y, tileRegionEntity.getZoom(), mapType);
                }
            }

        }
//        Flowable.just(tileRegionList)
//                .flatMap(new Function<List<TileRegionEntity>, Publisher<?>>() {
//                    @Override
//                    public Publisher<?> apply(List<TileRegionEntity> tileRegionEntities) throws Exception {
//                        return Flowable.fromIterable(tileRegionEntities);
//                    }
//                })
//                .subscribe(new Consumer<TileRegionEntity>() {
//                    @Override
//                    public void accept(TileRegionEntity tileRegionEntity) throws Exception {
//                        for (int x = tileRegionEntity.getMinX(); x <= tileRegionEntity.getMaxX(); x++){
//                            for (int y = tileRegionEntity.getMinY(); y <= tileRegionEntity.getMaxY() ; y++){
//                                saveTileFile(offlineMapName, x, y , tileRegionEntity.getZoom());
//                            }
//                        }
//                    }
//                });
    }

    public static void saveTileRegionFile(Context context, final String offlineMapName, final List<TileRegionEntity> tileRegionList, int mapType) {
        int progress = 0;
        int sum = 0;
        for (TileRegionEntity tileRegionEntity : tileRegionList) {
            int x = tileRegionEntity.getMaxX() - tileRegionEntity.getMinX() + 1;
            int y = tileRegionEntity.getMaxY() - tileRegionEntity.getMinY() + 1;
//            Log.d(TAG, "saveTileRegionFile: x: " + x);
//            Log.d(TAG, "saveTileRegionFile: y: " + y);
//            Log.d(TAG, "saveTileRegionFile: x * y: " + x * y);
            sum = sum + (x * y);
        }

        Log.d(TAG, "saveTileRegionFile: showDialog");
//        final android.app.AlertDialog alertDialog = AlertProgressDialog.show(context, "請稍等", "下載中...", false);
        AlertProgressDialog alertProgressDialog = new AlertProgressDialog();
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                alertProgressDialog.showWithProgress(context, "請稍等", "下載中...", false);
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
        final int finalSum = sum;

//        Flowable.interval(1, TimeUnit.SECONDS)
//                //丟棄來不及處理的資料
//                .onBackpressureDrop()
//                .takeWhile(tick -> !alertProgressDialog.isShowing())
//                .doOnComplete(new Action() {
//                    @Override
//                    public void run() throws Exception {
        downloadTileFile(offlineMapName, tileRegionList, alertProgressDialog, finalSum, mapType);
//                        Completable.fromAction(new Action() {
//                            @Override
//                            public void run() throws Exception {
//                                startDownloadTileFile(offlineMapName, tileRegionList, alertDialog, finalSum);
//                            }
//                        }).subscribeOn(Schedulers.newThread()).subscribe();

//                    }
//                })
//                .subscribe();

//        for (TileRegionEntity tileRegionEntity : tileRegionList) {
//            Log.d(TAG, "saveTileRegionFile: progressing");
//            for (int x = tileRegionEntity.getMinX(); x <= tileRegionEntity.getMaxX(); x++) {
////                alertDialog.setMessage("下載中...( " + progress + " / " + sum + " )");
////                Log.d(TAG, "saveTileRegionFile: " + "下載中...( " + progress + " / " + sum + " )");
//                for (int y = tileRegionEntity.getMinY(); y <= tileRegionEntity.getMaxY(); y++) {
//                    saveTileFileProgress(offlineMapName, tileRegionEntity, x, y, alertDialog, progress, sum).subscribe();
//                        if (progress % (sum /10) == 0) {
//                            alertDialog.setMessage("下載中...( " + progress + " / " + sum + " )");
//                            Log.d(TAG, "run: " + "下載中...( " + progress + " / " + sum + " )");
//                        }
//                    progress++;
////                    saveTileFile(offlineMapName, x, y, tileRegionEntity.getZoom());
////                    progress++;
////                    alertDialog.setMessage("下載中...( " + progress + " / " + sum + " )");
//                }
//            }
//        }

//        Log.d(TAG, "saveTileRegionFile: dismissDialog");
//        alertDialog.dismiss();
    }

    private static void downloadTileFile(final String offlineMapName, final List<TileRegionEntity> tileRegionList, AlertProgressDialog alertDialog, int sum, int mapType) {
        int progress = 0;
        for (TileRegionEntity tileRegionEntity : tileRegionList) {
//            Log.d(TAG, "saveTileRegionFile: progressing");
            for (int x = tileRegionEntity.getMinX(); x <= tileRegionEntity.getMaxX(); x++) {
                for (int y = tileRegionEntity.getMinY(); y <= tileRegionEntity.getMaxY(); y++) {
//                    Log.d(TAG, "downloadTileFile: x: " + x);
//                    Log.d(TAG, "downloadTileFile: y: " + y);
//                    Log.d(TAG, "downloadTileFile: progress: " + progress);
//                    saveTileFileProgress(offlineMapName, tileRegionEntity, x, y, alertDialog, progress, sum).subscribe();
                    saveTileFile(offlineMapName, x, y, tileRegionEntity.getZoom(), mapType);
//                    if (progress % (sum / 10) == 0) {
                    int finalProgress = progress;
                    Completable.fromAction(() -> {
                        alertDialog.setMessage("下載中...( " + finalProgress + " / " + sum + " )");
                        Log.d(TAG, "run: " + "下載中...( " + finalProgress + " / " + sum + " )");
                    }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
//                    }
                    progress++;
                    if (progress == sum) {
                        Completable.fromAction(() -> {
//                            Log.d(TAG, "saveTileRegionFile: dismissDialog");
                            alertDialog.dismiss();
                        }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
                    }
                }
            }
        }
    }

////    private static void prepareDownTileFile(final String offlineMapName, final List<TileRegionEntity> tileRegionList, AlertDialog alertDialog, int sum){
////        int progress = 0;
////        for (TileRegionEntity tileRegionEntity : tileRegionList) {
////            List<Integer> xList = new ArrayList<>();
////            List<Integer> yList = new ArrayList<>();
////            for (int x = tileRegionEntity.getMinX(); x <= tileRegionEntity.getMaxX(); x++) {
////                xList.add(x);
////            }
////            for (int y = tileRegionEntity.getMinY(); y <= tileRegionEntity.getMaxY(); y++) {
////                yList.add(y);
////            }
////            progress = startDownloadTileFile(offlineMapName, xList, yList, tileRegionEntity.getZoom(), alertDialog, progress , sum);
////        }
////    }
//
////    private static int startDownloadTileFile(String offlineMapName, List<Integer> xList, List<Integer> yList, int zoom, final AlertDialog progressDialog, int progress, int sum){
////        int i = 0;
////        int j = 0;
//////        while (downloadTileFileProgress(offlineMapName, zoom, xList.get(i), yList.get(j), progressDialog, progress, sum) == 1){
//////            if (j < yList.size() -1){
//////                j++;
//////            }else if (i < xList.size() -1){
//////                i++;
//////            }
//////        }
////        while (i < xList.size() && j < yList.size()){
////            if (j < yList.size() - 1) {
////                j = j + downloadTileFileProgress(offlineMapName, zoom, xList.get(i), yList.get(j), progressDialog, progress + ((i + 1) * (j + 1)) + j + 1, sum);
////            }else if (i < xList.size() - 1){
////                j = 0;
////                i = i + downloadTileFileProgress(offlineMapName, zoom, xList.get(i), yList.get(j), progressDialog, progress + ((i + 1) * (j + 1)) + j + 1, sum);
////            }
////        }
////        return progress + ((i + 1) * (j + 1)) + j + 1;
////    }
//
//    private static void startDownloadTileFile(final String offlineMapName, final List<TileRegionEntity> tileRegionList, AlertDialog progressDialog, int sum){
//        int progress = 0;
//        int k = 0;
//        int i = 0;
//        int j = 0;
//        while (k < tileRegionList.size()) {
//            List<Integer> xList = new ArrayList<>();
//            List<Integer> yList = new ArrayList<>();
//            for (int x = tileRegionList.get(k).getMinX(); x <= tileRegionList.get(k).getMaxX(); x++) {
//                xList.add(x);
//            }
//            for (int y = tileRegionList.get(k).getMinY(); y <= tileRegionList.get(k).getMaxY(); y++) {
//                yList.add(y);
//            }
//            while (i < xList.size() && j < yList.size()) {
//                if (j < yList.size() - 1) {
//                    j = j + downloadTileFileProgress(offlineMapName, tileRegionList.get(k).getZoom(), xList.get(i), yList.get(j), progressDialog, progress + ((i + 1) * (j + 1)) + j + 1, sum);
//                } else if (i < xList.size() - 1) {
//                    j = 0;
//                    i = i + downloadTileFileProgress(offlineMapName, tileRegionList.get(k).getZoom(), xList.get(i), yList.get(j), progressDialog, progress + ((i + 1) * (j + 1)) + j + 1, sum);
//                }
//            }
//            k++;
//        }
//    }

//    private static int downloadTileFileProgress(final String offlineMapName, int zoom, final int x, final int y, final AlertDialog progressDialog, final int progress, final int sum){
////        if (progress == (sum / 10) || progress == 0 || progress == 1){
//            Completable.fromAction(new Action() {
//                @Override
//                public void run() throws Exception {
//                    progressDialog.setMessage("下載中...( " + progress + " / " + sum + " )");
//                    Log.d(TAG, "run: " + "下載中...( " + progress + " / " + sum + " )");
//                }
//            }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
////        }
//        return saveTileFile(offlineMapName, x, y, zoom);
//    }

    private static Completable saveTileFileProgress(final String offlineMapName, final TileRegionEntity tileRegionEntity, final int x, final int y, final AlertProgressDialog progressDialog, final int progress, final int sum, int mapType) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                saveTileFile(offlineMapName, x, y, tileRegionEntity.getZoom(), mapType);
            }
        }).subscribeOn(Schedulers.newThread())
                ;
//                .observeOn(AndroidSchedulers.mainThread())
//                .andThen(Completable.fromAction(new Action() {
//                    @Override
//                    public void run() throws Exception {
//                        if (progress % (sum /10) == 0) {
//                            progressDialog.setMessage("下載中...( " + progress + " / " + sum + " )");
//                            Log.d(TAG, "run: " + "下載中...( " + progress + " / " + sum + " )");
//                        }
//                    }
//                }));
    }

//    public static int saveTileFile(String offlineMapName, int x, int y, int zoom) {
//        Log.d(TAG, "saveTileFile: x: " + x + " , y: " + y + " , zoom: " + zoom);
//        File directory = Environment.getExternalStorageDirectory();
////       String tileFilePath = GOOGLE_MAP_TILE_FILE_DIRECTORY_PATH + offlineMapName + '/' + zoom + '/' + x + '/' + y + ".jpg";
//        String tileFileDirectoryPath = GOOGLE_MAP_TILE_FILE_DIRECTORY_PATH + offlineMapName + '/' + zoom + '/' + x + '/';
//        File tileFileDirectory = new File(directory, tileFileDirectoryPath);
//        if (!tileFileDirectory.exists()) {
//            tileFileDirectory.mkdirs();
//        }
////        String tileFilePath = tileFileDirectoryPath + y + ".jpg";
//        File tileFile = new File(tileFileDirectory, y + ".jpg");
//        if (!tileFile.exists()) {
//            tileFile.getParentFile().mkdirs();
//        }
//        FileOutputStream fileos = null;
//        try {
//            fileos = new FileOutputStream(tileFile);
//            getBitmapFromURL(x, y, zoom).compress(Bitmap.CompressFormat.JPEG, 100, fileos);
//        } catch (FileNotFoundException e) {
//            Log.e(TAG, "createTileFile: ", e);
//            return 0;
//        } finally {
//            try {
//                if (!Objects.equals(fileos, null)) {
//                    fileos.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return 1;
////       return titleFile;
//    }

    public static void saveTileFile(String offlineMapName, int x, int y, int zoom, int mapType) {
        Log.d(TAG, "saveTileFile: x: " + x + " , y: " + y + " , zoom: " + zoom);
        File directory = Environment.getExternalStorageDirectory();
//       String tileFilePath = GOOGLE_MAP_TILE_FILE_DIRECTORY_PATH + offlineMapName + '/' + zoom + '/' + x + '/' + y + ".jpg";
        String tileFileDirectoryPath;
        if (mapType == GOOGLE_MAP) {
            tileFileDirectoryPath = GOOGLE_MAP_TILE_FILE_DIRECTORY_PATH + offlineMapName + '/' + zoom + '/' + x + '/';
        } else if (mapType == OPEN_STREET_MAP) {
            tileFileDirectoryPath = OSM_MAP_TILE_FILE_DIRECTORY_PATH + offlineMapName + '/' + zoom + '/' + x + '/';
        } else if (mapType == RUDY_MAP) {
            tileFileDirectoryPath = RUDY_MAP_TILE_FILE_DIRECTORY_PATH + offlineMapName + '/' + zoom + '/' + x + '/';
        } else if (mapType == WMTS_MAP) {
            tileFileDirectoryPath = WMTS_MAP_TILE_FILE_DIRECTORY_PATH + offlineMapName + '/' + zoom + '/' + x + '/';
        } else {
            tileFileDirectoryPath = GOOGLE_MAP_TILE_FILE_DIRECTORY_PATH + offlineMapName + '/' + zoom + '/' + x + '/';
        }
        File tileFileDirectory = new File(directory, tileFileDirectoryPath);
        if (!tileFileDirectory.exists()) {
            tileFileDirectory.mkdirs();
        }
//        String tileFilePath = tileFileDirectoryPath + y + ".jpg";
        File tileFile = new File(tileFileDirectory, y + ".jpg");
        if (!tileFile.exists()) {
            tileFile.getParentFile().mkdirs();
        }
        FileOutputStream fileos = null;
        try {
            fileos = new FileOutputStream(tileFile);
            getBitmapFromURL(x, y, zoom, mapType).compress(Bitmap.CompressFormat.JPEG, 100, fileos);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "createTileFile: ", e);
        } finally {
            try {
                if (!Objects.equals(fileos, null)) {
                    fileos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//       return titleFile;
    }

//    public static void saveTileFile(String directoryPath, String offlineMapName, int x, int y, int zoom) {
//        Log.d(TAG, "saveTileFile: x: " + x + " , y: " + y + " , zoom: " + zoom);
//        File directory = Environment.getExternalStorageDirectory();
////       String tileFilePath = GOOGLE_MAP_TILE_FILE_DIRECTORY_PATH + offlineMapName + '/' + zoom + '/' + x + '/' + y + ".jpg";
//        String tileFileDirectoryPath = directoryPath + offlineMapName + '/' + zoom + '/' + x + '/';
//        File tileFileDirectory = new File(directory, tileFileDirectoryPath);
//        if (!tileFileDirectory.exists()) {
//            tileFileDirectory.mkdirs();
//        }
////        String tileFilePath = tileFileDirectoryPath + y + ".jpg";
//        File tileFile = new File(tileFileDirectory, y + ".jpg");
//        if (!tileFile.exists()) {
//            tileFile.getParentFile().mkdirs();
//        }
//        FileOutputStream fileos = null;
//        try {
//            fileos = new FileOutputStream(tileFile);
//            getBitmapFromURL(x, y, zoom).compress(Bitmap.CompressFormat.JPEG, 100, fileos);
//        } catch (FileNotFoundException e) {
//            Log.e(TAG, "createTileFile: ", e);
//        } finally {
//            try {
//                if (!Objects.equals(fileos, null)) {
//                    fileos.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
////       return titleFile;
//    }

    public static Completable downloadTempTileFile(int x, int y, int zoom, int mapType) {
        return Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
//                saveTileFile(DIRECTORY_PATH + "TEMP/" , "tempTile", x, y , zoom);
                File directory = Environment.getExternalStorageDirectory();
                File tileFileDirectory = new File(directory, TEMP_TILE_PATH);
                if (!tileFileDirectory.exists()) {
                    tileFileDirectory.mkdirs();
                }

                File tileFile = new File(tileFileDirectory, TEMP_TILE_FILE_NAME);
                Log.d(TAG, "run: filePath: " + tileFile.getAbsolutePath());
                if (!tileFile.exists()) {
                    tileFile.getParentFile().mkdirs();
                }
                FileOutputStream fileos = null;
                try {
                    fileos = new FileOutputStream(tileFile);
                    getBitmapFromURL(x, y, zoom, mapType).compress(Bitmap.CompressFormat.JPEG, 100, fileos);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "createTileFile: ", e);
                } finally {
                    try {
                        if (!Objects.equals(fileos, null)) {
                            fileos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread());
    }

    public static void deleteTileFile(String offlineMapName, int x, int y, int zoom) {
        File directory = Environment.getExternalStorageDirectory();
        String tileFilePath = GOOGLE_MAP_TILE_FILE_DIRECTORY_PATH + offlineMapName + '/' + zoom + '/' + x + '/' + y + ".jpg";
        File tileFile = new File(directory, tileFilePath);
        if (tileFile.exists()) {
            tileFile.delete();
        }
    }

    public static File getTileFile(String offlineMapName, int x, int y, int zoom) {
        File directory = Environment.getExternalStorageDirectory();
        String tileFilePath = GOOGLE_MAP_TILE_FILE_DIRECTORY_PATH + offlineMapName + '/' + zoom + '/' + x + '/' + y + ".jpg";
        File tileFile = new File(directory, tileFilePath);
        return Objects.equals(tileFile, null) ? null : tileFile;
    }

    public static File getTileFile(String offlineMapName, int x, int y, int zoom, int mapType) {
        File directory = Environment.getExternalStorageDirectory();
        String tileFilePath;
        switch (mapType) {
            case GOOGLE_MAP:
                tileFilePath = GOOGLE_MAP_TILE_FILE_DIRECTORY_PATH + offlineMapName + '/' + zoom + '/' + x + '/' + y + ".jpg";
                break;
            case OPEN_STREET_MAP:
                tileFilePath = OSM_MAP_TILE_FILE_DIRECTORY_PATH + offlineMapName + '/' + zoom + '/' + x + '/' + y + ".jpg";
                break;
            case RUDY_MAP:
                tileFilePath = RUDY_MAP_TILE_FILE_DIRECTORY_PATH + offlineMapName + '/' + zoom + '/' + x + '/' + y + ".jpg";
                break;
            case WMTS_MAP:
                tileFilePath = WMTS_MAP_TILE_FILE_DIRECTORY_PATH + offlineMapName + '/' + zoom + '/' + x + '/' + y + ".jpg";
                break;
            default:
                tileFilePath = GOOGLE_MAP_TILE_FILE_DIRECTORY_PATH + offlineMapName + '/' + zoom + '/' + x + '/' + y + ".jpg";
                break;
        }
        File tileFile = new File(directory, tileFilePath);
        Log.d(TAG, "getTileFile: tileFile.path: " + tileFile.getAbsolutePath());
        return Objects.equals(tileFile, null) ? null : tileFile;
    }

    public static File getFile(String filePath) {
        Log.d(TAG, "getFile: filePath: " + filePath);
        File directory = Environment.getExternalStorageDirectory();
        File tileFile = new File(directory, filePath);
        return Objects.equals(tileFile, null) ? null : tileFile;
    }

    public static Bitmap getBitmapFromURL(int x, int y, int zoom, int mapType) {
        URL url;
        try {
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
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            return BitmapFactory.decodeStream(connection.getInputStream());
        } catch (IOException e) {
            Log.d(TAG, "exception when retrieving bitmap from internet" + e.toString());
            return null;
        }
    }

//    public static int convertLngToX(double MAP_WIDTH ,double lng){
//        return (int) ((MAP_WIDTH/360.0) * (180 + lng));
//    }
//
//    public static double getX(LatLng latLng, float zoom){
//        double t = Math.pow(2, zoom);
//        double s = 256 / t;
////        double p = this.fromLatLngToPoint(latLng);
//        Point point = fromLatLngToPoint(latLng.latitude, latLng.longitude);
////        double x = 128 + latLng.longitude * (256 / 360);
////        return Math.floor(x / s);
//        return Math.floor(point.x / s);
//
//
////        getTileAtLatLng:function(latLng,zoom){
////            var t=Math.pow(2,zoom),
////                    s=256/t,
////                    p=this.fromLatLngToPoint(latLng);
////            return {x:Math.floor(p.x/s),y:Math.floor(p.y/s),z:zoom};
////        }
//    }
//
////    public static Tile getTile(Point point,float zoom){
////        double t = Math.pow(2, zoom);
////        double s = 256 / t;
////        return new Tile(Math.floor(point.x / s), Math.floor(point.y / s), zoom, 256);
////    }
//
//    public static double getY(LatLng latLng, float zoom){
//        double t = Math.pow(2, zoom);
//        double s = 256 / t;
////        double p = this.fromLatLngToPoint(latLng);
//        Point point = fromLatLngToPoint(latLng.latitude, latLng.longitude);
//        return Math.floor(point.y / s);
//
//
////        getTileAtLatLng:function(latLng,zoom){
////            var t=Math.pow(2,zoom),
////                    s=256/t,
////                    p=this.fromLatLngToPoint(latLng);
////            return {x:Math.floor(p.x/s),y:Math.floor(p.y/s),z:zoom};
////        }
//    }
//
//    public static Point fromLatLngToPoint(double lat, double lng){
//        Log.d(TAG, "fromLatLngToPoint: lat: " + lat);
//        Log.d(TAG, "fromLatLngToPoint: lng: " + lng);
//        double sinY = Math.min(Math.max(Math.sin(lat * (Math.PI / 180)),
//                -.9999),
//                .9999);
//        return new Point(128 + lng * (256/360),
//                            128 + 0.5 * Math.log((1 + sinY) / (1 - sinY)) * -(256 / (2 * Math.PI)));
//
////        fromLatLngToPoint:function(latLng){
////            var siny =  Math.min(Math.max(Math.sin(latLng.lat* (Math.PI / 180)),
////                    -.9999),
////                    .9999);
////            return {
////                    x: 128 + latLng.lng * (256/360),
////                    y: 128 + 0.5 * Math.log((1 + siny) / (1 - siny)) * -(256 / (2 * Math.PI))
////     };
////        },
//    }
//
//    public static int convertLatToY(double MAP_HEIGHT, double lat) {
//        return (int) ((MAP_HEIGHT / 180.0) * (90 - lat));
//    }

    public static String getTileNumber(final double lat, final double lon, final int zoom) {
        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int ytile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
        if (xtile < 0)
            xtile = 0;
        if (xtile >= (1 << zoom))
            xtile = ((1 << zoom) - 1);
        if (ytile < 0)
            ytile = 0;
        if (ytile >= (1 << zoom))
            ytile = ((1 << zoom) - 1);
        return ("" + zoom + "/" + xtile + "/" + ytile);
    }

    public static int getTileX(final double lon, final int zoom) {
        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        if (xtile < 0)
            xtile = 0;
        if (xtile >= (1 << zoom))
            xtile = ((1 << zoom) - 1);
        return xtile;
    }

    public static int getTileY(final double lat, final int zoom) {
        int ytile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
        if (ytile < 0)
            ytile = 0;
        if (ytile >= (1 << zoom))
            ytile = ((1 << zoom) - 1);
        return ytile;
    }

    public static List<TileRegionEntity> getTileRegion(List<LatLng> locationList, List<Integer> zoomList) {
        ArrayList<TileRegionEntity> tileRegionList = new ArrayList<>();
        ArrayList<Double> latList = new ArrayList<>();
        ArrayList<Double> lngList = new ArrayList<>();
        for (int i = 0; i < locationList.size(); i++) {
            latList.add(locationList.get(i).latitude);
            lngList.add(locationList.get(i).longitude);
        }
        Collections.sort(latList);
        Collections.sort(lngList);

        for (int zoom : zoomList) {
            TileRegionEntity tileRegionEntity = new TileRegionEntity();
            tileRegionEntity.setMinX(getTileX(lngList.get(0), zoom));
            tileRegionEntity.setMinY(getTileY(latList.get(latList.size() - 1), zoom));
            tileRegionEntity.setMaxX(getTileX(lngList.get(lngList.size() - 1), zoom));
            tileRegionEntity.setMaxY(getTileY(latList.get(0), zoom));
            tileRegionEntity.setZoom(zoom);
            tileRegionList.add(tileRegionEntity);
        }
        return tileRegionList;
    }

    public static List<TileRegionEntity> getTileRegion(List<LatLng> locationList, boolean[] zoomList) {
        ArrayList<TileRegionEntity> tileRegionList = new ArrayList<>();
        ArrayList<Double> latList = new ArrayList<>();
        ArrayList<Double> lngList = new ArrayList<>();
        for (int i = 0; i < locationList.size(); i++) {
            latList.add(locationList.get(i).latitude);
            lngList.add(locationList.get(i).longitude);
        }
        Collections.sort(latList);
        Collections.sort(lngList);

        for (int i = 0; i < zoomList.length; i++) {
            TileRegionEntity tileRegionEntity = new TileRegionEntity();
            tileRegionEntity.setMinX(getTileX(lngList.get(0), i));
            tileRegionEntity.setMinY(getTileY(latList.get(latList.size() - 1), i));
            tileRegionEntity.setMaxX(getTileX(lngList.get(lngList.size() - 1), i));
            tileRegionEntity.setMaxY(getTileY(latList.get(0), i));
            tileRegionEntity.setZoom(i);
            tileRegionList.add(tileRegionEntity);
        }
        return tileRegionList;
    }

    public static List<OfflineMapData> getOfflineMapData() {
        List<OfflineMapData> offlineMapDataList = new ArrayList<>();
        File directory = Environment.getExternalStorageDirectory();
        String tileFileDirectoryPath = TILE_DIRECTORY_PATH;
        File tileFileDirectory = new File(directory, tileFileDirectoryPath);
        //TileMap主資料夾,下面的是GoogleMap, OSM之類的離線地圖類別資料夾
        //ex.TILE_MAP/GOOGLE_MAP
        File[] mapTypeFiles = tileFileDirectory.listFiles();
        if (!Objects.equals(mapTypeFiles, null)) {
            for (int j = 0; j < mapTypeFiles.length; j++) {
                //類別資料夾下的各個以下載的離線地圖資料夾
                //ex.TILE_MAP/GOOGLE_MAP/TEST_TILE
                File tileFilePath = new File(mapTypeFiles[j].getAbsoluteFile().toString());
                File[] tileFiles = tileFilePath.listFiles();
                if (!Objects.equals(tileFiles, null)) {
                    for (int i = 0; i < tileFiles.length; i++) {
                        OfflineMapData offlineMapData = new OfflineMapData();
                        offlineMapData.setName(tileFiles[i].getName());
                        offlineMapData.setFilePath(tileFiles[i].getAbsolutePath());
                        long directorySize = dirSize(tileFiles[i]);
                        if (directorySize >= (1024 * 1024)) {
                            Log.d(TAG, "getOfflineMapData: mb");
                            offlineMapData.setFileSize(String.valueOf(directorySize / 1024 / 1024) + " Mb");
                        } else if (directorySize >= 1024) {
                            Log.d(TAG, "getOfflineMapData: kb");
                            offlineMapData.setFileSize(String.valueOf(directorySize / 1024) + " Kb");
                        } else {
                            offlineMapData.setFileSize(String.valueOf(directorySize) + " b");
                        }
//                        offlineMapData.setMapType("GoogleMap");
                        switch (mapTypeFiles[j].getName()) {
                            case "GOOGLE_MAP":
                                offlineMapData.setMapType(GOOGLE_MAP);
                                break;
                            case "OSM":
                                offlineMapData.setMapType(OPEN_STREET_MAP);
                                break;
                            case "RUDY":
                                offlineMapData.setMapType(RUDY_MAP);
                                break;
                            case "WMTS":
                                offlineMapData.setMapType(WMTS_MAP);
                                break;
                        }
//                        offlineMapData.setMapType(mapTypeFiles[j].getName());
                        offlineMapDataList.add(offlineMapData);
                    }
                }
            }
        }
        return offlineMapDataList;
    }

    public static long dirSize(File dir) {
        if (dir.exists()) {
            long result = 0;
            File[] fileList = dir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // Recursive call if it's a directory
                if (fileList[i].isDirectory()) {
                    result += dirSize(fileList[i]);
                } else {
                    // Sum the file size in bytes
                    result += fileList[i].length();
                }
            }
            return result; // return the file size
        }
        return 0;
    }

    public static boolean deleteDir(File dir) {
        if (dir.exists()) {
            File[] fileList = dir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // Recursive call if it's a directory
                if (fileList[i].isDirectory()) {
                    deleteDir(fileList[i]);
                } else {
                    fileList[i].delete();
                }
            }
            dir.delete();
            return true;
        }
        return false;
    }

    public static int estimateTileCount(final List<TileRegionEntity> tileRegionList, boolean[] zooms) {
        int sum = 0;
//        for (TileRegionEntity tileRegionEntity : tileRegionList) {
//            int x = tileRegionEntity.getMaxX() - tileRegionEntity.getMinX() + 1;
//            int y = tileRegionEntity.getMaxY() - tileRegionEntity.getMinY() + 1;
//            sum = sum + (x * y);
//        }
        Log.d(TAG, "estimateTileCount: tileRegionList.size(): " + tileRegionList.size());
        for (int i = 0; i < zooms.length; i++) {
            if (zooms[i]) {
                int x = tileRegionList.get(i).getMaxX() - tileRegionList.get(i).getMinX() + 1;
                int y = tileRegionList.get(i).getMaxY() - tileRegionList.get(i).getMinY() + 1;
                Log.d(TAG, "estimateTileCount: (x * y): " + (x * y));
                sum = sum + (x * y);
            }
        }
        Log.d(TAG, "estimateTileCount: sum: " + sum);
        return sum;
    }

    public static String getFormatByte(int fileCount, long fileSize) {
        Log.d(TAG, "getFormatByte: fileCount: " + fileCount);
        Log.d(TAG, "getFormatByte: fileSize: " + fileSize);
        long sum = fileCount * fileSize;
        if (sum >= (1024 * 1024)) {
            return (sum / 1024 / 1024) + " Mb";
        } else if (sum >= 1024) {
            return (sum / 1024) + "Kb";
        } else {
            return sum + " b";
        }
    }
}
