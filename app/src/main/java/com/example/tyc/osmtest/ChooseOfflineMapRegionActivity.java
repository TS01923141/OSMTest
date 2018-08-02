package com.example.tyc.osmtest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tyc.osmtest.utils.GoogleMapOfflineFileController;
import com.example.tyc.osmtest.utils.OnlineTileProvider;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.tasks.OnSuccessListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
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

public class ChooseOfflineMapRegionActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener /* , MapEventsReceiver */ {
    private static final String TAG = "ChooseOfflineMapRegionA";

    private Location currentLocation;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
//    private MapView osmMap;
    private LocationManager locationManager;
    private boolean needMoveCameraToMyLocation = true;
    private Button button_confirm, button_previous, button_clear;
    private ImageView imageView_back;
//    private List<LatLng> latLngList;
    private List<Marker> googleMarkerList;
//    private List<org.osmdroid.views.overlay.Marker> osmMarkerList;
    //    private List<Polyline> polylineList;
    private Polygon googlePolygon;
    private org.osmdroid.views.overlay.Polygon osmPolygon;
    private FloatingActionButton fabSwitchMap, fabMyLocation;
    private OfflineMapSettingFragment offlineMapSettingFragment;
    private int currentMap = GOOGLE_MAP;
    private MyLocationNewOverlay mLocationOverlay;
    private MapEventsOverlay mapEventsOverlay;
    private TileProvider tileProvider;
    private TileOverlay offlineOverlay;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //osm setting
        Configuration.getInstance().load(getApplication(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_offline_map_region);
        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            currentLocation = location;
                        }
                    }
                });
//        if (Objects.equals(latLngList, null)) {
//            latLngList = new ArrayList<>();
//        }
//        if (Objects.equals(osmMarkerList, null)) {
//            osmMarkerList = new ArrayList<>();
//        }
        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        initGoogleMap();
//        initOsmMap();
        imageView_back = findViewById(R.id.imageView_chooseOfflineMapRegion_back);
        button_confirm = findViewById(R.id.button_chooseOfflineMapRegion_confirm);
        button_previous = findViewById(R.id.button_chooseOfflineMapRegion_previous);
        button_clear = findViewById(R.id.button_chooseOfflineMapRegion_clear);
        fabSwitchMap = findViewById(R.id.fab_chooseOfflineMapRegion_switchMap);
        fabMyLocation = findViewById(R.id.fab_chooseOfflineMapRegion_myLocation);

        imageView_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        button_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(googleMarkerList, null) || Objects.equals(googleMarkerList.size(), 0)) {
                    return;
                }
//                if (Objects.equals(latLngList, null) || Objects.equals(latLngList.size(), 0)) {
//                    return;
//                }
//                switch (currentMap) {
//                    case GOOGLE_MAP:
//                        if (Objects.equals(googleMarkerList, null) || Objects.equals(googleMarkerList.size(), 0)) {
//                            return;
//                        }
                        googleMarkerList.get(googleMarkerList.size() - 1).remove();
                        googleMarkerList.remove(googleMarkerList.size() - 1);
                        List<LatLng> latLngList = new ArrayList<>();
                        if (googleMarkerList.size() > 1) {
                            for (int i = 0; i < googleMarkerList.size(); i++) {
                                latLngList.add(googleMarkerList.get(i).getPosition());
                            }
                            googlePolygon = drawGooglePolygon(latLngList, Color.BLUE, 5);
                        } else {
                            if (!Objects.equals(googlePolygon, null)) {
                                googlePolygon.remove();
                                googlePolygon = null;
                            }
                        }
//                        break;
//                    case OPEN_STREET_MAP:
////                        if (Objects.equals(osmMarkerList, null) || Objects.equals(osmMarkerList.size(), 0)) {
////
////                            return;
////                        }
////                        osmMarkerList.get(osmMarkerList.size() - 1).remove(osmMap);
////                        osmMarkerList.remove(osmMarkerList.size() - 1);
////                        List<GeoPoint> geoPointList = new ArrayList<>();
////                        if (osmMarkerList.size() > 1) {
////                            for (int i = 0; i < osmMarkerList.size(); i++) {
////                                geoPointList.add(osmMarkerList.get(i).getPosition());
////                            }
////                            osmPolygon = drawOsmPolygon(geoPointList, Color.BLUE, 5);
////                        } else {
////                            if (!Objects.equals(osmPolygon, null)) {
////                                osmMap.getOverlays().remove(osmPolygon);
////                                osmPolygon = null;
////                            }
////                        }
////                        osmMap.invalidate();
//
//                        break;
//                }
//                latLngList.remove(latLngList.size() - 1);
            }
        });
        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                latLngList.clear();
