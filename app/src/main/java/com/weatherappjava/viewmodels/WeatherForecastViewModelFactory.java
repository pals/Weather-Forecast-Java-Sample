package com.weatherappjava.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class WeatherForecastViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    WeatherForecastViewModel weatherForecastViewModel;
    private final Map<Class<? extends ViewModel>, ViewModel> mFactory = new HashMap<>();

    public WeatherForecastViewModelFactory(WeatherForecastViewModel weatherForecastViewModel) {
        this.weatherForecastViewModel = weatherForecastViewModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        mFactory.put(modelClass, weatherForecastViewModel);

        if (WeatherForecastViewModel.class.isAssignableFrom(modelClass)) {
            WeatherForecastViewModel shareVM = null;

            if (mFactory.containsKey(modelClass)) {
                shareVM = (WeatherForecastViewModel) mFactory.get(modelClass);
            } else {
                try {
                    shareVM = (WeatherForecastViewModel) modelClass.getConstructor(Runnable.class).newInstance(new Runnable() {
                        @Override
                        public void run() {
                            mFactory.remove(modelClass);
                        }
                    });
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                } catch (InstantiationException e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                }
                mFactory.put(modelClass, shareVM);
            }

            return (T) shareVM;
        }
        return super.create(modelClass);

    }

}
