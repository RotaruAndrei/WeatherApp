package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView city,tempValue,weatherValue,humidity,maxTemp,minTemp,pressure,wind;
    private ImageView weatherImg;
    private FloatingActionButton floatBtn;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private double lat,lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUi();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                lat = location.getLatitude();
                lon = location.getLongitude();

                Log.e("location: lat = ", String.valueOf(lat));
                Log.e("location: lon = ", String.valueOf(lon));

                getWeatherData(lat,lon);
            }
        };


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,300,50,locationListener);
        }


        floatBtn.setOnClickListener(onClick ->{
            Intent intent = new Intent(MainActivity.this,WeatherActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,300,50,locationListener);
        }
    }

    private void initUi(){
        city         = findViewById(R.id.main_activity_cityTV);
        tempValue    = findViewById(R.id.main_activity_temperature_tempValueTV);
        weatherValue = findViewById(R.id.main_activity_temperature_weatherInfoTV);
        weatherImg   = findViewById(R.id.main_activity_details_image);
        humidity     = findViewById(R.id.main_activity_details_title_humidity_value);
        maxTemp      = findViewById(R.id.main_activity_details_title_maxTemp_value);
        minTemp      = findViewById(R.id.main_activity_details_title_minTemp_value);
        pressure     = findViewById(R.id.main_activity_details_title_pressure_value);
        wind         = findViewById(R.id.main_activity_details_title_wind_value);
        floatBtn     = findViewById(R.id.main_floatBtn);
    }

    public void getWeatherData (double lat, double lon){

        WeatherAPI weatherAPI = RetrofitWeather.getRetrofit().create(WeatherAPI.class);
        Call<WeatherModelMap> call = weatherAPI.getWeatherInfoWithLocation(lat,lon);

        //call the data asyncrounously (in the background)
        call.enqueue(new Callback<WeatherModelMap>() {
            @Override
            public void onResponse(Call<WeatherModelMap> call, Response<WeatherModelMap> response) {

                city.setText(response.body().getName() + ", " + response.body().getSys().getCountry());
                tempValue.setText(getCelsius(response.body().getMain().getTemp()) + " °C");
                weatherValue.setText(response.body().getWeather().get(0).getDescription());
                humidity.setText(response.body().getMain().getHumidity() + "%");
                maxTemp.setText(getCelsius(response.body().getMain().getTempMax()) + " °C");
                minTemp.setText(getCelsius(response.body().getMain().getTempMin()) + " °C");
                pressure.setText(response.body().getMain().getPressure().doubleValue() + " %");
                wind.setText(""+response.body().getWind().getSpeed());

                String iconCode = response.body().getWeather().get(0).getIcon();

                Glide.with(MainActivity.this)
                        .load("https://openweathermap.org/img/wn/"+iconCode+"@2x.png")
                        .centerCrop()
                        .into(weatherImg);
            }

            @Override
            public void onFailure(Call<WeatherModelMap> call, Throwable t) {

            }
        });
    }

    private int getCelsius(double kelvin){
        return (int)(kelvin - 273.15);
    }
}