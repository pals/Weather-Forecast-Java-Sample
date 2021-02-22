package com.weatherappjava.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.lantern.ml.weatherappjava.R;
import com.weatherappjava.models.ItemHourly;
import com.weatherappjava.viewmodels.WeatherForecastViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.weatherappjava.utils.Constants.APP_LOGGER_TAG;

public class ForecastListAdapter extends RecyclerView.Adapter<ForecastListAdapter.ForecastListAdapterHolder> {

    private final ArrayList<ItemHourly> hourlyItems;
    private final LayoutInflater layoutInflater;
    private final WeatherForecastViewModel weatherForecastViewModel;
    private Context context;

    public ForecastListAdapter(Context context, ArrayList<ItemHourly> hourlyItems, WeatherForecastViewModel weatherForecastViewModel) {
        this.context = context;
        this.hourlyItems = hourlyItems;
        layoutInflater = LayoutInflater.from(context);
        this.weatherForecastViewModel = weatherForecastViewModel;
    }

    @NonNull
    @Override
    public ForecastListAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate an item view.
        View mItemView = layoutInflater.inflate(
                R.layout.forecast_row, parent, false);
        return new ForecastListAdapterHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastListAdapterHolder holder, int position) {

        AppCompatTextView tvDate = holder.actvDate;
        AppCompatTextView tvWeather = holder.actvWeather;
        AppCompatTextView tvTemperature = holder.actvTemperature;

        ItemHourly itemHourly = hourlyItems.get(position);
        Log.d("WeatherApp", "Date: " + itemHourly.getDt());
        
        DateFormat incomingDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date incomingDate = null;
        try {
            incomingDate = incomingDateFormatter.parse(itemHourly.getDtTxt());
            DateFormat formatter = new SimpleDateFormat("EEE, MMM d h:mm a");
            String formatted = formatter.format(incomingDate);
            Log.d(APP_LOGGER_TAG, "formatted date:  " + formatted);
            tvDate.setText(formatted);

        } catch (ParseException e) {
            e.printStackTrace();
            tvDate.setText("");
        }

        String weather = "";
        if (itemHourly.getWeather() != null) {
            weather = itemHourly.getWeather().get(0).getMain();
        }

        tvWeather.setText(weather);
        Long temp = Math.round(itemHourly.getMain().getTemp());
        tvTemperature.setText(temp + "\u00B0F");
    }

    @Override
    public int getItemCount() {
        return hourlyItems.size();
    }

    class ForecastListAdapterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final AppCompatTextView actvDate;
        public final AppCompatTextView actvWeather;
        public final AppCompatTextView actvTemperature;
        final ForecastListAdapter forecastListAdapter;

        public ForecastListAdapterHolder(View itemView, ForecastListAdapter adapter) {
            super(itemView);
            forecastListAdapter = adapter;
            actvDate = itemView.findViewById(R.id.actv_date);
            actvWeather = itemView.findViewById(R.id.actv_weather);
            actvTemperature = itemView.findViewById(R.id.actv_temperature);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(APP_LOGGER_TAG, "forecast row clicked " + getLayoutPosition());
            ItemHourly itemHourly = hourlyItems.get(getLayoutPosition());
            weatherForecastViewModel.setSelectedForecast(itemHourly);
            Log.d(APP_LOGGER_TAG, "itemHourly clicked details : " + itemHourly.getClouds());
            Intent intent = new Intent();
            intent.setClass(context, ForecastDetailsActivity.class);
            context.startActivity(intent);
        }
    }
}