//                switch (currentMap) {
//                    case GOOGLE_MAP:
                        mMap.clear();
                        googleMarkerList.clear();
                        googlePolygon = null;
                        if (!Objects.equals(currentMap,GOOGLE_MAP)){
                            offlineOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider).zIndex(0));
                        }
//                        break;
//                    case OPEN_STREET_MAP:
//                        osmMap.getTileProvider().clearTileCache();
//                        osmMap.getOverlays().clear();
//                        osmMarkerList.clear();
//                        osmMap.getOverlays().add(0, mapEventsOverlay);
//                        osmMap.getOverlays().add(mLocationOverlay);
//                        osmMap.removeAllViews();
//                        osmMap.invalidate();
//                        break;
//                }
            }
        });

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleMarkerList.size() < 3) {
                    Toast.makeText(ChooseOfflineMapRegionActivity.this, "最少需選擇三個點", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    List<LatLng> latLngList = new ArrayList<>();
                    for (int i = 0; i < googleMarkerList.size(); i++) {
                        latLngList.add(googleMarkerList.get(i).getPosition());
                    }
                    //暫存圖磚, 用來當作預測總圖磚大小的基準
                    GoogleMapOfflineFileController.downloadTempTileFile(
                            GoogleMapOfflineFileController.getTileX(googleMarkerList.get(0).getPosition().longitude, 16),
                            GoogleMapOfflineFileController.getTileY(googleMarkerList.get(0).getPosition().latitude, 16),
                            16, currentMap)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .andThen(Completable.fromAction(new Action() {
                                @Override
                                public void run() throws Exception {
                                    Intent intent = new Intent(ChooseOfflineMapRegionActivity.this, OfflineMapSettingActivity.class);
                                    intent.putParcelableArrayListExtra("LAT_LNG_LIST", (ArrayList<? extends Parcelable>) latLngList);
                                    intent.putExtra("MAP_TYPE", currentMap);
                                    startActivityForResult(intent, 2222);
                                }
                            }))
                            .subscribe();

//                    Intent intent = new Intent(ChooseOfflineMapRegionActivity.this, MapsActivity.class);
//                    intent.putParcelableArrayListExtra("LAT_LNG_LIST" , (ArrayList<? extends Parcelable>) latLngList);
//                    setResult(RESULT_OK, intent);
//                    finish();

//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    Bundle bundle = new Bundle();
//                    bundle.putParcelableArrayList("LAT_LNG_LIST" , (ArrayList<? extends Parcelable>) latLngList);
//                    if (Objects.equals(offlineMapSettingFragment,null)){
//                        offlineMapSettingFragment = new OfflineMapSettingFragment();
//                    }
//                    offlineMapSettingFragment.setArguments(bundle);
//                    fragmentTransaction.replace(R.id.constraintLayout_main_fragmentContain, offlineMapSettingFragment);
//                    fragmentTransaction.commit();
                }
            }
        });

        fabSwitchMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initChooseMapDialog().show();
            }
        });

        fabMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.equals(currentLocation, null)) {
                    return;
                }
//                switch (currentMap) {
//                    case GOOGLE_MAP:
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(
                                new LatLng(
                                        currentLocation.getLatitude(),
                                        currentLocation.getLongitude()
                                )
                        ));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
//                        break;
//                    case OPEN_STREET_MAP:
//                        osmMap.getController().animateTo(new GeoPoint(
//                                currentLocation.getLatitude(),
//                                currentLocation.getLongitude()
//                        ));
//                        break;
//                }
            }
        });
        Toast.makeText(this, "點擊以選擇區域", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableAvailableProviders();
    }

    @Override
    protected void onPause() {
        this.locationManager.removeUpdates(this);
        super.onPause();
    }

    private void initGoogleMap() {
        //initGoogleMap google map
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_chooseOfflineMapRegion);
        mapFragment.getMapAsync(this);
    }

