package com.example.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {

    //get the data with call method on user location
    @GET("weather?lat=45.66&lon=25.61&appid=5a8e7226b0b8d43aedae224c6f7a3705&unit=metric")
    Call<WeatherModelMap>getWeatherInfoWithLocation(@Query("lat")double lat, @Query("lon")double lon);

    //get the data with call method on user city input
    @GET("weather?lat=45.66&lon=25.61&appid=5a8e7226b0b8d43aedae224c6f7a3705&unit=metric")
    Call<WeatherModelMap>getWeatherInfoWithCity(@Query("q")String name);
}
