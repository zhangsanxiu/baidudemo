package com.zwh.baidudemo;

import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherActivity extends AppCompatActivity {

    private EditText inputCity = null;
    private Button searchBtn = null;
    private TextView weatherView = null;
    JSONObject data = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        inputCity = (EditText) findViewById(R.id.inputCity);
        searchBtn = (Button) findViewById(R.id.search);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputCity.getText().toString() != null){
                    Log.d("WeatherActivity","begin request weather infos of " + inputCity.getText().toString());
                    getJSON(inputCity.getText().toString());
                }
            }
        });
        weatherView = (TextView) findViewById(R.id.weatherView);

    }

    public void getJSON(final String city) {

        new AsyncTask<Void, Void, Void>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... params) {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q="+city+"&APPID=f2b7d8d2dc87226713e2d191c8e08851&lang=zh");
                    connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuffer json = new StringBuffer(1024);
                    String tmp = "";
                    while((tmp = reader.readLine()) != null) {
                        json.append(tmp).append("\n");
                        Log.d("WeatherActivity",tmp);
                    }
                    reader.close();

                    data = new JSONObject(json.toString());
                    Log.d("WeatherActivity",data.toString(4));

                } catch (Exception e) {
                    Log.d("WeatherActivity",e.toString());
                } finally {
                    if(connection!=null){
                        connection.disconnect();
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void Void) {
                if(data!=null){
                    try {
                        Log.d("WeatherActivity",data.toString(4));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    updateWeather();
                }

            }
        }.execute();

    }
    public void updateWeather(){
        StringBuffer sb = new StringBuffer(256);
        if (data != null){
            try {
                sb.append("城市：" + data.getString("name") + "\n");
                sb.append("经度：" + data.getJSONObject("coord").getString("lon")
                        + "\n纬度：" + data.getJSONObject("coord").getString("lat") + "\n");
                sb.append("天气：" + data.getJSONArray("weather").getJSONObject(0).getString("description") + "\n");
                sb.append("温度：" + (data.getJSONObject("main").getDouble("temp") + -273.15) + "℃ \n");
                sb.append("压强：" + data.getJSONObject("main").getString("pressure") + "hPa\n");
                sb.append("湿度：" + data.getJSONObject("main").getString("humidity") + "%\n");
                sb.append("风力：" + data.getJSONObject("wind").getString("speed") +"级" + "\n");
                sb.append("风向：" + data.getJSONObject("wind").getString("deg") +"级" + "\n");
            }catch (JSONException e){
                e.printStackTrace();
            }
            if (weatherView != null && sb.length() > 0){
                weatherView.setText(sb);
            }
        }
    }
}
