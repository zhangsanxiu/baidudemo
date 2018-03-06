package com.zwh.baidudemo;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.district.DistrictSearch;
import com.baidu.mapapi.search.district.DistrictSearchOption;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import org.w3c.dom.Text;

import java.security.PrivateKey;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,OnGetGeoCoderResultListener{

    GeoCoder mGeoCoder = null;
    GeoCodeOption mGeoCodeOption = null;

    private TextView viewLocation = null;
    private Button getLocation = null;

    private LinearLayout networkLocationView = null;
    private LinearLayout gpsLocationView = null;

    private EditText latitudeText = null;
    private EditText longitudeText = null;
    private TextView locationResult =null;
    private Button searchLocation = null;

    private EditText cityNameText = null;
    private TextView cityResult = null;
    private Button searchCity = null;

    private TextView viewGpsLocation = null;
    private Button getGpsLocation = null;
    private Button recordeData = null;
    private Button countData = null;

    private LinearLayout errorView = null;
    private TextView errorText =null;
    private Button mTestButton = null;
    private int mInit = 0;

    private String[] PERMISSION_REQUIRED = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_PHONE_STATE};

    private String mLocationDetails = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        if (verifyStoragePermissions()){
            initComponents();
            startBDlocationService();
        }
        MyReceiver receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BroadcastUtil.LOCATIONUPDATE);
        registerReceiver(receiver,filter);
    }
    private void startBDlocationService(){
        startService(new Intent(MainActivity.this,BaiduLocationService.class));
    }

    private void initComponents() {
        networkLocationView = (LinearLayout) findViewById(R.id.networkLocation);
        networkLocationView.setVisibility(View.GONE);
        gpsLocationView = (LinearLayout) findViewById(R.id.gpsLocation);
        gpsLocationView.setVisibility(View.GONE);

        viewLocation = (TextView) findViewById(R.id.viewLocation);
        viewLocation.setMovementMethod(ScrollingMovementMethod.getInstance());
        getLocation = (Button) findViewById(R.id.getLocation);
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocation();
            }
        });
        //getLocation.setVisibility(View.GONE);

        latitudeText = (EditText) findViewById(R.id.latitude);
        longitudeText = (EditText) findViewById(R.id.longitude);
        locationResult = (TextView) findViewById(R.id.locationResult);
        searchLocation = (Button) findViewById(R.id.searchLocation);
        searchLocation.setOnClickListener(this);

        cityNameText = (EditText) findViewById(R.id.cityName);
        cityResult = (TextView) findViewById(R.id.cityResult);
        searchCity = (Button) findViewById(R.id.searchCity);
        searchCity.setOnClickListener(this);

        viewGpsLocation = (TextView) findViewById(R.id.viewGpsLocation) ;
        viewGpsLocation.setMovementMethod(ScrollingMovementMethod.getInstance());
        getGpsLocation = (Button) findViewById(R.id.getGpsLocation) ;
        getGpsLocation.setOnClickListener(this);
        getGpsLocation.setVisibility(View.GONE);
        recordeData = (Button) findViewById(R.id.recordeData) ;
        recordeData.setOnClickListener(this);
        countData = (Button) findViewById(R.id.countData) ;
        countData.setOnClickListener(this);

        errorView = (LinearLayout) findViewById(R.id.errorView);
        errorText = (TextView) findViewById(R.id.errorText);
        errorText.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTestButton = (Button) findViewById(R.id.testButton);
        mTestButton.setOnClickListener(this);
        mInit =1;
    }

    private void updateLocation(){
        if(mLocationDetails != null){
            viewLocation.setText(mLocationDetails);
        }
    }

    public boolean verifyStoragePermissions() {
        int permission_result = 0;
        for (String permission:PERMISSION_REQUIRED) {
            permission_result +=  ActivityCompat.checkSelfPermission(getApplicationContext(), permission);
        }
        if (permission_result != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSION_REQUIRED, 100);

        }
        return permission_result == 0 ? true:false;
    }

    @Override
    public void onClick(View v) {
        Log.e("zhangwenhao","onClick clicked");
        switch (v.getId()){
            case R.id.getLocation:
                Log.e("zhangwenhao","getLocation clicked");

                break;
            case R.id.searchLocation:
                showLocationCity();
                break;
            case R.id.searchCity:
                Log.e("zhangwenhao","searchCity clicked");
                showCityLocation();
                break;
            case R.id.getGpsLocation:

                break;
            case R.id.recordeData:
                startRecordeData();
                break;
            case R.id.countData:
                getCountData();
                break;
            case R.id.testButton:
                getCountData();
                break;

            default:
                break;
        }
    }

    private void getCountData() {
        Intent intent = new Intent();
        intent.setAction(BroadcastUtil.LOCATIONCOUNT);
        sendBroadcast(intent);
    }

    private void testButton(){
        /*Notification notification = new Notification.Builder(getApplicationContext())
                .setOnlyAlertOnce(true)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setVibrate(new long[]{200})
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        NotificationManager mNotificationMgr = (NotificationManager)getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationMgr.notify(-1000000,notification);*/
    }

    private void startRecordeData() {
        Intent intent = new Intent();
        intent.setAction(BroadcastUtil.LOCATIONSAVE);
        sendBroadcast(intent);
    }

    private void showCityLocation() {

        if (cityNameText.getText() == null){
            showErrorMessage("cityNameText.getText() == null");
            return;
        }
        String cityName = cityNameText.getText().toString();
        showErrorMessage(cityName);
        String[] cityAddress = cityName.split(" ");
        if (cityAddress.length !=2){
            showErrorMessage("city and address and space");
            return;
        }
        cityResult.setText("Requesting...");
        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(this);
        mGeoCoder.geocode(new GeoCodeOption().city(cityAddress[0]).address(cityAddress[1]));
        mGeoCoder.destroy();

    }

    private void showLocationCity() {

        if (latitudeText.getText() == null || longitudeText.getText() == null){
            showErrorMessage("cityNameText.getText() == null");
            return;
        }
        locationResult.setText("Requesting...");
        String latitude = latitudeText.getText().toString();
        String longitude = longitudeText.getText().toString();
        mGeoCoder = GeoCoder.newInstance();
        mGeoCoder.setOnGetGeoCodeResultListener(this);
        mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude))));
        mGeoCoder.destroy();
    }

    private void showErrorMessage(String string){
        //Log.e("zhangwenhao",string);
        //Toast.makeText(this, string,Toast.LENGTH_LONG).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int result = 0;
        String perssionNotice = "";
        int i = 0;
        for(int grantResult : grantResults){
            result +=  grantResult;
            if (grantResult == -1){
                perssionNotice += permissions[i] + "  ";
            }
            i++;
        }
        if (result != 0){
            Toast.makeText(this,"Required perssions:\n " + perssionNotice,Toast.LENGTH_SHORT).show();
            finish();
        }else {
            initComponents();
            startBDlocationService();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("zhangwenhao","onReceive broadcast " + mInit );
            if (mInit == 0){
                Toast.makeText(MainActivity.this,"init",Toast.LENGTH_LONG).show();
            }
            else if (intent.getAction() == BroadcastUtil.LOCATIONUPDATE){
                mLocationDetails = intent.getStringExtra(BroadcastUtil.LOCATIONDETAILS);
                int locationType = intent.getIntExtra(BroadcastUtil.LOCATIONTYPE,0);
                Log.e("zhangwenhao","onReceive broadcast " + locationType );
                Log.e("zhangwenhao","onReceive broadcast " + mLocationDetails );

                if (mLocationDetails != null && locationType != 0){
                    if (locationType == BDLocation.TypeNetWorkLocation){
                        gpsLocationView.setVisibility(View.GONE);
                        errorView.setVisibility(View.GONE);
                        networkLocationView.setVisibility(View.VISIBLE);
                        viewLocation.setText(mLocationDetails);
                    }else if(locationType == BDLocation.TypeGpsLocation){
                        networkLocationView.setVisibility(View.GONE);
                        errorView.setVisibility(View.GONE);
                        gpsLocationView.setVisibility(View.VISIBLE);
                        viewGpsLocation.setText(mLocationDetails);
                    }else{
                        networkLocationView.setVisibility(View.GONE);
                        gpsLocationView.setVisibility(View.GONE);
                        errorView.setVisibility(View.VISIBLE);
                        errorText.setText(mLocationDetails);
                    }
                }
            }
        }
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        if (cityResult != null) {
            if (geoCodeResult.error != SearchResult.ERRORNO.NO_ERROR){
                cityResult.setText(geoCodeResult.error.toString());
            }else {
                cityResult.setText(geoCodeResult.getLocation().toString());
            }
        }
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (locationResult != null) {
            if (reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR){
                locationResult.setText(reverseGeoCodeResult.error.toString());
            }else {
                locationResult.setText(reverseGeoCodeResult.getAddress());
            }
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(MainActivity.this,BaiduLocationService.class));
        Log.e("zhangwenhao","onDestroy stop");
        super.onDestroy();
    }
}










































