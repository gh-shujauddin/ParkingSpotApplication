@file:OptIn(ExperimentalMaterial3Api::class)

package com.qadri.parkingspotapplication.presentation


import android.content.ContentValues
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.qadri.parkingspotapplication.domain.repository.LocationRepository
import com.qadri.parkingspotapplication.viewmodels.ApplicationViewModel
import com.qadri.parkingspotapplication.viewmodels.MapsViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random
import com.qadri.parkingspotapplication.R

private val locationSource = MyLocationSource()

@Composable
fun MapScreen(
    viewModel: MapsViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val locationRepository = LocationRepository(context)

    val uiSettings = remember {
        MapUiSettings(zoomControlsEnabled = false)
    }
    val properties = viewModel.state.properties


    /*For Current Location*/

    var zoom by remember {
        mutableFloatStateOf(15f)
    }
    var isMapLoaded by remember { mutableStateOf(false) }
    val applicationViewModel: ApplicationViewModel = hiltViewModel()

    // To show blue dot on map
//    val mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }

    // Collect location updates
    val locationState =
        applicationViewModel.locationFlow.collectAsState(initial = newLocation())

    val defaultLocation =
        LatLng(locationState.value.latitude, locationState.value.longitude)
    val defaultCameraPosition = CameraPosition.fromLatLngZoom(defaultLocation, 1f)

    // To control and observe the map camera
    val cameraPositionState = rememberCameraPositionState {
        position = defaultCameraPosition
    }

    val isGpsEnabled = locationRepository.gpsStatus.collectAsState(initial = false)

    var cameraPosition by remember {
        mutableStateOf(
            CameraPosition.fromLatLngZoom(
                LatLng(
                    locationState.value.latitude,
                    locationState.value.longitude
                ), zoom
            )
        )
    }
    // Update blue dot and camera when the location changes
    LaunchedEffect(locationState.value) {
        Log.d(ContentValues.TAG, "Updating blue dot on map...")
        locationSource.onLocationChanged(locationState.value)
        if (!isGpsEnabled.value) {
//            Toast.makeText(
//                context,
//                "GPS not enabled",
//                Toast.LENGTH_LONG
//            ).show()
        } else {
            Log.d(ContentValues.TAG, "Updating camera position...")
            cameraPosition = CameraPosition.fromLatLngZoom(
                LatLng(
                    locationState.value.latitude,
                    locationState.value.longitude
                ), zoom
            )
        }
    }

    // Detect when the map starts moving and print the reason
    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving) {
            Log.d(
                ContentValues.TAG,
                "Map camera started moving due to ${cameraPositionState.position}"
            )
        }
    }

    /*For Current Location*/
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onEvent(MapEvent.ToggleFalloutMap) }) {
                Icon(
                    imageVector =
                    if (viewModel.state.isFallout) Icons.Default.ToggleOff
                    else Icons.Default.ToggleOn,
                    contentDescription = "Toggle Fallout Map"
                )
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(MaterialTheme.colorScheme.primary)
            )
        }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it))
        {
            GoogleMap(
                modifier = Modifier.matchParentSize(),
                uiSettings = uiSettings,
                onMapLongClick = { latlng ->
                    viewModel.onEvent(MapEvent.OnMapLongClick(latlng))
                },
                cameraPositionState = cameraPositionState,
                onMapLoaded = {
                    isMapLoaded = true
                },
                // This listener overrides the behavior for the location button. It is intended to be used when a
                // custom behavior is needed.
                onMyLocationButtonClick = {
                    Log.d(
                        ContentValues.TAG,
                        "Overriding the onMyLocationButtonClick with this Log"
                    );
                    if (!isGpsEnabled.value) {
                        Toast.makeText(
                            context,
                            "GPS not enabled",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        coroutineScope.launch {
                            cameraPositionState
                                .animate(
                                    update = CameraUpdateFactory.newCameraPosition(
                                        cameraPosition
                                    )
                                )
                        }
                    }
                    true
                },
                locationSource = locationSource,
                properties = properties
            ) {
                viewModel.state.parkingSpots.forEach { spot ->
                    Marker(
                        position = LatLng(spot.lat, spot.lng),
                        title = "Parking Spot (${spot.lat}, ${spot.lng})",
                        snippet = "Long Click to delete",
                        onInfoWindowLongClick = {
                            viewModel.onEvent(MapEvent.OnInfoWindowLongClick(spot))
                        },
                        onClick = {
                            it.showInfoWindow()
                            true
                        },
                        icon = BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_GREEN
                        )
                    )
                }
            }
            if (!isMapLoaded) {
                AnimatedVisibility(
                    modifier = Modifier
                        .matchParentSize(),
                    visible = !isMapLoaded,
                    enter = EnterTransition.None,
                    exit = fadeOut()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .wrapContentSize()
                    )
                }
            }
        }
    }
}


class MyLocationSource : LocationSource {

    private var listener: LocationSource.OnLocationChangedListener? = null

    override fun activate(listener: LocationSource.OnLocationChangedListener) {
        this.listener = listener
    }

    override fun deactivate() {
        listener = null
    }

    fun onLocationChanged(location: Location) {
        listener?.onLocationChanged(location)
    }
}

private fun newLocation(): Location {
    val location = Location("MyLocationProvider")
    location.apply {
        latitude = 27.265 + Random.nextFloat()
        longitude = 85.6598 + Random.nextFloat()
    }
    return location
}