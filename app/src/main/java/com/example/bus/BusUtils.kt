package com.example.bus

import com.google.firebase.database.FirebaseDatabase

object BusUtils {
    private val database = FirebaseDatabase.getInstance().getReference("BusDrivers")

    fun fetchBusData(callback: (List<String>) -> Unit) {
        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val busDataList = mutableListOf<String>()
                for (bus in snapshot.children) {
                    val busNumber = bus.key
                    val latitude = bus.child("latitude").value.toString()
                    val longitude = bus.child("longitude").value.toString()
                    busDataList.add("Bus: $busNumber, Lat: $latitude, Lng: $longitude")
                }
                callback(busDataList) // Return data via callback
            }
        }
    }
}
