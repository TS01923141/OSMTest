package com.example.tyc.osmtest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tyc.osmtest.utils.OfflineTileProvider;
import com.example.tyc.osmtest.utils.MyLocationOverlay;
import com.example.tyc.osmtest.utils.OnlineTileProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Circle;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.osmdroid.views.util.constants.OverlayConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private static final String TAG = "MapsActivity";

    private static final String MAP_FILE = "MOI_OSM_Taiwan_TOPO_Rudy.map";
    public static final int GOOGLE_MAP = 0;
    public static final int OPEN_STREET_MAP = 1;
    private static final int MAPSFORGE = 2;
    public static final int RUDY_MAP = 3;
    public static final int WMTS_MAP = 4;
    private static boolean moveToMyLocation = true;
    private boolean chooseOfflineMapRegion = false;

    private SupportMapFragment mapFragment;
    private Location currentLocation;
    private int currentMap = GOOGLE_MAP;
    private LocationManager locationManager;
    private MyLocationOverlay myLocationOverlay;
    private List<GeoPoint> geoPoints;
    private GoogleMap mMap;
    private ConstraintLayout googleMapFrame;
    private MapView osmMap = null;
    private org.mapsforge.map.android.view.MapView mapsforgeMapView = null;
    private TileCache tileCache;
    private MyLocationNewOverlay mLocationOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    private static final int RequestPermissionCode = 1;
    private FloatingActionButton fabSwitchMap, fabMyLocation, fabCloseMap, fabAddMarker, fabDownloadOfflineMap;
    private org.mapsforge.core.graphics.Bitmap mapsforgeBitmap;
    private TileProvider tileProvider;
    private TileOverlay offlineOverlay;

    private static Paint getPaint(int color, int strokeWidth, Style style) {
        Paint paint = AndroidGraphicFactory.INSTANCE.createPaint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
        return paint;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //osm setting
        Configuration.getInstance().load(getApplication(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        //initGoogleMap mapsforge library
        AndroidGraphicFactory.createInstance(getApplication());
        setContentView(R.layout.activity_maps);
        checkPermissions();
//        tileProvider.getTile(95, 55, 7);
        //get my location
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //init Views
        googleMapFrame = findViewById(R.id.google_map_frame);
        fabCloseMap = findViewById(R.id.fab_closeMap);
        fabSwitchMap = findViewById(R.id.fab_switchMap);
        fabMyLocation = findViewById(R.id.fab_myLocation);
        fabAddMarker = findViewById(R.id.fab_addMarker);
        fabDownloadOfflineMap = findViewById(R.id.fab_downloadOfflineMap);
        fabMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(currentLocation, null)) {
                    return;
                }
                switch (currentMap) {
                    case GOOGLE_MAP:
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(
                                new LatLng(
                                        currentLocation.getLatitude(),
                                        currentLocation.getLongitude()
                                )
                        ));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
                        break;
                    case OPEN_STREET_MAP:
                        osmMap.getController().animateTo(new GeoPoint(
                                currentLocation.getLatitude(),
                                currentLocation.getLongitude()
                        ));
                        break;
                    case MAPSFORGE:
                        mapsforgeMapView.setCenter(new LatLong(
                                currentLocation.getLatitude(),
                                currentLocation.getLongitude()
                        ));
                        break;
                }
            }
        });
        fabSwitchMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initChooseMapDialog().show();
            }
        });
        fabCloseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearMap();
            }
        });
        fabAddMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMarker();
            }
        });
        fabDownloadOfflineMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //先下載一張並且讀取
                //設定範圍下載並讀取->計算各邊界的圖磚數字, 中間的全部下載
                //讓使用者選範圍並下載讀取
                //切換地圖可選範圍並下載讀取

//                Completable.fromAction(new Action() {
//                    @Override
//                    public void run() throws Exception {
////                        GoogleMapOfflineFileController.saveTileFile("TEST_MAP", GoogleMapOfflineFileController.getTileX(currentLocation.getLongitude(), 16), GoogleMapOfflineFileController.getTileY(currentLocation.getLatitude(), 16), 16);
//                        List<LatLng> latLngs = new ArrayList<>();
//                        latLngs.add(new LatLng(25.028155, 121.528526));
//                        latLngs.add(new LatLng(25.024927, 121.534405));
//                        List<Integer> zoomList = new ArrayList<>();
//                        zoomList.add(16);
//                        zoomList.add(17);
//                        GoogleMapOfflineFileController.saveTileRegionFile("TEST_MAP", GoogleMapOfflineFileController.getTileRegion(latLngs, zoomList));
//                    }
//                }).subscribeOn(Schedulers.newThread())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .andThen(Completable.fromAction(new Action() {
//                            @Override
//                            public void run() throws Exception {
//                                GoogleMapOfflineTileProvider tileProvider = new GoogleMapOfflineTileProvider("TEST_MAP");
//                                TileOverlay offlineOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider).zIndex(0));
//                            }
//                        }))
//                        .subscribe();

