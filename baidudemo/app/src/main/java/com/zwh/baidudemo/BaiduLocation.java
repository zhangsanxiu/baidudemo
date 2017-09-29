package com.zwh.baidudemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.util.Log;
import android.view.View;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

/**
 * Created by zwh on 17-9-15.
 */

public class BaiduLocation {
    private Context mContext;
    private LocationClient mLocationClient = null;
    private BDLocation mBdLocation = null;
    private MyLocationListener myListener = null;
    private GeoCoder mGeocoder = null;

    private GeoCodeResult geoCoderResult = null;
    private ReverseGeoCodeResult geoCoderReverseResult = null;

    private String[] PERMISSION_REQUIRED = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_PHONE_STATE};

    public BaiduLocation(Context context){
        mContext = context;
        mLocationClient = new LocationClient(context);

    }

    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.e("zhangwenhao",this.getClass().toString() + "onReceiveLocation");
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                mBdLocation = location;
                if (location.getLocType() == BDLocation.TypeGpsLocation){
                    //networkLocationView.setVisibility(View.GONE);
                    //gpsLocationView.setVisibility(View.VISIBLE);
                    //errorView.setVisibility(View.GONE);
                }else if(location.getLocType() == BDLocation.TypeNetWorkLocation){
                    //networkLocationView.setVisibility(View.VISIBLE);
                    //gpsLocationView.setVisibility(View.GONE);
                    //errorView.setVisibility(View.GONE);
                }else {
                    //networkLocationView.setVisibility(View.GONE);
                    //gpsLocationView.setVisibility(View.GONE);
                    //errorView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public LocationClientOption getLocationClientOption() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        int span = 1000;
        option.setScanSpan(span);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        option.setIgnoreKillProcess(false);
        option.setEnableSimulateGps(false);
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        return option;
    }

    public String getLocationDetails(){
        if (mBdLocation != null && mBdLocation.getLocType() == BDLocation.TypeNetWorkLocation){
            return getNetworkLocationDetails();
        }else if (mBdLocation != null && mBdLocation.getLocType() == BDLocation.TypeGpsLocation){
            return getGpsLocationDetails();
        }else{
            return "Please ensure GPS or network is avaliable! \n" + getErrorLocationDetails();
        }
    }
    public String getNetworkLocationDetails(){
        StringBuffer sb = new StringBuffer(256);
        if (null != mBdLocation && mBdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
            sb.append("time : ");
            /**
             * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
             * mBdLocation.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
             */
            sb.append(mBdLocation.getTime());
            sb.append("\nlocType : ");// 定位类型
            sb.append(mBdLocation.getLocType());
            sb.append("\nlocType description : ");// *****对应的定位类型说明*****
            sb.append(mBdLocation.getLocTypeDescription());
            sb.append("\nlatitude : ");// 纬度
            sb.append(mBdLocation.getLatitude());
            sb.append("\nlontitude : ");// 经度
            sb.append(mBdLocation.getLongitude());
            sb.append("\nradius : ");// 半径
            sb.append(mBdLocation.getRadius());
            sb.append("\nCountryCode : ");// 国家码
            sb.append(mBdLocation.getCountryCode());
            sb.append("\nCountry : ");// 国家名称
            sb.append(mBdLocation.getCountry());
            sb.append("\ncitycode : ");// 城市编码
            sb.append(mBdLocation.getCityCode());
            sb.append("\ncity : ");// 城市
            sb.append(mBdLocation.getCity());
            sb.append("\nDistrict : ");// 区
            sb.append(mBdLocation.getDistrict());
            sb.append("\nStreet : ");// 街道
            sb.append(mBdLocation.getStreet());
            sb.append("\naddr : ");// 地址信息
            sb.append(mBdLocation.getAddrStr());
            sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
            sb.append(mBdLocation.getUserIndoorState());
            sb.append("\nDirection(not all devices have value): ");
            sb.append(mBdLocation.getDirection());// 方向
            sb.append("\nlocationdescribe: ");
            sb.append(mBdLocation.getLocationDescribe());// 位置语义化信息
            sb.append("\nPoi: ");// POI信息
            if (mBdLocation.getPoiList() != null && !mBdLocation.getPoiList().isEmpty()) {
                for (int i = 0; i < mBdLocation.getPoiList().size(); i++) {
                    Poi poi = (Poi) mBdLocation.getPoiList().get(i);
                    sb.append(poi.getName() + ";");
                }
            }
            // 网络定位结果
            // 运营商信息
            if (mBdLocation.hasAltitude()) {// *****如果有海拔高度*****
                sb.append("\nheight : ");
                sb.append(mBdLocation.getAltitude());// 单位：米
            }
            sb.append("\noperationers : ");// 运营商信息
            sb.append(mBdLocation.getOperators());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");

            /*} else if (mBdLocation.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (mBdLocation.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (mBdLocation.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (mBdLocation.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }*/
            //viewLocation.setText(sb);
        }
        return sb.toString();
    }

    public String getGpsLocationDetails(){
        StringBuffer sb = new StringBuffer(256);
        if (null != mBdLocation && mBdLocation.getLocType() == BDLocation.TypeGpsLocation) {
            sb.append("time : ");
            /**
             * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
             * mBdLocation.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
             */
            sb.append(mBdLocation.getTime());
            sb.append("\nlocType : ");// 定位类型
            sb.append(mBdLocation.getLocType());
            sb.append("\nlocType description : ");// *****对应的定位类型说明*****
            sb.append(mBdLocation.getLocTypeDescription());
            sb.append("\nlatitude : ");// 纬度
            sb.append(mBdLocation.getLatitude());
            sb.append("\nlontitude : ");// 经度
            sb.append(mBdLocation.getLongitude());
            sb.append("\nradius : ");// 半径
            sb.append(mBdLocation.getRadius());

            sb.append("\nDirection(not all devices have value): ");
            sb.append(mBdLocation.getDirection());// 方向

            sb.append("\nspeed : ");
            sb.append(mBdLocation.getSpeed());// 速度 单位：km/h
            sb.append("\nsatellite : ");
            sb.append(mBdLocation.getSatelliteNumber());// 卫星数目
            sb.append("\nheight : ");
            sb.append(mBdLocation.getAltitude());// 海拔高度 单位：米
            sb.append("\ngps status : ");
            sb.append(mBdLocation.getGpsAccuracyStatus());// *****gps质量判断*****
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

        }else {
            sb.append("");
        }
        return sb.toString();
    }

    public String getErrorLocationDetails(){
        StringBuffer sb = new StringBuffer(256);
        if (null != mBdLocation ) {
            sb.append("Solutions:\n");
            sb.append("1.Open location service.\n");
            sb.append("2.Move phone outdoor.\n");
            sb.append("3.Connect to one network.\n");

        }
        return sb.toString();
    }
    public void initBaiduLocation(){
        mLocationClient.setLocOption(getLocationClientOption());
        mLocationClient.registerLocationListener(new MyLocationListener());
        mGeocoder = GeoCoder.newInstance();
        mGeocoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                geoCoderResult = geoCodeResult;
                Intent intent = new Intent();
                intent.setAction(BroadcastUtil.GEOCODER);
                mContext.sendBroadcast(intent);
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                geoCoderReverseResult = reverseGeoCodeResult;
                Intent intent = new Intent();
                intent.setAction(BroadcastUtil.REVERSEGEOCODER);
                mContext.sendBroadcast(intent);
            }
        });
        mLocationClient.start();
    }
    public int getLocationType(){
        if (mBdLocation == null){
            return BDLocation.TypeNone;
        }
        return mBdLocation.getLocType();
    }
    public String getLocationData(){
        return mBdLocation.getLatitude() + "," + mBdLocation.getLongitude();
    }

    public double getLocationLatitude(){
        if (mBdLocation != null) {
            return mBdLocation.getLatitude();
        }else{
            return 0.0f;
        }
    }
    public double getLocationLongitude(){
        if (mBdLocation != null){
            return mBdLocation.getLongitude();
        }else {
            return 0.0f;
        }
    }


}
