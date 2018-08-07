package com.example.tyc.osmtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.tyc.osmtest.data.OfflineMapData;
import com.example.tyc.osmtest.utils.GoogleMapOfflineFileController;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class OfflineMapActivity extends AppCompatActivity {
    private static final String TAG = "OfflineMapActivity";

    @BindView(R.id.recyclerView_offlineMap)
    RecyclerView recyclerViewOfflineMap;

    private List<OfflineMapData> offlineMapDataList;
    private OfflineMapAdapter offlineMapAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_map);
        ButterKnife.bind(this);
        offlineMapDataList = GoogleMapOfflineFileController.getOfflineMapData();
        offlineMapAdapter = new OfflineMapAdapter(offlineMapDataList, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewOfflineMap.setLayoutManager(linearLayoutManager);
        recyclerViewOfflineMap.setAdapter(offlineMapAdapter);
    }

    @OnClick({R.id.imageView_offlineMap_back, R.id.imageView_offlineMap_add})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageView_offlineMap_back:
                onBackPressed();
                break;
            case R.id.imageView_offlineMap_add:
                startActivityForResult(new Intent(OfflineMapActivity.this, ChooseOfflineMapRegionActivity.class), 4321);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode: " + requestCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == 4321) {
                final List<LatLng> latLngList = data.getParcelableArrayListExtra("LAT_LNG_LIST");
                final boolean[] zoomLevels = data.getBooleanArrayExtra("ZOOM_LEVEL_LIST");
                final String mapName = data.getStringExtra("OFFLINE_MAP_NAME");
                int mapType = data.getIntExtra("MAP_TYPE" , -1);

                final List<Integer> zoomList = new ArrayList<>();
                Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.d(TAG, "run: 1");
                        for (int i = 0 ; i < zoomLevels.length; i++){
                            if (zoomLevels[i]) {
                                zoomList.add(i);
                            }
                        }
                    }
                }).subscribeOn(Schedulers.newThread())
                        .andThen(Completable.fromAction(new Action() {
                            @Override
                            public void run() throws Exception {
                                Log.d(TAG, "run: 2");
                                GoogleMapOfflineFileController.saveTileRegionFile(OfflineMapActivity.this ,mapName /*"TEST_MAP"*/, GoogleMapOfflineFileController.getTileRegion(latLngList, zoomList), mapType);
                            }
                        }))
                        .observeOn(AndroidSchedulers.mainThread())
                        .andThen(Completable.fromAction(new Action() {
                            @Override
                            public void run() throws Exception {
                                Log.d(TAG, "run: 3");
                                offlineMapDataList = GoogleMapOfflineFileController.getOfflineMapData();
                                offlineMapAdapter.setOfflineMapData(offlineMapDataList);
                            }
                        }))
                        .subscribe();
//                final android.app.AlertDialog alertDialog = AlertProgressDialog.show(this, "請稍等", "下載中...", false);
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
//                        GoogleMapOfflineFileController.saveTileRegionFile(mapName /*"TEST_MAP"*/, GoogleMapOfflineFileController.getTileRegion(latLngList, zoomList));
//                    }
//                }).subscribeOn(Schedulers.newThread())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .andThen(Completable.fromAction(new Action() {
//                            @Override
//                            public void run() throws Exception {
//                                alertDialog.dismiss();
//                            }
//                        }))
//                        .subscribe();
            }
        }
    }

}
