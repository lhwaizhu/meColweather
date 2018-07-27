package com.example.mecolweather;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mecolweather.db.FavorateDb;
import com.example.mecolweather.gson.Forecast;
import com.example.mecolweather.gson.Weather;
import com.example.mecolweather.service.AutoUpdateService;
import com.example.mecolweather.util.HForecastAdapter;
import com.example.mecolweather.util.HttpUtil;
import com.example.mecolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private ImageView nowImage;
    public SwipeRefreshLayout swipeRefreshLayout;
    public String weatherIdd;
    public DrawerLayout drawerLayout;
    private Button navButton;
    private Button settingButton;
    private RecyclerView recyclerView;
    public Boolean update;
    public String imagePath;
    public SharedPreferences sharedPreferences;
    public Button likeView;
    public Boolean backGround;
    static public List<Favorate> favorateList;
    static public FavorateAdapter favorateAdapter;
    private List<FavorateDb> favorateDbList;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
       // Log.d("曹操","操死你create");
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
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navButton=(Button)findViewById(R.id.nav_button);
        nowImage=(ImageView)findViewById(R.id.now_image);
        settingButton=(Button)findViewById(R.id.setting_button);
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        likeView=(Button)findViewById(R.id.like);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString=sharedPreferences.getString("weather",null);
        String bingPic=sharedPreferences.getString("bing_pic",null);
        update=sharedPreferences.getBoolean("update_pic",true);
        imagePath=sharedPreferences.getString("album",null);
        backGround=sharedPreferences.getBoolean("background",true);
        favorateList=new ArrayList<>();
        favorateDbList=DataSupport.findAll(FavorateDb.class);
        if(favorateDbList.size()>0)
        {
          //  favorateList.clear();
            for(FavorateDb favorateDb:favorateDbList)
            {
                Favorate favorate=new Favorate();
                favorate.setCityName(favorateDb.getCityName());
                favorate.setDegree(favorateDb.getDegree());
                favorate.setUpdateTime(favorateDb.getUpdateTime());
                favorate.setWeatherId(favorateDb.getWeatherId());
                favorate.setWeatherInfo(favorateDb.getWeatherInfo());
                favorateList.add(favorate);
            }
        }
        favorateAdapter=new FavorateAdapter(this,R.layout.favorate_item,
                WeatherActivity.favorateList);

        if(backGround)
        {
           // Log.d("曹操","create backGround");
            Glide.with(this).load(R.drawable.backgorund).into(bingPicImg);
        }
        else {
            if (update) {
                if (bingPic != null) {
                    Glide.with(this).load(bingPic).into(bingPicImg);
                } else {
                    loadBingPic();
                }
            } else {
                if (imagePath == null)
                    Glide.with(this).load(bingPic).into(bingPicImg);
                else
                    Glide.with(this).load(imagePath).into(bingPicImg);
            }
        }
        if(weatherString!=null)
        {
            //Log.d("曹操create",weatherString);

            Weather weather= Utility.handleWeatherResponse(weatherString);
           // Log.d("曹操","create"+weather.basic.weatherId);
            weatherIdd=weather.basic.weatherId;
            showWeatherInto(weather);
        }
        else
        {
            weatherIdd=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherIdd);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                requestWeather(weatherIdd);
            }
        });
        navButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                drawerLayout.openDrawer(Gravity.START);
            }
        });
        settingButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent=new Intent(WeatherActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });
        likeView.setOnClickListener(new View.OnClickListener()
        {
           // Boolean flag=false; //每次点击直接进入onClick 不会再执行这一句

            @Override
            public void onClick(View view)
            {
              //  Log.d("曹操",favorateList.size()+"");
                Boolean flag=true;
                sharedPreferences= PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
                String weatherString=sharedPreferences.getString("weather",null);
                Weather weather= Utility.handleWeatherResponse(weatherString);
                for (Favorate favorate : favorateList) {
                    if (weather.basic.cityName.equals(favorate.cityName)) {

                        likeView.setBackgroundResource(R.drawable.unlike);
                        favorateList.remove(favorate);//如果已经收藏过了 这里点击就是移除
                        //adapter.notify 通知收藏的list更新
                        favorateAdapter.notifyDataSetChanged();
                        flag = false;
                        break; }
                }
                if(flag)
                {
                    Favorate favorate = new Favorate();
                    favorate.cityName = weather.basic.cityName;
                    favorate.degree = weather.now.temperature;
                    favorate.updateTime = weather.basic.update.updateTime;
                    favorate.weatherInfo = weather.now.more.info;
                    favorate.weatherId=weather.basic.weatherId;
                    likeView.setBackgroundResource(R.drawable.like);
                    favorateList.add(favorate);
                    //adapter.notify 通知收藏的list更新
                    favorateAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        DataSupport.deleteAll(FavorateDb.class);
        for(Favorate favorate:favorateList)
        {
            FavorateDb favorateDb=new FavorateDb();
            favorateDb.setCityName(favorate.getCityName());
            favorateDb.setDegree(favorate.getDegree());
            favorateDb.setWeatherId(favorate.getWeatherId());
            favorateDb.setUpdateTime(favorate.getUpdateTime());
            favorateDb.setWeatherInfo(favorate.getWeatherInfo());
            favorateDb.save();
        }
    }

        @Override
        protected void onResume()
        {
            super.onResume();
            //Log.d("曹操","操死你resume");

            sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
            String weatherString=sharedPreferences.getString("weather",null);
            String bingPic=sharedPreferences.getString("bing_pic",null);
            update=sharedPreferences.getBoolean("update_pic",true);
            imagePath=sharedPreferences.getString("album",null);
            backGround=sharedPreferences.getBoolean("background",true);
            if(backGround)
            {
               // Log.d("曹操","resume backGround");
                Glide.with(this).load(R.drawable.backgorund).into(bingPicImg);
            }
            else {
                if (update) {
                    if (bingPic != null) {
                        Glide.with(this).load(bingPic).into(bingPicImg);
                    } else {
                        loadBingPic();
                    }
                } else {
                    if (imagePath == null)
                        Glide.with(this).load(bingPic).into(bingPicImg);
                    else
                        Glide.with(this).load(imagePath).into(bingPicImg);
                }
            }
            if(weatherString!=null)
            {
                //Log.d("曹操resume",weatherString);
                Weather weather= Utility.handleWeatherResponse(weatherString);
               // Log.d("曹操","resume"+weather.basic.weatherId);
                weatherIdd=weather.basic.weatherId;
                showWeatherInto(weather);
            }
            else
            {
                weatherIdd=getIntent().getStringExtra("weather_id");
                weatherLayout.setVisibility(View.INVISIBLE);
                requestWeather(weatherIdd);
            }

        }

    public void requestWeather(final String weatherId)
    {
/*
        String weatherUrl="http://guolin.tech/api/weather?cityid=" +
                weatherId+"&key=bc0418b57b2d4918819d3974ac1285d9";
           */
        String weatherUrl="http://guolin.tech/api/weather?cityid=" +
                weatherId+"&key=6632e52493e945b6b70ef5b811b83bef";
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
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseText=response.body().string();
                final Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable()
                {
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
                            weatherIdd=weather.basic.weatherId;
                            showWeatherInto(weather);
                        }
                        else
                        {
                            //Log.d("天气活动",weather.status);
                            Toast.makeText(WeatherActivity.this,"获取天气数据失败",Toast.LENGTH_LONG)
                                    .show();
                        }
                     swipeRefreshLayout.setRefreshing(false);
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
                /*
                runOnUiThread(new Runnable(){

                    @Override
                    public void run()
                    {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
                */
            }
        });
    }
    private void showWeatherInto(Weather weather)
    {
        likeView.setBackgroundResource(R.drawable.unlike);
        for (Favorate favorate : favorateList)
        {
            if (weather.basic.cityName.equals(favorate.cityName))
            {
                likeView.setBackgroundResource(R.drawable.like);
                break;
            }
        }
        HForecastAdapter hForecastAdapter=new HForecastAdapter(weather.hForecastList);
        recyclerView.setAdapter(hForecastAdapter);
        String cityName=weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime.split(" ")[1];
       // Log.d("曹操",weather.basic.update.updateTime);

        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        switch(weatherInfo)
        {
            case "晴":
                    nowImage.setImageResource(R.drawable.qing);
                    break;
            case "多云":
                    nowImage.setImageResource(R.drawable.duoyun);
                    break;
            case "小雨":
            case "中雨":
                    nowImage.setImageResource(R.drawable.xiaoyu);
                    break;
            case "大雨":
                    nowImage.setImageResource(R.drawable.dayu);
                    break;
            case "阴":
                    nowImage.setImageResource(R.drawable.yin);
                    break;
            case "雷阵雨":
            case "雷雨":
            case "暴雨":
                    nowImage.setImageResource(R.drawable.leizhenyu);
                    break;
            default:nowImage.setImageResource(R.mipmap.logo);
                    break;
        }
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
            ImageView forecastItemImage=(ImageView)view.findViewById(R.id.forecast_item_image);
            switch(forecast.more.info)
            {
                case "晴":
                    forecastItemImage.setImageResource(R.drawable.qing);
                    break;
                case "多云":
                    forecastItemImage.setImageResource(R.drawable.duoyun);
                    break;
                case "阴":
                    forecastItemImage.setImageResource(R.drawable.yin);
                    break;
                case "小雨":
                case "中雨":
                    forecastItemImage.setImageResource(R.drawable.xiaoyu);
                    break;
                case "大雨":
                    forecastItemImage.setImageResource(R.drawable.dayu);
                    break;
                case "雷阵雨":
                case "雷雨":
                case "暴雨":
                    forecastItemImage.setImageResource(R.drawable.leizhenyu);
                    break;
                default:
                    forecastItemImage.setImageResource(R.mipmap.logo);
                    break;
            }
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max+"℃");
            minText.setText(forecast.temperature.min+"℃");
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
        Intent intent=new Intent(this, AutoUpdateService.class);
        startService(intent);
    }
}
