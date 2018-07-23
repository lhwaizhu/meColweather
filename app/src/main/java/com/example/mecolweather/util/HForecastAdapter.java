package com.example.mecolweather.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mecolweather.R;
import com.example.mecolweather.gson.HForecast;
import com.example.mecolweather.gson.Weather;

import java.util.List;

public class HForecastAdapter extends RecyclerView.Adapter<HForecastAdapter.ViewHolder>
{
    private List<HForecast> mHForecastList;

    public HForecastAdapter(List<HForecast> mHForecastList)
    {
        this.mHForecastList=mHForecastList;
    }
    static class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView hForecastImage;
        TextView hForecastTmp;
        TextView hForecastInfo;
        TextView hForecastTime;
        public ViewHolder(View view)
        {
            super(view);
            hForecastImage=(ImageView)view.findViewById(R.id.hforecast_image);
            hForecastTmp=(TextView)view.findViewById(R.id.hforecast_tmp);
            hForecastInfo=(TextView)view.findViewById(R.id.hforecast_info);
            hForecastTime=(TextView)view.findViewById(R.id.hforecast_time);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view= LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.hforecast_item,viewGroup,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i)
    {
        HForecast hForecast=mHForecastList.get(i);
        viewHolder.hForecastTime.setText(hForecast.getTime().split(" ")[1]);
        viewHolder.hForecastInfo.setText(hForecast.getWeatherInfo());
        viewHolder.hForecastTmp.setText(hForecast.getTmp()+"℃");
        //下面需要switch判断
     //   viewHolder.hForecastImage.setImageResource(R.drawable.yin);
        switch(hForecast.getWeatherInfo())
        {
            case "晴":
                viewHolder.hForecastImage.setImageResource(R.drawable.qing);
                break;
            case "多云":
                viewHolder.hForecastImage.setImageResource(R.drawable.duoyun);
                break;
            case "阴":
                viewHolder.hForecastImage.setImageResource(R.drawable.yin);
                break;
            case "小雨":
            case "中雨":
                viewHolder.hForecastImage.setImageResource(R.drawable.xiaoyu);
                break;
            case "大雨":
                viewHolder.hForecastImage.setImageResource(R.drawable.dayu);
                break;
            case "雷阵雨":
            case "雷雨":
            case "暴雨":
                viewHolder.hForecastImage.setImageResource(R.drawable.leizhenyu);
                break;
            default:
                viewHolder.hForecastImage.setImageResource(R.mipmap.logo);
                break;
        }
    }

    @Override
    public int getItemCount()
    {
        return mHForecastList.size();
    }
}
