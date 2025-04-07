package com.example.bus

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class BusStudentsActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var database: DatabaseReference
    private var studentList = mutableListOf<String>()
    private var busID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_student)

        listView = findViewById(R.id.listViewStudents)

        // Get Bus ID from intent
        busID = intent.getStringExtra("BUS_ID")
        if (busID == null) {
            Toast.makeText(this, "Invalid Bus ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        database = FirebaseDatabase.getInstance().getReference("Buses").child(busID!!).child("Students")

        fetchStudents()
    }

    private fun fetchStudents() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear()
                for (student in snapshot.children) {
                    val name = student.child("name").value.toString()
                    studentList.add(name)
                }

                val adapter = ArrayAdapter(this@BusStudentsActivity, android.R.layout.simple_list_item_1, studentList)
                listView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@BusStudentsActivity, "Failed to load students: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
