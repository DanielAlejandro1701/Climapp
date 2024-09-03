package com.darkarmored.climapp


import androidx.compose.runtime.mutableDoubleStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkarmored.climapp.api.Constant
import com.darkarmored.climapp.api.NetworkResponse
import com.darkarmored.climapp.api.RetrofitInstance
import com.darkarmored.climapp.api.WeatherModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel de la aplicacion
 *
 * @param weatherApi instancia de Retrofit y la API de OpenWeatherMap
 * @param _weatherResult contiene los datos de la consulta a la API de OpenWeatherMap
 * @param weatherResult contiene los datos de la consulta a la API de OpenWeatherMap
 * @param latitude contiene la latitud obtenida del dispositivo
 * @param longitude contiene la longitud obtenida del dispositivo
 *
 */
class WeatherViewModel : ViewModel() {

    val weatherApi = RetrofitInstance.weatherApi
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult

    val latitude = mutableDoubleStateOf(0.0)
    val longitude = mutableDoubleStateOf(0.0)

    /**
     * Obtiene los datos desde la api de Openweather segun la longitud y latitud
     * donde se encuentre el dispositivo
     *
     * @param response Obtiene los datos segun la consulta especifica
     *
     * @exception NetworkResponse.Error si hay algun error en la consulta
     */
    fun getDatalonlat(){

        _weatherResult.value = NetworkResponse.Loading
        viewModelScope.launch{
            try {
                delay(5000)
                val response = weatherApi.getWeather(Constant.appid, latitude.value.toString(), longitude.value.toString(),Constant.units, Constant.lang)
                if (response.isSuccessful){
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Error("Error cargando los datos")
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("Error cargando los datos")
            }
        }
    }

    /**
     * Obtiene los datos desde la api de Openweather segun la ciudad especificada
     * que se desea consultar
     *
     * @param response Obtiene los datos segun la consulta especifica
     *
     * @exception NetworkResponse.Error si hay algun error en la consulta
     */
    fun getDataCity(city: String) {

        _weatherResult.value = NetworkResponse.Loading

        viewModelScope.launch {

            try {
                val response =
                    weatherApi.getWeatherByCity(Constant.appid, city, Constant.units, Constant.lang)
                if (response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Error("Error cargando los datos")

                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("Error cargando los datos")
            }


        }

    }

}