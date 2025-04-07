package com.example.bus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class UniversityMainPage : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var universityNameTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_university_mianpage) // Ensure this matches your XML file name

        // Find UI elements
        universityNameTextView = findViewById(R.id.uni_name)
        val btnSeeAllBuses = findViewById<Button>(R.id.btn_see_all_buses)
        val btnAddStudents = findViewById<Button>(R.id.btn_add_students)

        // Initialize Firebase Database Reference
        database = FirebaseDatabase.getInstance("https://hoponapp-131d1-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("UniversityDetails").child("university_name")

        // Fetch university name from Firebase
        fetchUniversityName()

        // Click listener for "See All Buses"
        btnSeeAllBuses.setOnClickListener {
            startActivity(Intent(this, SeeAllBuses::class.java))
        }

        // Click listener for "Add Students"
        btnAddStudents.setOnClickListener {
            startActivity(Intent(this, AddStudents::class.java))
        }
    }

    private fun fetchUniversityName() {
        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val universityName = snapshot.value.toString()
                universityNameTextView.text = universityName // Set name to TextView
            } else {
                universityNameTextView.text = "University Name Not Found"
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