//                Toast.makeText(MapsActivity.this, "請選擇離線地圖範圍", Toast.LENGTH_SHORT).show();
//                startActivityForResult(new Intent(MapsActivity.this, ChooseOfflineMapRegionActivity.class), 1111);

                Intent intent = new Intent(MapsActivity.this, OfflineMapActivity.class);
                startActivityForResult(intent, 1234);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Objects.equals(osmMap, null)) {
            osmMap.onResume();
        }
        //get my location
        enableAvailableProviders();
    }

    @Override
    protected void onPause() {
        //mapsforge
        this.locationManager.removeUpdates(this);
        super.onPause();
        if (!Objects.equals(osmMap, null)) {
            osmMap.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        mapsforgeMapView.destroy();
//        AndroidGraphicFactory.clearResourceMemoryCache();
        super.onDestroy();
    }

    private void initMapsforge() {
        Log.d(TAG, "initMapsforge: ");
//        mapsforgeMapView = new org.mapsforge.map.android.view.MapView(this);
        mapsforgeMapView = findViewById(R.id.mapsforge_map);

        try {
            mapsforgeMapView.setClickable(true);
            mapsforgeMapView.getMapScaleBar().setVisible(true);
            mapsforgeMapView.setBuiltInZoomControls(true);

            tileCache = AndroidUtil.createTileCache(this, "mapcache",
                    mapsforgeMapView.getModel().displayModel.getTileSize(), 10f,
                    mapsforgeMapView.getModel().frameBufferModel.getOverdrawFactor());

//            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//            File mapFile = new File(Environment.getExternalStorageDirectory(), MAP_FILE);
            File mapFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), MAP_FILE);
            MapDataStore mapDataStore = new MapFile(mapFile);
            TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
                    mapsforgeMapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
            tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT);

            mapsforgeMapView.getLayerManager().getLayers().add(tileRendererLayer);

            mapsforgeMapView.setCenter(new LatLong(25.026231, 121.531847));
            mapsforgeMapView.setZoomLevel((byte) 16);

            //init MyLocation resource
            Drawable myLocationIcon = ContextCompat.getDrawable(MapsActivity.this, R.drawable.ic_my_location_black_24dp);
            DrawableCompat.setTint(myLocationIcon, ContextCompat.getColor(this, android.R.color.holo_blue_dark));
            Marker myLocationMarker = new Marker(null, AndroidGraphicFactory.convertToBitmap(myLocationIcon), 0, 0);
            // circle to show the location accuracy (optional)
            Circle circle = new Circle(null, 0,
                    getPaint(AndroidGraphicFactory.INSTANCE.createColor(48, 0, 0, 255), 0, Style.FILL),
                    getPaint(AndroidGraphicFactory.INSTANCE.createColor(160, 0, 0, 255), 2, Style.STROKE));
            //create MyLocation marker
            myLocationOverlay = new MyLocationOverlay(myLocationMarker, circle);
            //add MyLocation marker
            mapsforgeMapView.getLayerManager().getLayers().add(myLocationOverlay);

            //create marker
            Drawable icon = ContextCompat.getDrawable(MapsActivity.this, R.drawable.ic_location_black_24dp);
            DrawableCompat.setTint(icon, ContextCompat.getColor(this, android.R.color.holo_red_dark));
            org.mapsforge.core.graphics.Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(icon);
            bitmap.incrementRefCount();
            Marker marker = new Marker(new LatLong(25.026231, 121.531847), bitmap, 0, -bitmap.getHeight() / 2) {
                @Override
                public boolean onTap(LatLong geoPoint, Point viewPosition, Point tapPoint) {
                    if (contains(viewPosition, tapPoint)) {
                        Toast.makeText(MapsActivity.this,
                                "The Marker was tapped " + geoPoint.toString(),
                                Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    return false;
                }
            };
            //add marker to map
            mapsforgeMapView.getLayerManager().getLayers().add(marker);

            //create polyLine point
            List<LatLong> latLongPoints = new ArrayList<>();
            latLongPoints.add(new LatLong(25.026231, 121.531847));
            latLongPoints.add(new LatLong(25.027316, 121.522310));
            latLongPoints.add(new LatLong(25.026763, 121.518977));
            //create polyLine
            Paint paint = AndroidGraphicFactory.INSTANCE.createPaint();
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(5 * mapsforgeMapView.getModel().displayModel.getScaleFactor());
            paint.setStyle(Style.STROKE);
            org.mapsforge.map.layer.overlay.Polyline polyline = new org.mapsforge.map.layer.overlay.Polyline(
                    paint, AndroidGraphicFactory.INSTANCE);

            polyline.getLatLongs().addAll(latLongPoints);
//            polyline.setPoints(geoPoints);
            //add polyline to map
            mapsforgeMapView.getLayerManager().getLayers().add(polyline);

//            mapsforgeMapView.getLayerManager().getLayers().

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initGoogleMap() {
        //initGoogleMap google map
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initOsmMap() {
        //init osm map
        osmMap = (MapView) findViewById(R.id.osm_map);
//        MapTileProviderBasic tileProvider = new MapTileProviderBasic(getApplicationContext());
//        ITileSource tileSource = new GoogleMapTileSource("TEST", 2, 19, 256, ".png",
//                new String[]{"http://mt1.google.com/vt/lyrs=m"});
//        osmMap.setTileSource(tileSource);
        osmMap.setTileSource(TileSourceFactory.MAPNIK);
        osmMap.setBuiltInZoomControls(true);
        osmMap.setMultiTouchControls(true);
        IMapController mapController = osmMap.getController();
        mapController.setZoom(16);
        //cameraMove
        GeoPoint startPoint = new GeoPoint(25.026231, 121.531847);
        mapController.setCenter(startPoint);
        //initGoogleMap mark image

        //add mark
        addOsmMarker();
        //add myLocation mark
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), osmMap);
        this.mLocationOverlay.enableMyLocation();
        osmMap.getOverlays().add(this.mLocationOverlay);
        //drawPolyLine
        geoPoints = new ArrayList<>();
        geoPoints.add(new GeoPoint(25.026231, 121.531847));
        geoPoints.add(new GeoPoint(25.027316, 121.522310));
        geoPoints.add(new GeoPoint(25.026763, 121.518977));
        drawPolyLine(geoPoints);

        osmMap.removeAllViews();
//        //enable the Grid line Overlay
//        LatLonGridlineOverlay2 overlay = new LatLonGridlineOverlay2();
//        osmMap.getOverlays().add(overlay);
//        //add roattion gestures
//        mRotationGestureOverlay = new RotationGestureOverlay(this, osmMap);
//        mRotationGestureOverlay.setEnabled(true);
//        osmMap.setMultiTouchControls(true);
//        osmMap.getOverlays().add(this.mRotationGestureOverlay);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
//        mMap.addOsmMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (!Objects.equals(currentLocation, null)) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(
                    new LatLng(
                            currentLocation.getLatitude(),
                            currentLocation.getLongitude()
                    )
            ));
        }
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));