//    private void initOsmMap() {
//        //init osm map
//        osmMap = findViewById(R.id.mapView_chooseOfflineMapRegion_osm);
//        mapEventsOverlay = new MapEventsOverlay(this, this);
//        osmMap.getOverlays().add(0, mapEventsOverlay);
//        MapTileProviderBasic tileProvider = new MapTileProviderBasic(getApplicationContext());
//        osmMap.setTileSource(TileSourceFactory.MAPNIK);
//        osmMap.setBuiltInZoomControls(true);
//        osmMap.setMultiTouchControls(true);
//        IMapController mapController = osmMap.getController();
//        mapController.setZoom(16);
//        //cameraMove
//        if (!Objects.equals(currentLocation, null)) {
//            GeoPoint startPoint = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());
//            mapController.setCenter(startPoint);
//        }
//        //add myLocation mark
//        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), osmMap);
//        this.mLocationOverlay.enableMyLocation();
//        osmMap.getOverlays().add(this.mLocationOverlay);
//    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick: ");
//                if (Objects.equals(latLng,null)){
//                    latLngList = new ArrayList<>();
//                }
                if (Objects.equals(googleMarkerList, null)) {
                    googleMarkerList = new ArrayList<>();
                }
//                latLngList.add(latLng);
                googleMarkerList.add(mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latLng.latitude, latLng.longitude)))
                );
//                latLngList.add(latLng);
//                if (googleMarkerList.size() == 2) {
//                    if (Objects.equals(polylineList, null)) {
//                        polylineList = new ArrayList<>();
//                    }
//                    List<LatLng> latLngList = new ArrayList<>();
//                    latLngList.add(googleMarkerList.get(googleMarkerList.size() - 2).getPosition());
//                    latLngList.add(googleMarkerList.get(googleMarkerList.size() - 1).getPosition());
//                    polylineList.add(drawPolyLine(latLngList, Color.BLUE, 5));
//                }
                if (googleMarkerList.size() >= 3) {
                    List<LatLng> latLngList = new ArrayList<>();
                    for (int i = 0; i < googleMarkerList.size(); i++) {
                        latLngList.add(googleMarkerList.get(i).getPosition());
                    }
                    googlePolygon = drawGooglePolygon(latLngList, Color.BLUE, 5);
                }
            }
        });

        if (!Objects.equals(currentLocation, null)) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(
                    new LatLng(
                            currentLocation.getLatitude(),
                            currentLocation.getLongitude()
                    )
            ));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
        }
    }

    private Polyline drawPolyLine(List<LatLng> latLngList, int lineColor, int lineWidth) {
        Log.i(TAG, "drawPolyline");
        if (Objects.equals(latLngList, null)) {
            return null;
        }
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(latLngList);
        polylineOptions.width(lineWidth);
        polylineOptions.color(lineColor);
        polylineOptions.geodesic(true);
        if (!Objects.equals(mMap, null)) {
            return mMap.addPolyline(polylineOptions);
        }
        return null;
    }

    private Polygon drawGooglePolygon(List<LatLng> latLngList, int lineColor, int lineWidth) {
        if (!Objects.equals(googlePolygon, null)) {
            googlePolygon.remove();
        }
        Log.d(TAG, "drawGooglePolygon: latLngList.size: " + latLngList.size());
        PolygonOptions rectOptions = new PolygonOptions();
        rectOptions.addAll(latLngList);
        rectOptions.strokeColor(lineColor);
        rectOptions.strokeWidth(lineWidth);
        rectOptions.fillColor(Color.parseColor("#40c4c4c4"));
//        rectOptions.zIndex(0);
        Polygon polygon = mMap.addPolygon(rectOptions);
        polygon.setZIndex(10000);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        return polygon;
    }

