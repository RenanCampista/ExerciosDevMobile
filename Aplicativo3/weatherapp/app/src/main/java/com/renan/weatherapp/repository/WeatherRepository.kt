package com.renan.weatherapp.repository

import com.renan.weatherapp.model.WeatherResponse
import com.renan.weatherapp.network.WeatherService
import com.renan.weatherapp.util.Resource
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class WeatherRepository(private val service: WeatherService) {

    suspend fun getWeather(city: String, apiKey: String): Resource<WeatherResponse> {
        return try {
            val response = service.getCurrentWeather(city, apiKey)

            // Validar se os dados recebidos são válidos
            if (response.name.isEmpty() || response.weather.isEmpty()) {
                Resource.Error("error_invalid_data")
            } else {
                Resource.Success(response)
            }
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is UnknownHostException -> "error_no_internet"
                is SocketTimeoutException -> "error_timeout"
                is IOException -> "error_no_internet"
                is HttpException -> {
                    when (e.code()) {
                        404 -> "error_city_not_found"
                        500, 502, 503 -> "error_server"
                        else -> "error_generic"
                    }
                }
                else -> "error_generic"
            }
            Resource.Error(errorMessage)
        }
    }
}