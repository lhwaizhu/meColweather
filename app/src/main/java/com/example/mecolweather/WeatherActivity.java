package com.example.mecolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mecolweather.gson.Forecast;
import com.example.mecolweather.gson.Weather;
import com.example.mecolweather.util.HttpUtil;
import com.example.mecolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity
{
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21)
        {
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView)findViewById(R.id.title_update_time);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        aqiText=(TextView)findViewById(R.id.aqi_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        comfortText=(TextView)findViewById(R.id.comfort_text);
        carWashText=(TextView)findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);
        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);

        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=sharedPreferences.getString("weather",null);
        String bingPic=sharedPreferences.getString("bing_pic",null);
        if(bingPic!=null)
        {
           // Log.d("卧槽","刘备");
           // Log.d("卧槽",bingPic);
            Glide.with(this).load(bingPic).into(bingPicImg);
        }
        else
        {
          //  Log.d("卧槽","曹操");
            loadBingPic();
        }
        if(weatherString!=null)
        {
            Weather weather= Utility.handleWeatherResponse(weatherString);
            showWeatherInto(weather);
        }
        else
        {
            String weatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }
    public void requestWeather(final String weatherId)
    {
        String weatherUrl="http://guolin.tech/api/weather?cityid=" +
                weatherId+"&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e)
            {
                //Log.d("WeatherActivity","直接失败");
                runOnUiThread(new Runnable(){

                    @Override
                    public void run()
                    {
                        Toast.makeText(WeatherActivity.this,"获取天气数据失败",Toast.LENGTH_LONG)
                        .show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseText=response.body().string();
                final Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable(){

                    @Override
                    public void run()
                    {
                        if(weather!=null&&"ok".equals(weather.status))
                        {
                            SharedPreferences.Editor editor=PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInto(weather);
                        }
                        else
                        {
                            //Log.d("天气活动",weather.status);
                            Toast.makeText(WeatherActivity.this,"获取天气数据失败",Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
            }
        });
        loadBingPic();
    }
    private void loadBingPic()
    {
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e)
            {
                    e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String bingPic=response.body().string();
               // Log.d("活动",bingPic);
                SharedPreferences.Editor editor=PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable(){

                    @Override
                    public void run()
                    {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
    private void showWeatherInto(Weather weather)
    {
        String cityName=weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime.split(" ")[1];
     //   Log.d("活动",weather.basic.update.updateTime);
       // Log.d("活动",weather.basic.update.updateTime.split(" ")[0]);
        //Log.d("活动",weather.basic.update.updateTime.split(" ")[1]);
        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList)
        {
            View view= LayoutInflater.from(WeatherActivity.this).inflate
                    (R.layout.forecast_item,forecastLayout,false);
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if(weather.aqi!=null)
        {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort="舒适度:"+weather.suggestion.comfort.info;
        String carWash="洗车指数"+weather.suggestion.carWash.info;
        String sport="运动指数"+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }



}