package com.example.mecolweather.gson;

import com.google.gson.annotations.SerializedName;

public class HForecast
{
    @SerializedName("cond_txt")
    public String weatherInfo;
    public String time;
    public String tmp;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getWeatherInfo() {
        return weatherInfo;
    }

    public void setWeatherInfo(String weatherInfo) {
        this.weatherInfo = weatherInfo;
    }
}