//    private org.osmdroid.views.overlay.Polygon drawOsmPolygon(List<GeoPoint> geoPointList, int lineColor, int lineWidth) {
//        if (!Objects.equals(osmPolygon, null)) {
//            osmMap.getOverlays().remove(osmPolygon);
//        }
//        Log.d(TAG, "drawGooglePolygon: latLngList.size: " + geoPointList.size());
//        org.osmdroid.views.overlay.Polygon polygon = new org.osmdroid.views.overlay.Polygon();
//        polygon.setPoints(geoPointList);
//        polygon.setStrokeColor(lineColor);
//        polygon.setStrokeWidth(lineWidth);
//        polygon.setFillColor(Color.parseColor("#40c4c4c4"));
//        osmMap.getOverlays().add(polygon);
//        return polygon;
//    }

    @Override
    public void onLocationChanged(Location location) {
        if (Objects.equals(location, null)) {
            return;
        }
        currentLocation = location;
        //googleMap
        if (!Objects.equals(mMap, null) && needMoveCameraToMyLocation) {
            needMoveCameraToMyLocation = false;
//            switch (currentMap) {
//                case GOOGLE_MAP:
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
//                    break;
//
//                case OPEN_STREET_MAP:
//                    osmMap.getController().animateTo(new GeoPoint(
//                            currentLocation.getLatitude(),
//                            currentLocation.getLongitude()
//                    ));
//                    break;
//            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2222) {
                final boolean[] zoomLevels = data.getBooleanArrayExtra("ZOOM_LEVEL_LIST");
                final String mapName = data.getStringExtra("OFFLINE_MAP_NAME");
                List<LatLng> latLngList = new ArrayList<>();
                for (int i = 0; i < googleMarkerList.size(); i++) {
                    latLngList.add(googleMarkerList.get(i).getPosition());
                }
                Intent intent = new Intent(ChooseOfflineMapRegionActivity.this, MapsActivity.class);
                intent.putParcelableArrayListExtra("LAT_LNG_LIST", (ArrayList<? extends Parcelable>) latLngList);
                intent.putExtra("OFFLINE_MAP_NAME", mapName);
                intent.putExtra("ZOOM_LEVEL_LIST", zoomLevels);
                intent.putExtra("MAP_TYPE", currentMap);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    private AlertDialog initChooseMapDialog() {
        View view = LayoutInflater.from(ChooseOfflineMapRegionActivity.this).inflate(R.layout.dialog_switch_map, null);
        TextView textView_onlineMap = view.findViewById(R.id.textView_onlineMap);
        TextView textView_offlineMap = view.findViewById(R.id.textView_offlineMap);
        TextView textView_googleMap = view.findViewById(R.id.textView_googleMap);
        TextView textView_OSM = view.findViewById(R.id.textView_OSM);
        TextView textView_mapsforge = view.findViewById(R.id.textView_mapsforge);
        TextView textView_rudy = view.findViewById(R.id.textView_rudy);
        TextView textView_wmts = view.findViewById(R.id.textView_wmts);

        textView_onlineMap.setVisibility(View.GONE);
        textView_offlineMap.setVisibility(View.GONE);
        textView_mapsforge.setVisibility(View.GONE);

        final AlertDialog alertDialog = new AlertDialog.Builder(ChooseOfflineMapRegionActivity.this)
                .setTitle("選擇地圖")
                .setView(view)
                .create();

        textView_googleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (Objects.equals(currentMap, GOOGLE_MAP)) {
//                    alertDialog.dismiss();
//                    return;
//                }
                mapFragment.getView().setVisibility(View.VISIBLE);
//                osmMap.setVisibility(View.INVISIBLE);
//                clearMap();
                currentMap = GOOGLE_MAP;
                alertDialog.dismiss();
            }
        });
        textView_OSM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (Objects.equals(currentMap, OPEN_STREET_MAP)) {
//                    alertDialog.dismiss();
//                    return;
//                }
//                mapFragment.getView().setVisibility(View.INVISIBLE);
//                osmMap.setVisibility(View.VISIBLE);
//                clearMap();
//                currentMap = OPEN_STREET_MAP;
//                alertDialog.dismiss();
//                if (!Objects.equals(currentMap, OPEN_STREET_MAP)) {
//                    mapFragment.getView().setVisibility(View.VISIBLE);
//                    osmMap.setVisibility(View.INVISIBLE);
//                    clearMap();
//                    currentMap = GOOGLE_MAP;
//                }
                if (!Objects.equals(tileProvider, null)) {
                    offlineOverlay.remove();
                    tileProvider = null;
                }
                currentMap = OPEN_STREET_MAP;
                tileProvider = new OnlineTileProvider(OPEN_STREET_MAP);
                offlineOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider).zIndex(0));
                alertDialog.dismiss();
            }
        });
        textView_rudy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!Objects.equals(currentMap, RUDY_MAP)) {
//                    mapFragment.getView().setVisibility(View.VISIBLE);
//                    osmMap.setVisibility(View.INVISIBLE);
//                    clearMap();
//                    currentMap = GOOGLE_MAP;
//                }
                if (!Objects.equals(tileProvider, null)) {
                    offlineOverlay.remove();
                    tileProvider = null;
                }
                currentMap = RUDY_MAP;
                tileProvider = new OnlineTileProvider(RUDY_MAP);
                offlineOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider).zIndex(0));
                alertDialog.dismiss();
            }
        });
        textView_wmts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!Objects.equals(currentMap, WMTS_MAP)) {
//                    mapFragment.getView().setVisibility(View.VISIBLE);
//                    osmMap.setVisibility(View.INVISIBLE);
//                    clearMap();
//                    currentMap = GOOGLE_MAP;
//                }
                if (!Objects.equals(tileProvider, null)) {
                    offlineOverlay.remove();
                    tileProvider = null;
                }

                currentMap = WMTS_MAP;
                tileProvider = new OnlineTileProvider(WMTS_MAP);
                offlineOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider).zIndex(0));
                alertDialog.dismiss();
            }
        });

        return alertDialog;
    }

    private void clearMap() {
//        latLngList.clear();
//        switch (currentMap) {
//            case GOOGLE_MAP:
                Log.d(TAG, "onClick: mapFragment is null?: " + Objects.equals(mapFragment, null));
                if (Objects.equals(mapFragment, null)) {
                    return;
                }
                if (!Objects.equals(googlePolygon, null)) {
                    googlePolygon.remove();
                }
                mMap.clear();
                if (!Objects.equals(googleMarkerList, null)) {
                    googleMarkerList.clear();
                }
//                break;
//            case OPEN_STREET_MAP:
//                if (Objects.equals(osmMap, null)) {
//                    return;
//                }
//                if (!Objects.equals(osmMarkerList, null)) {
//                    osmMarkerList.clear();
//                }
//                osmMap.getTileProvider().clearTileCache();
//                osmMap.getOverlays().clear();
//                osmMap.getOverlays().add(0, mapEventsOverlay);
//                osmMap.getOverlays().add(mLocationOverlay);
//                osmMap.removeAllViews();
//                osmMap.invalidate();
//                break;
//        }
    }

