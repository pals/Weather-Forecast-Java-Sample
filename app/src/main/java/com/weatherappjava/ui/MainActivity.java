package com.weatherappjava.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.lantern.ml.weatherappjava.R;
import com.lantern.ml.weatherappjava.databinding.ActivityMainBinding;
import com.weatherappjava.models.ItemHourly;
import com.weatherappjava.service.ApiService;
import com.weatherappjava.service.ApiClient;
import com.weatherappjava.viewmodels.WeatherForecastViewModel;
import com.weatherappjava.viewmodels.WeatherForecastViewModelFactory;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;

import static com.weatherappjava.utils.Constants.APP_LOGGER_TAG;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    private CompositeDisposable disposable = new CompositeDisposable();

    private static String etCityName;
    private ForecastListAdapter forecastListAdapter;
    private ArrayList<ItemHourly> itemHourlies;
    private WeatherForecastViewModel weatherForecastViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.btnLookUp.setOnClickListener(new LookUpButtonClick());


        initUIAndValues();
    }

    private void initUIAndValues() {

        setSupportActionBar(activityMainBinding.toolbarLayout.findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        itemHourlies = new ArrayList<>();
        WeatherForecastViewModelFactory weatherForecastViewModelFactory = new WeatherForecastViewModelFactory(WeatherForecastViewModel.getInstance());
        weatherForecastViewModel = ViewModelProviders.of(this, weatherForecastViewModelFactory).get(WeatherForecastViewModel.class);


        forecastListAdapter = new ForecastListAdapter(MainActivity.this, itemHourlies, weatherForecastViewModel);
        activityMainBinding.rvForecast.setAdapter(forecastListAdapter);
        activityMainBinding.rvForecast.setLayoutManager(new LinearLayoutManager(this));
        activityMainBinding.rvForecast.addItemDecoration(new DividerItemDecoration(activityMainBinding.rvForecast.getContext(), DividerItemDecoration.VERTICAL));

        // Add observer for city name. Previously stored city name is persisted.
        weatherForecastViewModel.getSelectedCity().observe(MainActivity.this, s -> activityMainBinding.etSearchCity.setText(s));

        // Add observer for API response
        weatherForecastViewModel.getFiveDayResponseMutableLiveData().observe(MainActivity.this, weatherForecastResponse -> {
            itemHourlies.clear();
            forecastListAdapter.notifyDataSetChanged();

            if (weatherForecastResponse != null) {
                if (weatherForecastResponse.getCity() != null) {
                    weatherForecastViewModel.setSelectedCity(weatherForecastResponse.getCity().getName());
                } else {
                    weatherForecastViewModel.setSelectedCity("");
                }
                itemHourlies.addAll(weatherForecastResponse.getList());
                forecastListAdapter.notifyDataSetChanged();
            } else {
                Log.d(APP_LOGGER_TAG, "fiveDayResponse value null as error in API call");
                itemHourlies.clear();
                forecastListAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "No city found! Please enter valid city name", Toast.LENGTH_SHORT).show();
            }


        });

    }

    private class LookUpButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.d(APP_LOGGER_TAG, "Lookup button clicked");

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);

            etCityName = activityMainBinding.etSearchCity.getText().toString();
            Log.d(APP_LOGGER_TAG, "city name: " + etCityName);
            if (etCityName.equals("")) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.empty_city), Toast.LENGTH_SHORT).show();
            } else {
                Log.d(APP_LOGGER_TAG, "weatherForecastViewModel: " + weatherForecastViewModel);
                disposable.add(weatherForecastViewModel.fetchWeatherForecast(etCityName));
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}