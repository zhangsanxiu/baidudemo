package com.zwh.baidudemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class BaiduLocationService extends Service {
    private BaiduLocation baiduLocation = null;
    private boolean threadDisable = false;
    private boolean saveLocationData = true;

    private BaiduLocationDao baiduLocationDao = null;
    public BaiduLocationService() {
    }

    private Handler MyHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private Thread updateUIThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!threadDisable) {
                Log.e("zhangwenhao", "sendBroadcast broadcast " + "test");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (baiduLocation != null) {
                    String locationDetails = baiduLocation.getLocationDetails();
                    int locationType = baiduLocation.getLocationType();
                    Intent intent = new Intent();
                    intent.putExtra(BroadcastUtil.LOCATIONDETAILS, locationDetails);
                    intent.putExtra(BroadcastUtil.LOCATIONTYPE, locationType);
                    intent.putExtra(BroadcastUtil.LOCATIONLATITUDE, baiduLocation.getLocationLatitude());
                    intent.putExtra(BroadcastUtil.LOCATIONLONGITUDE, baiduLocation.getLocationLongitude());
                    intent.setAction(BroadcastUtil.LOCATIONUPDATE);
                    sendBroadcast(intent);
                    Log.e("zhangwenhao", "sendBroadcast broadcast " + locationType);

                }
            }
        }
    });
    @Override
    public void onCreate() {
        super.onCreate();
        baiduLocation = new BaiduLocation(BaiduLocationService.this);
        baiduLocation.initBaiduLocation();
        baiduLocationDao = new BaiduLocationDao(BaiduLocationService.this);
        MyReceiver receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastUtil.LOCATIONSAVE);
        filter.addAction(BroadcastUtil.LOCATIONSTOP);
        filter.addAction(BroadcastUtil.LOCATIONCOUNT);
        filter.addAction(BroadcastUtil.LOCATIONDRAW);
        registerReceiver(receiver,filter);
        updateUIThread.start();
    }

    @Override
    public void onDestroy() {
        threadDisable = true;
        saveLocationData = false;
        Log.e("zhangwenhao","onDestory 111");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String broadcastType = intent.getAction();
            if (broadcastType.equals(BroadcastUtil.LOCATIONSAVE)){
                saveLocationData();
            }
            else if (broadcastType.equals(BroadcastUtil.LOCATIONCOUNT)){
                countLocationData();
            }
            else if (broadcastType.equals(BroadcastUtil.LOCATIONDRAW)){
                //drawLocationData();
            }
        }
    }

    private void drawLocationData() {
        threadDisable =true;
    }

    private void countLocationData() {
        saveLocationData =false;
        BaiduLocationDao mDao = new BaiduLocationDao(BaiduLocationService.this);
        int countNum = mDao.getLocationsCount();
        Toast.makeText(this,countNum + " items have been saved!",Toast.LENGTH_LONG).show();
    }

    private void stopLocationData() {
        saveLocationData = false;
    }

    private void saveLocationData() {
        saveLocationData = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (saveLocationData) {
                    Log.e("zhangwenhao", "saveLocationData ");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (baiduLocation != null) {
                        String locationDetails = baiduLocation.getLocationData();
                        String[] lat_lon = locationDetails.split(",");
                        double latitude = Double.parseDouble(lat_lon[0]);
                        double longitude = Double.parseDouble(lat_lon[1]);
                        BaiduLocationDao mDao = new BaiduLocationDao(BaiduLocationService.this);
                        mDao.addLocation(latitude,longitude);
                    }
                }
                Log.e("zhangwenhao", "saveLocationData stop");

            }
        }).start();
    }


}
