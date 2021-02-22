package com.weatherappjava.service;

import com.weatherappjava.models.WeatherForecastResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("forecast")
    Single<WeatherForecastResponse> getFiveDaysWeather(
            @Query("q") String q,
            @Query("units") String units,
            @Query("lang") String lang,
            @Query("appid") String appId
    );
}


