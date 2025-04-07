import android.content.Context
import com.example.bus.BusUtils
import com.google.firebase.database.FirebaseDatabase

private fun updateBusInfo(busNumber: String, newPickup: String, newDrop: String) {
    val databaseRef = FirebaseDatabase.getInstance().getReference("Buses").child(busNumber)

    val updates = mapOf(
        "pickupLocation" to newPickup,
        "dropLocation" to newDrop
    )

    databaseRef.updateChildren(updates)
        .addOnSuccessListener {
            BusUtils.fetchBusData { busDataList ->
                for (bus in busDataList) {
                    println(bus) // Replace this with your UI update logic
                }
            }

        }
}



