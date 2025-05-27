package vn.com.rd.waterreminder.data.model

data class WeatherResponse(
    val name: String, // city name
    val main: Main,
    val weather: List<Weather>
)

data class Main(
    val temp: Double,
    val humidity: Int
)

data class Weather(
    val description: String,
    val icon: String
)