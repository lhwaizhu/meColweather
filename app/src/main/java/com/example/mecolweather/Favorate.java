package com.example.mecolweather;
public class Favorate
{
    String cityName;
    String updateTime;
    String degree;
    String weatherInfo;
    String weatherId;

    public void setWeatherInfo(String weatherInfo) {
        this.weatherInfo = weatherInfo;
    }

    public String getWeatherInfo() {
        return weatherInfo;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public String getDegree() {
        return degree;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getWeatherId() {
        return weatherId;
    }
}
