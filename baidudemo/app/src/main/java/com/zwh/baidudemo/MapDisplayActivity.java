package com.zwh.baidudemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MapDisplayActivity extends AppCompatActivity {

    private MapView mMapView = null;
    private BaiduMap bdMap =null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_map_display);
        init();
        LocationReceiver receiver = new LocationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastUtil.LOCATIONUPDATE);
        registerReceiver(receiver,filter);
    }

    public void init(){
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.showZoomControls(false);

        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        bdMap = mMapView.getMap();
        bdMap.setMapStatus(msu);
    }

    @Override
    protected void onDestroy() {
        if (mMapView != null) {
            mMapView.onDestroy();
            mMapView = null;
        }

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class LocationReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Double latitude = intent.getDoubleExtra(BroadcastUtil.LOCATIONLATITUDE,0.0f);
            Double longitude = intent.getDoubleExtra(BroadcastUtil.LOCATIONLONGITUDE,0.0f);
            MyLocationData locData = new MyLocationData.Builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();
            bdMap.setMyLocationData(locData);
            LatLng latLng = new LatLng(latitude, longitude);
            MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
            bdMap.animateMapStatus(msu);
            Log.e("zhangwenhao","location receiver");
        }
    }
    private MyLocationData getLocationData(double latitude, double longitude ){
        MyLocationData locData = new MyLocationData.Builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();



        return locData;
    }
}
