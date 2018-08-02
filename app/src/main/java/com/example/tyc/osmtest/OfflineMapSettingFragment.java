package com.example.tyc.osmtest;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class OfflineMapSettingFragment extends Fragment {
    @BindView(R.id.imageView_offlineMapSetting_back)
    ImageView imageViewOfflineMapSettingBack;
    @BindView(R.id.button_offlineMapSetting_confirm)
    TextView buttonOfflineMapSettingConfirm;
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
    Unbinder unbinder;

    private List<LatLng> latLngList;
    private boolean[] zoomLeveList;

    public OfflineMapSettingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zoomLeveList = new boolean[20];
        for (int i = 0 ; i < 20; i++){
            zoomLeveList[i] = false;
        }
        Bundle bundle = getArguments();
        if (Objects.equals(bundle, null)){
            latLngList = getArguments().getParcelableArrayList("LAT_LNG_LIST");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offline_map_setting, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.checkbox_offlineMapSetting_zoomLevel2:
//                zoomLeveList.set(2, !zoomLeveList.get(2));
                zoomLeveList[2] = !zoomLeveList[2];
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel3:
//                zoomLeveList.set(3, !zoomLeveList.get(3));
                zoomLeveList[3] = !zoomLeveList[3];
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel4:
//                zoomLeveList.set(4, !zoomLeveList.get(4));
                zoomLeveList[4] = !zoomLeveList[4];
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel5:
//                zoomLeveList.set(5, !zoomLeveList.get(5));
                zoomLeveList[5] = !zoomLeveList[5];
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel6:
//                zoomLeveList.set(6, !zoomLeveList.get(6));
                zoomLeveList[6] = !zoomLeveList[6];
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel7:
//                zoomLeveList.set(7, !zoomLeveList.get(7));
                zoomLeveList[7] = !zoomLeveList[7];
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel8:
//                zoomLeveList.set(8, !zoomLeveList.get(8));
                zoomLeveList[8] = !zoomLeveList[8];
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel9:
//                zoomLeveList.set(9, !zoomLeveList.get(9));
                zoomLeveList[9] = !zoomLeveList[9];
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel10:
//                zoomLeveList.set(10, !zoomLeveList.get(10));
                zoomLeveList[10] = !zoomLeveList[10];
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel11:
//                zoomLeveList.set(11, !zoomLeveList.get(11));
                zoomLeveList[11] = !zoomLeveList[11];
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel12:
//                zoomLeveList.set(12, !zoomLeveList.get(12));
                zoomLeveList[12] = !zoomLeveList[12];
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel13:
//                zoomLeveList.set(13, !zoomLeveList.get(13));
                zoomLeveList[13] = !zoomLeveList[13];
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel14:
//                zoomLeveList.set(14, !zoomLeveList.get(14));
                zoomLeveList[14] = !zoomLeveList[14];
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel15:
//                zoomLeveList.set(15, !zoomLeveList.get(15));
                zoomLeveList[15] = !zoomLeveList[15];
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel16:
//                zoomLeveList.set(16, !zoomLeveList.get(16));
                zoomLeveList[16] = !zoomLeveList[16];
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel17:
//                zoomLeveList.set(17, !zoomLeveList.get(17));
                zoomLeveList[17] = !zoomLeveList[17];
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel18:
//                zoomLeveList.set(18, !zoomLeveList.get(18));
                zoomLeveList[18] = !zoomLeveList[18];
                break;
            case R.id.checkbox_offlineMapSetting_zoomLevel19:
//                zoomLeveList.set(19, !zoomLeveList.get(19));
                zoomLeveList[19] = !zoomLeveList[19];
                break;
            case R.id.imageView_offlineMapSetting_back:
                getActivity().onBackPressed();
                break;
            case R.id.button_offlineMapSetting_confirm:
                if (Objects.equals(editTextOfflineMapSettingMapName.getText(), null) ||
                    Objects.equals(editTextOfflineMapSettingMapName.getText().toString(), "")){
                    Toast.makeText(getContext(), "請輸入地圖名稱", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0 ; i < zoomLeveList.length ; i ++){
                    if (zoomLeveList[i]){
                        break;
                    }else if (i == zoomLeveList.length -1 /* && !zoomLeveList.get(zoomLeveList.size()-1)*/){
                        Toast.makeText(getContext(), "請至少選擇一個縮放層級", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putParcelableArrayListExtra("LAT_LNG_LIST" , (ArrayList<? extends Parcelable>) latLngList);
                intent.putExtra("OFFLINE_MAP_NAME", editTextOfflineMapSettingMapName.getText().toString());
                intent.putExtra("ZOOM_LEVEL_LIST", zoomLeveList);
                getActivity().setResult(RESULT_OK, intent);
                getActivity().finish();
                break;
        }
    }
}
