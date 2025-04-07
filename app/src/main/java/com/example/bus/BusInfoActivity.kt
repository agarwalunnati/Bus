package com.example.bus

import BusDetails
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bus.BusAdapter
import com.google.firebase.database.*

class BusInfoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var busAdapter: BusAdapter
    private lateinit var busList: MutableList<BusDetails>
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_info)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        busList = mutableListOf()
        val adapter = BusAdapter(this, busList)

        recyclerView.adapter = busAdapter

        // Reference to Firebase Database (Ensure the correct path in your database)
        databaseRef = FirebaseDatabase.getInstance().getReference("Buses")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                busList.clear()
                for (busSnapshot in snapshot.children) {
                    val bus = busSnapshot.getValue(BusDetails::class.java)
                    if (bus != null) {
                        busList.add(bus)
                    }
                }
                busAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BusInfoActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
