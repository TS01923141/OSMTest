package com.example.tyc.osmtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tyc.osmtest.data.TileRegionEntity;
import com.example.tyc.osmtest.utils.GoogleMapOfflineFileController;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.tyc.osmtest.MapsActivity.WMTS_MAP;
import static com.example.tyc.osmtest.utils.GoogleMapOfflineFileController.TEMP_TILE_FILE_NAME;
import static com.example.tyc.osmtest.utils.GoogleMapOfflineFileController.TEMP_TILE_PATH;

public class OfflineMapSettingActivity extends AppCompatActivity {
    private static final String TAG = "OfflineMapSettingActivi";

    @BindView(R.id.imageView_offlineMapSetting_back)
    ImageView imageViewOfflineMapSettingBack;
    @BindView(R.id.textView_offlineMapSetting_confirm)
    TextView textViewOfflineMapSettingConfirm;
    @BindView(R.id.editText_offlineMapSetting_mapName)
    EditText editTextOfflineMapSettingMapName;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel2)
    CheckBox checkboxOfflineMapSettingZoomLevel2;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel3)
    CheckBox checkboxOfflineMapSettingZoomLevel3;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel4)
    CheckBox checkboxOfflineMapSettingZoomLevel4;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel5)
    CheckBox checkboxOfflineMapSettingZoomLevel5;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel6)
    CheckBox checkboxOfflineMapSettingZoomLevel6;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel7)
    CheckBox checkboxOfflineMapSettingZoomLevel7;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel8)
    CheckBox checkboxOfflineMapSettingZoomLevel8;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel9)
    CheckBox checkboxOfflineMapSettingZoomLevel9;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel10)
    CheckBox checkboxOfflineMapSettingZoomLevel10;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel11)
    CheckBox checkboxOfflineMapSettingZoomLevel11;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel12)
    CheckBox checkboxOfflineMapSettingZoomLevel12;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel13)
    CheckBox checkboxOfflineMapSettingZoomLevel13;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel14)
    CheckBox checkboxOfflineMapSettingZoomLevel14;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel15)
    CheckBox checkboxOfflineMapSettingZoomLevel15;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel16)
    CheckBox checkboxOfflineMapSettingZoomLevel16;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel17)
    CheckBox checkboxOfflineMapSettingZoomLevel17;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel18)
    CheckBox checkboxOfflineMapSettingZoomLevel18;
    @BindView(R.id.checkbox_offlineMapSetting_zoomLevel19)
    CheckBox checkboxOfflineMapSettingZoomLevel19;
    @BindView(R.id.textView_offlineMapSetting_estimateSize)
    TextView textViewOfflineMapSettingEstimateSize;
    private List<LatLng> latLngList;
    private boolean[] zoomLeveList;
    private String estimateFileSize;
    private long tileFileSize;
    private List<TileRegionEntity> tileRegionList;
    private int mapType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_map_setting);
        ButterKnife.bind(this);
        tileFileSize = GoogleMapOfflineFileController.getFile(TEMP_TILE_PATH + TEMP_TILE_FILE_NAME).length();
        zoomLeveList = new boolean[20];
        for (int i = 0; i < 20; i++) {
            zoomLeveList[i] = false;
        }
        Intent intent = getIntent();
        if (!Objects.equals(intent,null)){
            latLngList = intent.getParcelableArrayListExtra("LAT_LNG_LIST");
            mapType = intent.getIntExtra("MAP_TYPE", -1);
            Log.d(TAG, "onCreate: mayTpye: " + mapType);
        }
        Log.d(TAG, "onCreate: mayTpye2: " + mapType);
        if (mapType == WMTS_MAP){
            checkboxOfflineMapSettingZoomLevel17.setVisibility(View.INVISIBLE);
            checkboxOfflineMapSettingZoomLevel18.setVisibility(View.INVISIBLE);
            checkboxOfflineMapSettingZoomLevel19.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick({R.id.imageView_offlineMapSetting_back, R.id.textView_offlineMapSetting_confirm, R.id.editText_offlineMapSetting_mapName, R.id.checkbox_offlineMapSetting_zoomLevel2, R.id.checkbox_offlineMapSetting_zoomLevel3, R.id.checkbox_offlineMapSetting_zoomLevel4, R.id.checkbox_offlineMapSetting_zoomLevel5, R.id.checkbox_offlineMapSetting_zoomLevel6, R.id.checkbox_offlineMapSetting_zoomLevel7, R.id.checkbox_offlineMapSetting_zoomLevel8, R.id.checkbox_offlineMapSetting_zoomLevel9, R.id.checkbox_offlineMapSetting_zoomLevel10, R.id.checkbox_offlineMapSetting_zoomLevel11, R.id.checkbox_offlineMapSetting_zoomLevel12, R.id.checkbox_offlineMapSetting_zoomLevel13, R.id.checkbox_offlineMapSetting_zoomLevel14, R.id.checkbox_offlineMapSetting_zoomLevel15, R.id.checkbox_offlineMapSetting_zoomLevel16, R.id.checkbox_offlineMapSetting_zoomLevel17, R.id.checkbox_offlineMapSetting_zoomLevel18, R.id.checkbox_offlineMapSetting_zoomLevel19})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.checkbox_offlineMapSetting_zoomLevel2:
//                zoomLeveList.set(2, !zoomLeveList.get(2));
                zoomLeveList[2] = !zoomLeveList[2];
                setEstimateTileSize();
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel3:
//                zoomLeveList.set(3, !zoomLeveList.get(3));
                zoomLeveList[3] = !zoomLeveList[3];
                setEstimateTileSize();
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel4:
//                zoomLeveList.set(4, !zoomLeveList.get(4));
                zoomLeveList[4] = !zoomLeveList[4];
                setEstimateTileSize();
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel5:
//                zoomLeveList.set(5, !zoomLeveList.get(5));
                zoomLeveList[5] = !zoomLeveList[5];
                setEstimateTileSize();
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel6:
//                zoomLeveList.set(6, !zoomLeveList.get(6));
                zoomLeveList[6] = !zoomLeveList[6];
                setEstimateTileSize();
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel7:
//                zoomLeveList.set(7, !zoomLeveList.get(7));
                zoomLeveList[7] = !zoomLeveList[7];
                setEstimateTileSize();
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel8:
//                zoomLeveList.set(8, !zoomLeveList.get(8));
                zoomLeveList[8] = !zoomLeveList[8];
                setEstimateTileSize();
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel9:
//                zoomLeveList.set(9, !zoomLeveList.get(9));
                zoomLeveList[9] = !zoomLeveList[9];
                setEstimateTileSize();
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel10:
//                zoomLeveList.set(10, !zoomLeveList.get(10));
                zoomLeveList[10] = !zoomLeveList[10];
                setEstimateTileSize();
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel11:
//                zoomLeveList.set(11, !zoomLeveList.get(11));
                zoomLeveList[11] = !zoomLeveList[11];
                setEstimateTileSize();
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel12:
//                zoomLeveList.set(12, !zoomLeveList.get(12));
                zoomLeveList[12] = !zoomLeveList[12];
                setEstimateTileSize();
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel13:
//                zoomLeveList.set(13, !zoomLeveList.get(13));
                zoomLeveList[13] = !zoomLeveList[13];
                setEstimateTileSize();
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel14:
//                zoomLeveList.set(14, !zoomLeveList.get(14));
                zoomLeveList[14] = !zoomLeveList[14];
                setEstimateTileSize();
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel15:
//                zoomLeveList.set(15, !zoomLeveList.get(15));
                zoomLeveList[15] = !zoomLeveList[15];
                setEstimateTileSize();
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel16:
//                zoomLeveList.set(16, !zoomLeveList.get(16));
                zoomLeveList[16] = !zoomLeveList[16];
                setEstimateTileSize();
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel17:
//                zoomLeveList.set(17, !zoomLeveList.get(17));
                zoomLeveList[17] = !zoomLeveList[17];
                setEstimateTileSize();
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel18:
//                zoomLeveList.set(18, !zoomLeveList.get(18));
                zoomLeveList[18] = !zoomLeveList[18];
                setEstimateTileSize();
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel19:
//                zoomLeveList.set(19, !zoomLeveList.get(19));
                zoomLeveList[19] = !zoomLeveList[19];
                setEstimateTileSize();
                break;
            case R.id.imageView_offlineMapSetting_back:
                onBackPressed();
                break;
            case R.id.textView_offlineMapSetting_confirm:
                if (Objects.equals(editTextOfflineMapSettingMapName.getText(), null) ||
                        Objects.equals(editTextOfflineMapSettingMapName.getText().toString(), "")) {
                    Toast.makeText(this, "請輸入地圖名稱", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < zoomLeveList.length; i++) {
                    if (zoomLeveList[i]) {
                        break;
                    } else if (i == zoomLeveList.length - 1 /* && !zoomLeveList.get(zoomLeveList.size()-1)*/) {
                        Toast.makeText(this, "請至少選擇一個縮放層級", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Intent intent = new Intent(this, MapsActivity.class);
//                intent.putParcelableArrayListExtra("LAT_LNG_LIST", (ArrayList<? extends Parcelable>) latLngList);
                intent.putExtra("OFFLINE_MAP_NAME", editTextOfflineMapSettingMapName.getText().toString());
                intent.putExtra("ZOOM_LEVEL_LIST", zoomLeveList);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private void setEstimateTileSize(){
        int tileCount = GoogleMapOfflineFileController.estimateTileCount(
                GoogleMapOfflineFileController.getTileRegion(latLngList, zoomLeveList), zoomLeveList);
        estimateFileSize = GoogleMapOfflineFileController.getFormatByte(tileCount
                        ,tileFileSize);
        String s = tileCount + " 片 , " + estimateFileSize;
        textViewOfflineMapSettingEstimateSize.setText(s);
    }
}
