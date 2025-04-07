package com.example.bus

import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var database: DatabaseReference
    private lateinit var driverId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        database = FirebaseDatabase.getInstance().reference.child("BusDrivers")

        driverId = "Bus_101_Driver"
        requestLocationUpdates()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    updateDriverLocation(location)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    private fun updateDriverLocation(location: Location) {
        val driverLocation = mapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude
        )

        database.child(driverId).setValue(driverLocation)
            .addOnSuccessListener {
                Log.d("Firebase", "Location Updated Successfully")
                fetchBusData()
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to update location", e)
            }
    }

    private fun fetchBusData() {
        database.child("BusDrivers").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val busDataList = mutableListOf<String>()
                for (bus in snapshot.children) {
                    val busNumber = bus.key
                    val latitude = bus.child("latitude").value.toString()
                    val longitude = bus.child("longitude").value.toString()
                    busDataList.add("Bus: $busNumber, Lat: $latitude, Lng: $longitude")
                }
                runOnUiThread {
                    Toast.makeText(this, busDataList.joinToString("\n"), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                requestLocationUpdates()
            } else {
                Toast.makeText(this, "Location permission required!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
