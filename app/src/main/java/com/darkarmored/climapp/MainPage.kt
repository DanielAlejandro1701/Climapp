package com.darkarmored.climapp


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkarmored.climapp.api.NetworkResponse
import com.darkarmored.climapp.api.WeatherModel
import kotlin.math.roundToInt

/**
 * Pantalla principal de la aplicacion donde de inicialmente se muestra la informacion
 * especifica donde se encuentre el dispositivo
 *
 * En la opcion de busqueda, se consulta alguna ciudad para obtener datos especificos desde
 * la API de OpenWeatherMap
 *
 */
@Composable
fun MainPage(viewModel: WeatherViewModel) {

    //Lanzamiento inicial de la funcion como pantalla inicial
    LaunchedEffect(Unit) {
        viewModel.getDatalonlat()
    }

    var city by remember { mutableStateOf("") }
    val weatherResult = viewModel.weatherResult.observeAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val backIcon = Icons.Default.KeyboardArrowLeft
    var showIcon by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xDCECFB)
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(48.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (showIcon) {
                IconButton(onClick = {
                    viewModel.getDatalonlat()
                    showIcon = false
                    city = ""
                }) {
                    Icon(
                        imageVector = backIcon,
                        contentDescription = "back",
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            // Caja de busqueda por ciudad
            OutlinedTextField(
                modifier = Modifier.weight(0.5f),
                singleLine = true,
                value = city,
                onValueChange = {
                    city = it
                },
                label = { Text(text = "Find some city") }
            )
            IconButton(onClick = {
                viewModel.getDataCity(city)
                keyboardController?.hide()
                showIcon = true
            }) {

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Location",
                    modifier = Modifier.size(56.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        when (val result = weatherResult.value) {

            is NetworkResponse.Error -> {
                Text(text = result.message)
            }

            NetworkResponse.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            is NetworkResponse.Success -> {
                WeatherDetails(data = result.data)
            }

            null -> {}
        }
    }

    Row (
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = 128.dp),

    ){
        Text(text = "Powered by ")
        Image(painter = painterResource(id = R.drawable.openweather) , contentDescription = "OpenWeather logo", modifier = Modifier.size(100.dp).padding(top = 48.dp))

    }

}

/**
 * Funcion para mostrar los datos obtenidos desde la API de OpenWeatherMap
 *
 */
@Composable
fun WeatherDetails(data: WeatherModel) {

    var temp = (data.main.temp.toDouble().roundToInt())
    var condition = data.weather.get(0).description

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {

            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location Icon",
                modifier = Modifier.size(40.dp)
            )
            Text(text = data.name, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = data.sys.country, fontSize = 18.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,

            ) {
            Text(
                text = "${temp}°",
                fontSize = 128.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(32.dp))
            WeatherIcon(condition.capitalize())
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = condition.capitalize(),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedCard(
            border = BorderStroke(
                width = 2.dp,
                color = Color.LightGray),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {

                ElevatedCard(
                    colors = CardDefaults.cardColors(),
                    modifier = Modifier.padding(8.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )

                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painterResource(R.drawable.wind),
                            contentDescription = " ",
                            modifier = Modifier
                                .size(96.dp)
                                .padding(16.dp)
                        )

                        WeatherKeyVal("Wind speed", "${data.wind.speed} km/h")
                    }

                }
                ElevatedCard(
                    colors = CardDefaults.cardColors(),
                    modifier = Modifier.padding(8.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painterResource(R.drawable.rain),
                            contentDescription = " ",
                            modifier = Modifier
                                .size(96.dp)
                                .padding(16.dp)
                        )
                        WeatherKeyVal("Humidity", "${data.main.humidity} %")
                    }

                }

                ElevatedCard(
                    colors = CardDefaults.cardColors(),
                    modifier = Modifier.padding(8.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painterResource(R.drawable.temp),
                            contentDescription = " ",
                            modifier = Modifier
                                .size(96.dp)
                                .padding(16.dp)
                        )
                        WeatherKeyVal(
                            "Feels like",
                            "${data.main.feels_like.toDouble().roundToInt()}°C"
                        )
                    }
                }
            }
        }
    }
}

/**
 * Funcion para organizar los datos obtenidos desde la API de OpenWeatherMap
 */
@Composable
fun WeatherKeyVal(key: String, value: String) {
    Column(
        modifier = Modifier.padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = key, fontWeight = FontWeight.Medium, color = Color.Gray, fontSize = 14.sp)
    }
}

/**
 * Funcion para mostrar un icono segun el clima obtenido segun la infomacion de
 * la API de OpenWeatherMap
 */
@Composable
fun WeatherIcon(condition: String) {

    var image: Painter? = null

    when (condition) {
        "Nubes dispersas" -> image = painterResource(R.drawable.few_clouds)
        "Algo de nubes" -> image = painterResource(R.drawable.few_clouds)
        "Muy nuboso" -> image = painterResource(R.drawable.broken_clouds)
        "Nubes" -> image = painterResource(R.drawable.broken_clouds)
        "Cielo claro" -> image = painterResource(R.drawable.clear_sky)
        "Lluvia ligera" -> image = painterResource(R.drawable.shower_rain)
        "Lluvia moderada" -> image = painterResource(R.drawable.shower_rain)
        "Lluvia" -> image = painterResource(R.drawable.rain)
        //"Lluvia" -> image = painterResource(R.drawable.rain)
    }

    Box() {
        if (image != null) {
            Image(
                painter = image,
                contentDescription = "viento",
                modifier = Modifier.size(128.dp)
            )
        }
    }
}