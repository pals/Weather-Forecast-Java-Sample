package com.weatherappjava.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.weatherappjava.models.WeatherForecastResponse;
import com.weatherappjava.models.ItemHourly;
import com.weatherappjava.service.ApiService;
import com.weatherappjava.service.ApiClient;
import com.weatherappjava.utils.Constants;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class WeatherForecastViewModel extends ViewModel {

    private ApiService apiService;

    private MutableLiveData<WeatherForecastResponse> fiveDayResponseMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<ItemHourly> itemHourlyMutableLiveData = new MutableLiveData<>();
    private static WeatherForecastViewModel myViewModel;
    private MutableLiveData<String> cityLiveData = new MutableLiveData<>();


    public WeatherForecastViewModel() {
        Log.d(Constants.APP_LOGGER_TAG, "WeatherForecastViewModel");
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public static synchronized WeatherForecastViewModel getInstance() {
        if (myViewModel == null) {
            myViewModel = new WeatherForecastViewModel();
            return myViewModel;
        }

        return myViewModel;
    }

    public MutableLiveData<WeatherForecastResponse> getFiveDayResponseMutableLiveData() {
        if (fiveDayResponseMutableLiveData == null) {
            fiveDayResponseMutableLiveData = new MutableLiveData<>();
        }
        return fiveDayResponseMutableLiveData;
    }

    public void setSelectedCity(String city) {
        cityLiveData.setValue(city);
    }

    public MutableLiveData<String> getSelectedCity() {
        return cityLiveData;
    }

    public void setSelectedForecast(ItemHourly itemHourly) {
        itemHourlyMutableLiveData.setValue(itemHourly);
    }

    public MutableLiveData<ItemHourly> getSelectedForecastDetails() {
        return itemHourlyMutableLiveData;
    }

    public DisposableSingleObserver<WeatherForecastResponse> fetchWeatherForecast(String etCityName) {

        return apiService.getFiveDaysWeather(etCityName, Constants.UNITS, Constants.DEFAULT_LANG, Constants.API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<WeatherForecastResponse>() {
                    @Override
                    public void onSuccess(WeatherForecastResponse response) {
                        Log.d(Constants.APP_LOGGER_TAG, "response: " + response.getCity().getName());
                        Log.d(Constants.APP_LOGGER_TAG, "hourly items size : " + response.getList().size());

                        fiveDayResponseMutableLiveData.setValue(response);
                    }


                    @Override
                    public void onError(Throwable e) {

                        e.printStackTrace();
                        Log.d(Constants.APP_LOGGER_TAG, "response: " + e.getMessage());
                        fiveDayResponseMutableLiveData.setValue(null);
                    }
                });
    }

}