//        GoogleMapOfflineTileProvider1 googleMapOfflineTileProvider1 = new GoogleMapOfflineTileProvider1();
//        TileOverlay offlineOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(googleMapOfflineTileProvider1).zIndex(0));
    }


    private void addOsmMarker() {
        //your items
        ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
        items.add(new OverlayItem("Title", "Description", new GeoPoint(25.026231d, 121.531847d))); // Lat/Lon decimal degrees

//the overlay
//        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(this ,items,
//                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
        Drawable icon = ContextCompat.getDrawable(MapsActivity.this, R.drawable.ic_location_black_24dp);
        DrawableCompat.setTint(icon, ContextCompat.getColor(this, android.R.color.holo_red_dark));
        Drawable foucusIcon = ContextCompat.getDrawable(MapsActivity.this, R.drawable.ic_location_black_24dp);
        DrawableCompat.setTint(foucusIcon, ContextCompat.getColor(this, android.R.color.holo_green_dark));
//        icon.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));
        ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(items,
                icon, icon, /*android.R.color.white*/OverlayConstants.NOT_SET,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, MapsActivity.this);
        mOverlay.setFocusItemsOnTap(true);

        osmMap.getOverlays().add(mOverlay);
    }

    private void drawPolyLine(List<GeoPoint> geoPoints) {
//        List<GeoPoint> geoPoints = new ArrayList<>();
//add your points here
        Polyline line = new Polyline();   //see note below!
//        line.setColor(android.R.color.holo_blue_light);
        line.setColor(Color.BLUE);
        line.setPoints(geoPoints);
        line.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                Toast.makeText(mapView.getContext(), "polyline with " + polyline.getPoints().size() + "pts was tapped", Toast.LENGTH_LONG).show();
                return false;
            }
        });
        osmMap.getOverlayManager().add(line);
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!checkAllPermission()) {
                requestPermission();
            } else {
                initGoogleMap();
                initOsmMap();
                initMapsforge();
            }
        }
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {
                        CAMERA,
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE,
                        INTERNET,
                        ACCESS_FINE_LOCATION,
                        ACCESS_NETWORK_STATE,
                        //check more permissions if you want
//                     ........


                }, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {

                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadExternalStatePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadWriteStatePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean InternetPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean AccessFineLocationPermission = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean AccessNetworkStatePermission = grantResults[5] == PackageManager.PERMISSION_GRANTED;

//                    .......


                    if (CameraPermission && ReadExternalStatePermission && ReadWriteStatePermission && InternetPermission && AccessFineLocationPermission && AccessNetworkStatePermission) {
                        initGoogleMap();
                        initOsmMap();
                        initMapsforge();
//                        Toast.makeText(MainActivity.this, "Permissions acquired", Toast.LENGTH_LONG).show();
                    } else {
//                        Toast.makeText(MainActivity.this, "One or more permissions denied", Toast.LENGTH_LONG).show();
                        checkPermissions();
                    }
                }

                break;
            default:
                break;
        }
    }

    public boolean checkAllPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int FourthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), INTERNET);
        int FifthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int sixPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_NETWORK_STATE);
