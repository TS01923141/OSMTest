package com.example.tyc.osmtest.utils;

import android.os.Environment;
import android.util.Log;

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.MapTileIndex;

import static com.example.tyc.osmtest.utils.GoogleMapOfflineFileController.OSM_MAP_TILE_FILE_DIRECTORY_PATH;

/**
 * Created by biji on 2018/7/31.
 */

public class OfflineMapTileSource extends OnlineTileSourceBase {
    private static final String TAG = "OfflineMapTileSource";
    private String direcotry;
    private static final String OSM_MAP_PATH = OSM_MAP_TILE_FILE_DIRECTORY_PATH +"%s/%d/%d/%d.jpg";
    private String fileName;

    /**
     *
     * @param aName this is used for caching purposes, make sure it is consistent and unique
     * @param aZoomMinLevel
     * @param aZoomMaxLevel
     * @param aTileSizePixels
     * @param aImageFilenameEnding
     * @param aBaseUrl
     */
    public OfflineMapTileSource(final String aName, final int aZoomMinLevel,
                               final int aZoomMaxLevel, final int aTileSizePixels, final String aImageFilenameEnding,
                               final String[] aBaseUrl) {
        this(aName, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels,
                aImageFilenameEnding, aBaseUrl,null);
        fileName = aName;
        direcotry = Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public OfflineMapTileSource(final String aName, final int aZoomMinLevel,
                               final int aZoomMaxLevel, final int aTileSizePixels, final String aImageFilenameEnding,
                               final String[] aBaseUrl, final String copyright) {
        super(aName, aZoomMinLevel, aZoomMaxLevel, aTileSizePixels,
                aImageFilenameEnding, aBaseUrl,copyright);
        fileName = aName;
        direcotry = Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    @Override
    public String toString(){
        return name();
    }
    @Override
    public String getTileURLString(final long pMapTileIndex) {
        Log.d(TAG, "getTileURLString: path: " + String.format(OSM_MAP_PATH, fileName, MapTileIndex.getZoom(pMapTileIndex) , MapTileIndex.getX(pMapTileIndex), MapTileIndex.getY(pMapTileIndex)));
        return direcotry + "/" + String.format(OSM_MAP_PATH, fileName, MapTileIndex.getZoom(pMapTileIndex) , MapTileIndex.getX(pMapTileIndex), MapTileIndex.getY(pMapTileIndex));
//        return getBaseUrl() + MapTileIndex.getZoom(pMapTileIndex) + "/" + MapTileIndex.getX(pMapTileIndex) + "/" + MapTileIndex.getY(pMapTileIndex)
//                + mImageFilenameEnding;
    }
}