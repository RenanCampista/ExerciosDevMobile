package com.renan.weatherapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.renan.weatherapp.util.Resource
import com.renan.weatherapp.viewmodel.WeatherViewModel

class MainActivity : AppCompatActivity() {

    private val vm: WeatherViewModel by viewModels()
    private lateinit var etCity: EditText
    private lateinit var btnSearch: Button
    private lateinit var ivWeather: ImageView
    private lateinit var tvTemp: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etCity = findViewById(R.id.et_city)
        btnSearch = findViewById(R.id.btn_search)
        ivWeather = findViewById(R.id.iv_weather)
        tvTemp = findViewById(R.id.tv_temp)
        progressBar = findViewById(R.id.progress_bar)

        btnSearch.setOnClickListener {
            val city = etCity.text.toString().trim()
            vm.fetchWeather(city)
        }

        vm.weatherState.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                    tvTemp.text = getString(R.string.loading)
                }
                is Resource.Success -> {
                    showLoading(false)
                    resource.data?.let { weatherData ->
                        // Navegar para a tela de relatório detalhado
                        val intent = Intent(this, WeatherReportActivity::class.java)
                        intent.putExtra("WEATHER_DATA", weatherData)
                        startActivity(intent)

                        // Limpar o estado após navegar
                        vm.clearWeatherState()
                        tvTemp.text = getString(R.string.app_name)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    val errorMessage = getErrorMessage(resource.message)
                    tvTemp.text = errorMessage
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
                null -> {
                    showLoading(false)
                    tvTemp.text = getString(R.string.app_name)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnSearch.isEnabled = !isLoading
        etCity.isEnabled = !isLoading
    }

    private fun getErrorMessage(errorKey: String?): String {
        return when (errorKey) {
            "error_empty_city" -> getString(R.string.error_empty_city)
            "error_city_not_found" -> getString(R.string.error_city_not_found)
            "error_no_internet" -> getString(R.string.error_no_internet)
            "error_timeout" -> getString(R.string.error_timeout)
            "error_server" -> getString(R.string.error_server)
            "error_invalid_data" -> getString(R.string.error_invalid_data)
            else -> getString(R.string.error_generic)
        }
    }
}