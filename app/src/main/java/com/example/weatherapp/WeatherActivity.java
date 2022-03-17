package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherActivity extends AppCompatActivity {

    private EditText citySearch;
    private TextView city, tempValue, weatherInfo, humidity, maxTemp, minTemp, pressure, wind;
    private ImageView imageView;
    private Button searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        initUi();

        searchBtn.setOnClickListener(onClick ->{

            String userInput = citySearch.getText().toString();

            if (!userInput.isEmpty()){

                getWeatherData(userInput);
                citySearch.setText("");
            }else {
                Toast.makeText(this, "Please enter a city", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUi(){
        citySearch  = findViewById(R.id.weather_activity_cityEdit);
        city        = findViewById(R.id.weather_activity_cityTV);
        tempValue   = findViewById(R.id.weather_activity_temperature_tempValueTV);
        weatherInfo = findViewById(R.id.weather_activity_temperature_weatherInfoTV);
        humidity    = findViewById(R.id.weather_activity_details_title_humidity_value);
        maxTemp     = findViewById(R.id.weather_activity_details_title_maxTemp_value);
        minTemp     = findViewById(R.id.weather_activity_details_title_minTemp_value);
        pressure    = findViewById(R.id.weather_activity_details_title_pressure_value);
        wind        = findViewById(R.id.weather_activity_details_title_wind_value);
        imageView   = findViewById(R.id.weather_activity_details_image);
        searchBtn   = findViewById(R.id.weather_activity_searchBtn);
    }

    public void getWeatherData (String cityName){

        WeatherAPI weatherAPI = RetrofitWeather.getRetrofit().create(WeatherAPI.class);
        Call<WeatherModelMap> call = weatherAPI.getWeatherInfoWithCity(cityName);

        //call the data asyncrounously (in the background)
        call.enqueue(new Callback<WeatherModelMap>() {
            @Override
            public void onResponse(Call<WeatherModelMap> call, Response<WeatherModelMap> response) {

                if (response.isSuccessful()){

                    city.setText(response.body().getName() + ", " + response.body().getSys().getCountry());

                    tempValue.setText(getCelsius(response.body().getMain().getTemp()) +" °C");
                    weatherInfo.setText(response.body().getWeather().get(0).getDescription());
                    humidity.setText(response.body().getMain().getHumidity() + "%");
                    maxTemp.setText(getCelsius(response.body().getMain().getTempMax()) + " °C");
                    minTemp.setText(getCelsius(response.body().getMain().getTempMin()) + " °C");
                    pressure.setText(response.body().getMain().getPressure().doubleValue() + " %");
                    wind.setText(""+response.body().getWind().getSpeed());

                    String iconCode = response.body().getWeather().get(0).getIcon();

                    Glide.with(WeatherActivity.this)
                            .load("https://openweathermap.org/img/wn/"+iconCode+"@2x.png")
                            .centerCrop()
                            .into(imageView);
                }else {

                    Toast.makeText(WeatherActivity.this, "City has not been found, please try again", Toast.LENGTH_SHORT).show();
                }

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