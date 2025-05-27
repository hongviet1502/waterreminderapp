package vn.com.rd.waterreminder.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
import vn.com.rd.waterreminder.data.model.WeatherResponse

interface WeatherApi {
    @GET("weather")
    fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Call<WeatherResponse>
}