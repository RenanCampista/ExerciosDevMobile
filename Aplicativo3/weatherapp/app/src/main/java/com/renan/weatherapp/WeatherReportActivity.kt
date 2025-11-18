package com.renan.weatherapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.renan.weatherapp.model.WeatherResponse
import kotlin.math.roundToInt

class WeatherReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_report)

        // Receber os dados da intent
        val weatherData = intent.getSerializableExtra("WEATHER_DATA") as? WeatherResponse

        // Validar se os dados foram recebidos corretamente
        if (weatherData == null) {
            Toast.makeText(this, getString(R.string.error_invalid_data), Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Referências das views
        val ivBack = findViewById<ImageView>(R.id.iv_back)
        val ivWeatherIcon = findViewById<ImageView>(R.id.iv_weather_icon)
        val tvCityName = findViewById<TextView>(R.id.tv_city_name)
        val tvWeatherCondition = findViewById<TextView>(R.id.tv_weather_condition)
        val tvCurrentTemp = findViewById<TextView>(R.id.tv_current_temp)
        val tvDescription = findViewById<TextView>(R.id.tv_description)
        val tvTempMax = findViewById<TextView>(R.id.tv_temp_max)
        val tvTempMin = findViewById<TextView>(R.id.tv_temp_min)
        val tvFeelsLike = findViewById<TextView>(R.id.tv_feels_like)
        val tvWindSpeed = findViewById<TextView>(R.id.tv_wind_speed)
        val tvHumidity = findViewById<TextView>(R.id.tv_humidity)

        // Botão de voltar
        ivBack.setOnClickListener {
            finish()
        }

        // Preencher os dados com tratamento de erros
        try {
            tvCityName.text = weatherData.name.ifEmpty { getString(R.string.error_invalid_data) }

            // Validar se a lista de weather não está vazia
            if (weatherData.weather.isNotEmpty()) {
                tvWeatherCondition.text = weatherData.weather[0].main
                tvDescription.text = weatherData.weather[0].description

                // Definir a imagem do clima baseada na condição
                val condition = weatherData.weather[0].main.lowercase()
                val weatherIconRes = when {
                    condition.contains("cloud") -> R.drawable.ic_cloud
                    condition.contains("rain") -> R.drawable.ic_rain
                    condition.contains("clear") -> R.drawable.ic_sun
                    else -> R.drawable.ic_cloud
                }
                ivWeatherIcon.setImageResource(weatherIconRes)
            }

            // Preencher dados de temperatura com valores seguros
            tvCurrentTemp.text = "${weatherData.main.temp.roundToInt()}°C"
            tvTempMax.text = "${weatherData.main.tempMax.roundToInt()}°C"
            tvTempMin.text = "${weatherData.main.tempMin.roundToInt()}°C"
            tvFeelsLike.text = "${weatherData.main.feelsLike.roundToInt()}°C"
            tvWindSpeed.text = "${weatherData.wind.speed.roundToInt()} km/h"
            tvHumidity.text = "${weatherData.main.humidity}%"

        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.error_invalid_data), Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