//    @Override
//    public boolean singleTapConfirmedHelper(GeoPoint p) {
//        Log.d(TAG, "singleTapConfirmedHelper: ");
//        org.osmdroid.views.overlay.Marker osmMarker = new org.osmdroid.views.overlay.Marker(osmMap);
//        osmMarker.setPosition(new GeoPoint(p.getLatitude(), p.getLongitude()));
//        osmMarker.setAnchor(org.osmdroid.views.overlay.Marker.ANCHOR_CENTER, org.osmdroid.views.overlay.Marker.ANCHOR_BOTTOM);
//        osmMap.getOverlays().add(osmMarker);
//        osmMarkerList.add(osmMarker);
//        latLngList.add(new LatLng(p.getLatitude(), p.getLongitude()));
//        if (osmMarkerList.size() >= 3) {
//            List<GeoPoint> geoPointList = new ArrayList<>();
//            for (int i = 0; i < osmMarkerList.size(); i++) {
//                geoPointList.add(osmMarkerList.get(i).getPosition());
//            }
//            osmPolygon = drawOsmPolygon(geoPointList, Color.BLUE, 5);
//        }
//        osmMap.invalidate();
//        return false;
//    }
//
//    @Override
//    public boolean longPressHelper(GeoPoint p) {
//        return false;
//    }
}
