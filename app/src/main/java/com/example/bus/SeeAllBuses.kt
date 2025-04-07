package com.example.bus

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class SeeAllBuses : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_all_busses)

        tableLayout = findViewById(R.id.tableLayoutBuses)
        database = FirebaseDatabase.getInstance().getReference("Buses")

        fetchBuses()
    }

    private fun fetchBuses() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (bus in snapshot.children) {
                    val busID = bus.key ?: "Unknown"
                    val university = bus.child("university").value.toString()
                    val studentsSnapshot = bus.child("Students")

                    val totalStudents = studentsSnapshot.childrenCount.toInt()
                    val studentNames = studentsSnapshot.children.joinToString(", ") {
                        it.child("name").value.toString()
                    }

                    addRowToTable(busID, university, totalStudents, studentNames)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SeeAllBuses, "Failed to load buses: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addRowToTable(busID: String, university: String, totalStudents: Int, studentNames: String) {
        val tableRow = TableRow(this)

        val busIDView = TextView(this)
        busIDView.text = busID
        busIDView.setPadding(10, 10, 10, 10)

        val universityView = TextView(this)
        universityView.text = university
        universityView.setPadding(10, 10, 10, 10)

        val totalStudentsView = TextView(this)
        totalStudentsView.text = totalStudents.toString()
        totalStudentsView.setPadding(10, 10, 10, 10)
        totalStudentsView.setTextColor(resources.getColor(android.R.color.holo_blue_dark)) // Make it look like a hyperlink
        totalStudentsView.setOnClickListener {
            val intent = Intent(this@SeeAllBuses, BusStudentsActivity::class.java)
            intent.putExtra("BUS_ID", busID) // Send Bus ID to next activity
            startActivity(intent)
        }

        val studentNamesView = TextView(this)
        studentNamesView.text = studentNames
        studentNamesView.setPadding(10, 10, 10, 10)

        tableRow.addView(busIDView)
        tableRow.addView(universityView)
        tableRow.addView(totalStudentsView)
        tableRow.addView(studentNamesView)

        tableLayout.addView(tableRow)
    }
}
