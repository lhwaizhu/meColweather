package com.example.mecolweather;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.example.mecolweather.gson.Weather;
import com.example.mecolweather.util.HttpUtil;
import com.example.mecolweather.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SettingActivity extends AppCompatActivity
{
    private Button backButton;
    private Switch bk_Switch;
    private Button albumButton;
    private Boolean switchCheck;
    private Switch backgroundSwitch;
    private Boolean backgroundCheck;
    private SharedPreferences.Editor   editor;
    public ProgressBar settingProgressBar;
    //static public FavorateAdapter favorateAdapter;

    public static final int CHOOSE_PHOTO=2;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        switchCheck=sharedPreferences.getBoolean("update_pic",false);
        backgroundCheck=sharedPreferences.getBoolean("background",true);
        backButton=(Button)findViewById(R.id.backToWeather_button);
        bk_Switch=(Switch)findViewById(R.id.bk_switch);
        backgroundSwitch=(Switch)findViewById(R.id.background_switch);
        settingProgressBar=(ProgressBar)findViewById(R.id.setting_progress_bar);
        ListView listView=(ListView)findViewById(R.id.list_view);
        listView.setAdapter(WeatherActivity.favorateAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                String weatherId=null;
                /*
                String cityName=WeatherActivity.favorateList.get(i).getCityName();
                for(Favorate favorate:WeatherActivity.favorateList)
                {
                    if(cityName.equals(favorate.getCityName()))
                    {
                        weatherId=favorate.weatherId;
                        break;
                    }
                }
                */
               // Log.d("曹操",WeatherActivity.favorateList.get(i).getCityName());
                //Log.d("曹操",WeatherActivity.favorateList.get(i).getWeatherId());
                //weatherId=WeatherActivity.favorateList.get(i).getWeatherId();
                //requestWeather(weatherId);
                //finish();
                settingProgressBar.setVisibility(View.VISIBLE);
                requestWeather(WeatherActivity.favorateList.get(i).getWeatherId());
            }
        });
        if(backgroundCheck)
        {
            backgroundSwitch.setChecked(true);
        }
        else
        {
            backgroundSwitch.setChecked(false);
        }
        if(switchCheck) {
            bk_Switch.setChecked(true);
        }
        else
        {
            bk_Switch.setChecked(false);
        }
        albumButton=(Button)findViewById(R.id.album);
        albumButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                if(ContextCompat.checkSelfPermission(SettingActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(SettingActivity.this,new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                else
                {
                    openAlbum();
                    bk_Switch.setChecked(false);
                    backgroundSwitch.setChecked(false);
                    editor=PreferenceManager.
                            getDefaultSharedPreferences(SettingActivity.this).edit();
                    editor.putBoolean("update_pic",false);
                    editor.apply();

                }
            }
        });
        bk_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if(b)
                {
                    editor=PreferenceManager.
                            getDefaultSharedPreferences(SettingActivity.this).edit();
                    editor.putBoolean("update_pic",true);
                    editor.apply();
                    backgroundSwitch.setChecked(false);
                }
                else
                {
                    editor=PreferenceManager.
                            getDefaultSharedPreferences(SettingActivity.this).edit();
                    editor.putBoolean("update_pic",false);
                    editor.apply();
                }

            }
        });
        backgroundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                if(b)
                {
                    editor=PreferenceManager.
                            getDefaultSharedPreferences(SettingActivity.this).edit();
                    editor.putBoolean("background",true);
                    editor.apply();
                    bk_Switch.setChecked(false);
                }
                else
                {
                    editor=PreferenceManager.
                            getDefaultSharedPreferences(SettingActivity.this).edit();
                    editor.putBoolean("background",false);
                    editor.apply();
                }

            }
        });
        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }

    private void openAlbum()
    {
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch(requestCode)
        {
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    openAlbum();
                }
                else
                {
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        switch (requestCode)
        {
            case CHOOSE_PHOTO:
                if(resultCode==RESULT_OK)
                {
                    if(Build.VERSION.SDK_INT>=19)
                    {
                        handleImageOnKitKat(data);
                    }
                    else
                    {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data)
    {
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri))
        {
            String docId=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority()))
            {
                String id=docId.split(":")[1];
                String selection= MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority()))
            {
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }
        else if("content".equalsIgnoreCase(uri.getScheme()))
        {
            imagePath=getImagePath(uri,null);
        }
        else if("file".equalsIgnoreCase(uri.getScheme()))
        {
            imagePath=uri.getPath();
        }
        editor=PreferenceManager.
                getDefaultSharedPreferences(SettingActivity.this).edit();
        editor.putString("album",imagePath);
        editor.apply();
    }

    private void handleImageBeforeKitKat(Intent data)
    {
        Uri uri=data.getData();
        String imagePath=getImagePath(uri,null);
        editor=PreferenceManager.
                getDefaultSharedPreferences(SettingActivity.this).edit();
        editor.putString("album",imagePath);
        editor.apply();
    }
    private  String getImagePath(Uri uri,String selection)
    {
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null)
        {
            if(cursor.moveToFirst())
            {
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
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
                runOnUiThread(new Runnable(){

                    @Override
                    public void run()
                    {
                        Toast.makeText(SettingActivity.this,"获取天气数据失败",Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseText=response.body().string();
                final Weather weather= Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(weather!=null&&"ok".equals(weather.status))
                        {
                             editor=PreferenceManager.
                                    getDefaultSharedPreferences(SettingActivity.this)
                                    .edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            //Log.d("曹操","我写进去了");
                            settingProgressBar.setVisibility(View.GONE);
                            finish();
                        }
                        else
                        {
                            //Log.d("天气活动",weather.status);
                            Toast.makeText(SettingActivity.this,"获取天气数据失败",Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
            }
        });
    }
}
