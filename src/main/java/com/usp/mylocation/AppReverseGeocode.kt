package com.usp.mylocation

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.util.*

object AppReverseGeocode {
    fun getAddressFromLocation(context: Context, lat: Double, lon: Double): Address? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address> =
            geocoder.getFromLocation(lat, lon, 1)
        return if (addresses.isNotEmpty()) addresses[0] else null
    }
}
