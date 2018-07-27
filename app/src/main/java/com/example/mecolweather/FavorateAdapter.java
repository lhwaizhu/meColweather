package com.example.mecolweather;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FavorateAdapter extends ArrayAdapter<Favorate>
{
    private int resourceId;
    public FavorateAdapter(Context context, int textViewResourceId, List<Favorate> objects)
    {
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }
    class ViewHolder
    {
        TextView cityName;
        TextView cityDegree;
        TextView cityWeatherInfo;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        Favorate favorate=getItem(position);
       // View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        View view;
        ViewHolder viewHolder;
        if(convertView==null)
        {
            view=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.cityName=(TextView)view.findViewById(R.id.favorate_ctity_name);
            viewHolder.cityDegree=(TextView)view.findViewById(R.id.favorate_degree);
            viewHolder.cityWeatherInfo=(TextView)view.findViewById(R.id.favorate_weatherInfo);
            view.setTag(viewHolder);
        }
        else
        {
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        /*
        TextView cityName=(TextView)view.findViewById(R.id.favorate_ctity_name);
        TextView cityDegree=(TextView)view.findViewById(R.id.favorate_degree);
        TextView cityWeatherInfo=(TextView)view.findViewById(R.id.favorate_weatherInfo);
        cityName.setText(favorate.getCityName());
        cityDegree.setText(favorate.getDegree());
        cityWeatherInfo.setText(favorate.getWeatherInfo());
        */
        viewHolder.cityWeatherInfo.setText(favorate.getWeatherInfo());
        viewHolder.cityDegree.setText(favorate.getDegree()+"℃");
        viewHolder.cityName.setText(favorate.getCityName());
        return view;
    }
}