//        .....


        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FourthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FifthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                sixPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (Objects.equals(location, null)) {
            return;
        }
        currentLocation = location;

//        Log.d(TAG, "onLocationChanged: getTileX: " + GoogleMapOfflineFileController.getTileX(location.getLongitude(), 16));
//        Log.d(TAG, "onLocationChanged: getTileY: " + GoogleMapOfflineFileController.getTileY(location.getLatitude(), 16));
        this.myLocationOverlay.setPosition(location.getLatitude(), location.getLongitude(), location.getAccuracy());
        if (moveToMyLocation) {
            moveToMyLocation = false;
            switch (currentMap) {
                case GOOGLE_MAP:
                    //googleMap
                    if (!Objects.equals(mMap, null)) {
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(
                                new LatLng(
                                        currentLocation.getLatitude(),
                                        currentLocation.getLongitude()
                                )
                        ));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
                    }
                    break;

                case OPEN_STREET_MAP:
                    osmMap.getController().animateTo(new GeoPoint(
                            currentLocation.getLatitude(),
                            currentLocation.getLongitude()
                    ));
                    break;

                case MAPSFORGE:
                    // Follow location
                    this.mapsforgeMapView.setCenter(new LatLong(location.getLatitude(), location.getLongitude()));
                    break;
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @SuppressLint("MissingPermission")
    private void enableAvailableProviders() {
        this.locationManager.removeUpdates(this);

        for (String provider : this.locationManager.getProviders(true)) {
            if (LocationManager.GPS_PROVIDER.equals(provider)
                    || LocationManager.NETWORK_PROVIDER.equals(provider)) {
                this.locationManager.requestLocationUpdates(provider, 0, 0, this);
            }
        }
    }

    private AlertDialog initChooseMapDialog() {
        View view = LayoutInflater.from(MapsActivity.this).inflate(R.layout.dialog_switch_map, null);
        TextView textView_googleMap = view.findViewById(R.id.textView_googleMap);
        TextView textView_OSM = view.findViewById(R.id.textView_OSM);
        TextView textView_mapsforge = view.findViewById(R.id.textView_mapsforge);
        TextView textView_rudy = view.findViewById(R.id.textView_rudy);
        TextView textView_wmts = view.findViewById(R.id.textView_wmts);

        final AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this)
                .setTitle("選擇地圖")
                .setView(view)
                .create();

        textView_googleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(currentMap, GOOGLE_MAP)) {
                    alertDialog.dismiss();
                    return;
                }
                googleMapFrame.setVisibility(View.VISIBLE);
                osmMap.setVisibility(View.INVISIBLE);
                mapsforgeMapView.setVisibility(View.INVISIBLE);
                clearMap();
                currentMap = GOOGLE_MAP;
                alertDialog.dismiss();
            }
        });
        textView_OSM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(currentMap, OPEN_STREET_MAP)) {
                    alertDialog.dismiss();
                    return;
                }
                clearMap();
                googleMapFrame.setVisibility(View.INVISIBLE);
                osmMap.setVisibility(View.VISIBLE);
                mapsforgeMapView.setVisibility(View.INVISIBLE);
                clearMap();
                currentMap = OPEN_STREET_MAP;
                alertDialog.dismiss();
            }
        });
        textView_mapsforge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(currentMap, MAPSFORGE)) {
                    alertDialog.dismiss();
                    return;
                }
                clearMap();
                googleMapFrame.setVisibility(View.INVISIBLE);
                osmMap.setVisibility(View.INVISIBLE);
                mapsforgeMapView.setVisibility(View.VISIBLE);
                clearMap();
                currentMap = MAPSFORGE;
                alertDialog.dismiss();
            }
        });

        textView_rudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Objects.equals(currentMap, GOOGLE_MAP)) {
                    googleMapFrame.setVisibility(View.VISIBLE);
                    osmMap.setVisibility(View.INVISIBLE);
                    mapsforgeMapView.setVisibility(View.INVISIBLE);
                    clearMap();
                    currentMap = GOOGLE_MAP;
                }
                if (!Objects.equals(tileProvider, null)) {
                    offlineOverlay.remove();
                    tileProvider = null;
                }
                tileProvider = new OnlineTileProvider(RUDY_MAP);
                offlineOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider).zIndex(0));
                alertDialog.dismiss();
            }
        });
        textView_wmts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Objects.equals(currentMap, GOOGLE_MAP)) {
                    googleMapFrame.setVisibility(View.VISIBLE);
                    osmMap.setVisibility(View.INVISIBLE);
                    mapsforgeMapView.setVisibility(View.INVISIBLE);
                    clearMap();
                    currentMap = GOOGLE_MAP;
                }
                if (!Objects.equals(tileProvider, null)) {
                    offlineOverlay.remove();
                    tileProvider = null;
                }
                tileProvider = new OnlineTileProvider(WMTS_MAP);
                offlineOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider).zIndex(0));
                alertDialog.dismiss();
            }
        });

        return alertDialog;
    }

    //clear all marker and polyLine
    private void clearMap() {
        switch (currentMap) {
            case GOOGLE_MAP:
                Log.d(TAG, "onClick: mapFragment is null?: " + Objects.equals(mapFragment, null));
                if (Objects.equals(mapFragment, null)) {
                    return;
                }
                mMap.clear();
//                        getSupportFragmentManager().beginTransaction().remove(mapFragment).commit();
//                        googleMapFrame.setVisibility(View.INVISIBLE);
//                        Log.d(TAG, "onClick: mapFragment is null?: " + Objects.equals(mapFragment,null));
                break;
            case OPEN_STREET_MAP:
                if (Objects.equals(osmMap, null)) {
                    return;
                }
                osmMap.getTileProvider().clearTileCache();
                osmMap.getOverlays().clear();
                osmMap.getOverlays().add(mLocationOverlay);
                osmMap.removeAllViews();
//                        osmMap.getTileProvider().detach();
//                        osmMap.onPause();
//                        osmMap.onDetach();
                break;
            case MAPSFORGE:
                if (Objects.equals(mapsforgeMapView, null)) {
                    return;
                }
                tileCache.purge();
                for (int i = mapsforgeMapView.getLayerManager().getLayers().size() - 1; i > 1; i--) {
                    mapsforgeMapView.getLayerManager().getLayers().remove(mapsforgeMapView.getLayerManager().getLayers().get(i));
                }
                mapsforgeMapView.removeAllViews();
                break;
        }
    }

    private void addMarker() {
        switch (currentMap) {
            case GOOGLE_MAP:
                //create GoogleMap marker
                com.google.android.gms.maps.model.Marker gMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())));
                break;
            case OPEN_STREET_MAP:
                //create osmMap marker

                org.osmdroid.views.overlay.Marker osmMarker = new org.osmdroid.views.overlay.Marker(osmMap);
                osmMarker.setPosition(new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude()));
                osmMarker.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM);
