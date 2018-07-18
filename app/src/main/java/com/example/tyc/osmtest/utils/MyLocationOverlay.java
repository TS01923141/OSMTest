package com.example.tyc.osmtest.utils;

/**
 * Created by biji on 2018/7/13.
 */

import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.overlay.Circle;
import org.mapsforge.map.layer.overlay.Marker;

/**
 * A {@link Layer} implementation to display the current location.
 * Needs to be added before requesting location updates, otherwise no DisplayModel is set.
 */
public class MyLocationOverlay extends Layer {

    private final Circle circle;
    private final Marker marker;

    /**
     * Constructs a new {@code MyLocationOverlay} without an accuracy circle.
     *
     * @param marker a marker to display at the current location
     */
    public MyLocationOverlay(Marker marker) {
        this(marker, null);
    }

    /**
     * Constructs a new {@code MyLocationOverlay} with an accuracy circle.
     *
     * @param marker a marker to display at the current location
     * @param circle a circle to show the location accuracy (can be null)
     */
    public MyLocationOverlay(Marker marker, Circle circle) {
        super();
        this.marker = marker;
        this.circle = circle;
    }

    @Override
    public synchronized void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point topLeftPoint) {
        if (this.circle != null) {
            this.circle.draw(boundingBox, zoomLevel, canvas, topLeftPoint);
        }
        this.marker.draw(boundingBox, zoomLevel, canvas, topLeftPoint);
    }

    @Override
    protected void onAdd() {
        if (this.circle != null) {
            this.circle.setDisplayModel(this.displayModel);
        }
        this.marker.setDisplayModel(this.displayModel);
    }

    @Override
    public void onDestroy() {
        this.marker.onDestroy();
    }

    public void setPosition(double latitude, double longitude, float accuracy) {
        synchronized (this) {
            LatLong latLong = new LatLong(latitude, longitude);
            this.marker.setLatLong(latLong);
            if (this.circle != null) {
                this.circle.setLatLong(latLong);
                this.circle.setRadius(accuracy);
            }
            requestRedraw();
        }
    }
}
