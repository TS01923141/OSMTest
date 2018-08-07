package com.example.tyc.osmtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.tyc.osmtest.data.OfflineMapData;
import com.example.tyc.osmtest.utils.AlertProgressDialog;
import com.example.tyc.osmtest.utils.GoogleMapOfflineFileController;

import java.io.File;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

import static com.example.tyc.osmtest.MapsActivity.GOOGLE_MAP;
import static com.example.tyc.osmtest.MapsActivity.OPEN_STREET_MAP;
import static com.example.tyc.osmtest.MapsActivity.RUDY_MAP;
import static com.example.tyc.osmtest.MapsActivity.WMTS_MAP;

/**
 * Created by biji on 2018/7/27.
 */

public class OfflineMapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "OfflineMapAdapter";

    private List<OfflineMapData> offlineMapData;
    private Activity activity;

    public OfflineMapAdapter(List<OfflineMapData> offlineMapData, Activity activity){
        this.offlineMapData = offlineMapData;
        this.activity = activity;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout_offlinemap_frame;
        TextView textView_offlinemap_mapName;
        TextView textView_offlinemap_mapType;
        TextView textView_offlinemap_mapSize;
        Button button_offlinemap_deleteMap;
        public ViewHolder(View itemView) {
            super(itemView);
            constraintLayout_offlinemap_frame = itemView.findViewById(R.id.constraintLayout_offlinemap_frame);
            textView_offlinemap_mapName = itemView.findViewById(R.id.textView_offlinemap_mapName);
            textView_offlinemap_mapType = itemView.findViewById(R.id.textView_offlinemap_mapType);
            textView_offlinemap_mapSize = itemView.findViewById(R.id.textView_offlinemap_mapSize);
            button_offlinemap_deleteMap = itemView.findViewById(R.id.button_offlinemap_deleteMap);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_offline_map, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, final int position) {
        ViewHolder holder = (ViewHolder) h;
        holder.textView_offlinemap_mapName.setText(offlineMapData.get(position).getName());
        holder.textView_offlinemap_mapSize.setText(offlineMapData.get(position).getFileSize());
        switch (offlineMapData.get(position).getMapType()){
            case GOOGLE_MAP:
                holder.textView_offlinemap_mapType.setText("GoogleMap");
                break;
            case OPEN_STREET_MAP:
                holder.textView_offlinemap_mapType.setText("OSM");
                break;
            case RUDY_MAP:
                holder.textView_offlinemap_mapType.setText("魯地圖");
                break;
            case WMTS_MAP:
                holder.textView_offlinemap_mapType.setText("經建三");
                break;
        }
        holder.constraintLayout_offlinemap_frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("OFFLINE_MAP_FILE_NAME",offlineMapData.get(position).getName());
                intent.putExtra("MAP_TYPE", offlineMapData.get(position).getMapType());
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            }
        });
        holder.button_offlinemap_deleteMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File offlineMapFile = new File(offlineMapData.get(position).getFilePath());
                if (offlineMapFile.exists()){
                    AlertDialog alertDialog = AlertProgressDialog.create(activity, "請稍等", "刪除中...", false);
                    Completable.fromAction(new Action() {
                        @Override
                        public void run() throws Exception {
                           alertDialog.show();
                            Log.d(TAG, "run: show");
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(Schedulers.newThread())
                            .andThen(Completable.fromAction(new Action() {
                                @Override
                                public void run() throws Exception {
                                    Log.d(TAG, "run: do delete");
                                    GoogleMapOfflineFileController.deleteDir(offlineMapFile);
                                }
                            }))
                            .observeOn(AndroidSchedulers.mainThread())
                            .andThen(Completable.fromAction(new Action() {
                                @Override
                                public void run() throws Exception {
                                    Log.d(TAG, "run: do refresh and close dialog");
                                    refresh();
                                    alertDialog.dismiss();
                                }
                            })).subscribe();
//                    offlineMapFile.delete();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return offlineMapData.size();
    }

    public void setOfflineMapData(List<OfflineMapData> offlineMapData){
        this.offlineMapData = offlineMapData;
        notifyDataSetChanged();
    }

    private void refresh(){
        offlineMapData = GoogleMapOfflineFileController.getOfflineMapData();
        notifyDataSetChanged();
    }
}