//                osmMarker.setTitle(locationName);
//                osmMarker.setSnippet(lat + " , " + lng);
                osmMap.getOverlays().add(osmMarker);
                break;
            case MAPSFORGE:
                //create mapsforge marker
                if (Objects.equals(mapsforgeBitmap, null)) {
                    Drawable icon = ContextCompat.getDrawable(MapsActivity.this, R.drawable.ic_location_black_24dp);
                    DrawableCompat.setTint(icon, ContextCompat.getColor(MapsActivity.this, android.R.color.holo_red_dark));
                    mapsforgeBitmap = AndroidGraphicFactory.convertToBitmap(icon);
                    mapsforgeBitmap.incrementRefCount();
                }
                Marker marker = new Marker(new LatLong(25.026231, 121.531847), mapsforgeBitmap, 0, -mapsforgeBitmap.getHeight() / 2) {
                    @Override
                    public boolean onTap(LatLong geoPoint, Point viewPosition, Point tapPoint) {
                        if (contains(viewPosition, tapPoint)) {
                            Toast.makeText(MapsActivity.this,
                                    "The Marker was tapped " + geoPoint.toString(),
                                    Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                };
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (resultCode == RESULT_OK) {
            if (requestCode == 1111) {
//                final List<LatLng> latLngList = data.getParcelableArrayListExtra("LAT_LNG_LIST");
//                final boolean[] zoomLevels = data.getBooleanArrayExtra("ZOOM_LEVEL_LIST");
//                final String mapName = data.getStringExtra("OFFLINE_MAP_NAME");
//                int mapType = data.getIntExtra("MAP_TYPE", -1);
//                Completable.fromAction(new Action() {
//                    @Override
//                    public void run() throws Exception {
//                        List<Integer> zoomList = new ArrayList<>();
////                        zoomList.add(16);
//                        for (int i = 0 ; i < zoomLevels.length; i++){
//                            if (zoomLevels[i]) {
//                                zoomList.add(i);
//                            }
//                        }
//                        GoogleMapOfflineFileController.saveTileRegionFile(mapName /*"TEST_MAP"*/, GoogleMapOfflineFileController.getTileRegion(latLngList, zoomList), mapType);
//                    }
//                }).subscribeOn(Schedulers.newThread())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .andThen(Completable.fromAction(new Action() {
//                            @Override
//                            public void run() throws Exception {
//                                GoogleMapOfflineTileProvider tileProvider = new GoogleMapOfflineTileProvider(mapName /*"TEST_MAP"*/);
//                                TileOverlay offlineOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider).zIndex(0));
//                            }
//                        }))
//                        .subscribe();
            } else if (requestCode == 1234) {
                String mapName = data.getStringExtra("OFFLINE_MAP_FILE_NAME");
                int mapType = data.getIntExtra("MAP_TYPE", -1);
                if (!Objects.equals(tileProvider, null)) {
                    offlineOverlay.remove();
                    tileProvider = null;
                }
                    tileProvider = new OfflineTileProvider(mapName , mapType);
                    offlineOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider).zIndex(0));
                    if (currentMap != GOOGLE_MAP){
                        currentMap = GOOGLE_MAP;
                        mapFragment.getView().setVisibility(View.VISIBLE);
                        osmMap.setVisibility(View.INVISIBLE);
                        mapsforgeMapView.setVisibility(View.INVISIBLE);
                    }
//                if (mapType == GOOGLE_MAP) {
//                    tileProvider = new GoogleMapOfflineTileProvider(mapName /*"TEST_MAP"*/);
//                    offlineOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider).zIndex(0));
//                    if (currentMap != GOOGLE_MAP){
//                        currentMap = GOOGLE_MAP;
//                        mapFragment.getView().setVisibility(View.VISIBLE);
//                        osmMap.setVisibility(View.INVISIBLE);
//                    }
//                } else if (mapType == OPEN_STREET_MAP) {
//                    ITileSource tileSource = new OfflineMapTileSource(mapName, 2, 19, 256, ".png",
//                            new String[]{"http://b.tile.openstreetmap.fr/osmfr/"});
//                    osmMap.setTileSource(tileSource);
//                    if (currentMap != OPEN_STREET_MAP){
//                        currentMap = OPEN_STREET_MAP;
//                        osmMap.setVisibility(View.VISIBLE);
//                        mapFragment.getView().setVisibility(View.INVISIBLE);
//                    }
//                }
            }
        }
    }
}
