package com.usp.mylocation

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.*
import com.permissionx.guolindev.PermissionX

class FetchCurrentLocation constructor(private val activity: FragmentActivity) {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null

    fun startLocationUpdates(location: (Location) -> Unit, address: (Address?) -> Unit) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        if (PermissionX.isGranted(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            checkLocationCallback(location, address)
        } else {
            PermissionX.init(activity)
                .permissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .request { allGranted, _, _ ->
                    if (allGranted) {
                        checkLocationCallback(location, address)
                    }
                }
        }
    }

    private fun checkLocationCallback(location: (Location) -> Unit , address: (Address?) -> Unit) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                if (p0.locations.size > 0) {
                    val currLocation = p0.locations[0]
                    location(currLocation)
                    address(AppReverseGeocode.getAddressFromLocation(activity, currLocation.latitude, currLocation.longitude))
                }

            }
        }
        createLocationRequest()
    }

    private fun createLocationRequest() {
        val locationRequest = LocationRequest.create().apply {
            interval = 300 * 1000
            fastestInterval = 180 * 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationCallback?.let {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }
    }

    fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }
}
