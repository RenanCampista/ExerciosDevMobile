package com.renan.weatherapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.renan.weatherapp.model.WeatherResponse
import com.renan.weatherapp.network.WeatherService
import com.renan.weatherapp.repository.WeatherRepository
import com.renan.weatherapp.util.NetworkUtil
import com.renan.weatherapp.util.Resource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class WeatherViewModel(app: Application) : AndroidViewModel(app) {

    private val _weatherState = MutableLiveData<Resource<WeatherResponse>>()
    val weatherState: LiveData<Resource<WeatherResponse>> = _weatherState

    private val service: WeatherService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WeatherService::class.java)
    }

    private val repo by lazy { WeatherRepository(service) }

    fun fetchWeather(city: String) {
        // Validar se a cidade não está vazia
        if (city.trim().isEmpty()) {
            _weatherState.postValue(Resource.Error("error_empty_city"))
            return
        }

        // Verificar conectividade antes de fazer a requisição
        if (!NetworkUtil.isNetworkAvailable(getApplication())) {
            _weatherState.postValue(Resource.Error("error_no_internet"))
            return
        }

        // Indicar estado de carregamento
        _weatherState.postValue(Resource.Loading())

        viewModelScope.launch {
            try {
                val result = repo.getWeather(city, "apiKey")

                when (result) {
                    is Resource.Success -> {
                        Log.d("WeatherViewModel", "Resposta recebida: ${result.data?.name}")
                        result.data?.let { resp ->
                            Log.d("WeatherViewModel", "Temp: ${resp.main.temp}, Min: ${resp.main.tempMin}, Max: ${resp.main.tempMax}")
                            Log.d("WeatherViewModel", "Feels Like: ${resp.main.feelsLike}, Humidity: ${resp.main.humidity}")
                            Log.d("WeatherViewModel", "Wind Speed: ${resp.wind.speed}")
                        }
                    }
                    is Resource.Error -> {
                        Log.e("WeatherViewModel", "Erro ao buscar clima: ${result.message}")
                    }
                    is Resource.Loading -> {
                        Log.d("WeatherViewModel", "Carregando dados...")
                    }
                }

                _weatherState.postValue(result)
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Erro inesperado", e)
                _weatherState.postValue(Resource.Error("error_generic"))
            }
        }
    }

    fun clearWeatherState() {
        _weatherState.value = null
    }
}