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
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity
{
    private Button backButton;
    private Switch bk_Switch;
    private Button albumButton;
    private Boolean switchCheck;
    SharedPreferences.Editor   editor;
    public static final int CHOOSE_PHOTO=2;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        switchCheck=sharedPreferences.getBoolean("update_pic",true);
        backButton=(Button)findViewById(R.id.backToWeather_button);
        bk_Switch=(Switch)findViewById(R.id.bk_switch);
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
}
