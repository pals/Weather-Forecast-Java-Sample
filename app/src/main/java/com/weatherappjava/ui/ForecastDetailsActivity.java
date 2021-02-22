package com.weatherappjava.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;

import com.lantern.ml.weatherappjava.R;
import com.lantern.ml.weatherappjava.databinding.ActivityForecastDetailsBinding;
import com.weatherappjava.models.ItemHourly;
import com.weatherappjava.viewmodels.WeatherForecastViewModel;
import com.weatherappjava.viewmodels.WeatherForecastViewModelFactory;

import static com.weatherappjava.utils.Constants.APP_LOGGER_TAG;

public class ForecastDetailsActivity extends AppCompatActivity {

    private WeatherForecastViewModel weatherForecastViewModel;
    private ActivityForecastDetailsBinding activityForecastDetailsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityForecastDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_forecast_details);
        setSupportActionBar(activityForecastDetailsBinding.toolbarLayout.findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WeatherForecastViewModelFactory weatherForecastViewModelFactory = new WeatherForecastViewModelFactory(WeatherForecastViewModel.getInstance());
        weatherForecastViewModel = ViewModelProviders.of(this, weatherForecastViewModelFactory).get(WeatherForecastViewModel.class);
        weatherForecastViewModel.getSelectedForecastDetails().observe(this, itemHourly -> {

            Log.d(APP_LOGGER_TAG, "Selected forecast city: " + itemHourly.getDtTxt());
            Long temp = Math.round(itemHourly.getMain().getTemp());
            activityForecastDetailsBinding.tvTemperature.setText(temp + "\u00B0F");

            Long feelLike = Math.round(itemHourly.getMain().getFeelsLikeTemp());
            activityForecastDetailsBinding.tvFeelsLike.setText(getResources().getString(R.string.feel_like_str) + " " + feelLike + "\u00B0F");
            activityForecastDetailsBinding.tvWeather.setText(itemHourly.getWeather().get(0).getMain());
            activityForecastDetailsBinding.tvWeatherDetails.setText(itemHourly.getWeather().get(0).getDescription());
        });

        weatherForecastViewModel.getSelectedCity().observe(this, s -> activityForecastDetailsBinding.tvSelectedCityName.setText(weatherForecastViewModel.getSelectedCity().getValue()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}