package com.qadri.parkingspotapplication

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.qadri.parkingspotapplication.presentation.MapScreen
import com.qadri.parkingspotapplication.ui.theme.ParkingSpotApplicationTheme
import com.qadri.parkingspotapplication.util.PermissionBox
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ParkingSpotApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val locationPermissions = listOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    )

                    val locationPermissionsState = rememberMultiplePermissionsState(
                        locationPermissions
                    )

                    if (locationPermissionsState.allPermissionsGranted) {
                        Text("Thanks! I can access your exact location :D")
                        MapScreen()
                    } else {
                        PermissionBox(
                            permissions = locationPermissions,
                            onGranted = {
                                MapScreen()
                            }
                        )
                    }
                }
            }
        }
    }
}