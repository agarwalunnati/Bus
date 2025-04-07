package com.example.bus

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class AddStudents : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPhone: EditText
    private lateinit var editStudentID: EditText
    private lateinit var editPassword: EditText
    private lateinit var spinnerBuses: Spinner
    private lateinit var btnAddStudent: Button
    private lateinit var database: DatabaseReference
    private var busList = mutableListOf<String>()
    private var universityName: String = "" // Store the university name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_students)

        // Initialize UI Elements
        editName = findViewById(R.id.editStudentName)
        editEmail = findViewById(R.id.editStudentEmail)
        editPhone = findViewById(R.id.editStudentPhone)
        editStudentID = findViewById(R.id.editStudentID)
        editPassword = findViewById(R.id.editStudentpass)
        spinnerBuses = findViewById(R.id.spinnerBuses)
        btnAddStudent = findViewById(R.id.btnAddStudent)

        // Firebase Database Reference
        database = FirebaseDatabase.getInstance().getReference("Buses")

        // Get university name first
        fetchUniversityName()

        // Button Click: Add Student
        btnAddStudent.setOnClickListener {
            addStudentToDatabase()
        }
    }

    private fun fetchUniversityName() {
        val universityRef = FirebaseDatabase.getInstance()
            .getReference("UniversityDetails")
            .child("university_name") // Assuming it's stored under UniversityDetails

        universityRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                universityName = snapshot.value.toString() // Store university name
                fetchBuses() // Fetch buses only after university name is retrieved
            } else {
                Toast.makeText(this, "University name not found!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching university name: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchBuses() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                busList.clear()
                for (bus in snapshot.children) {
                    val busUniversity = bus.child("university").value.toString()
                    if (busUniversity == universityName) { // Only add buses from this university
                        busList.add(bus.key!!) // Add bus ID to the list
                    }
                }

                if (busList.isEmpty()) {
                    Toast.makeText(this@AddStudents, "No buses found for $universityName", Toast.LENGTH_SHORT).show()
                }

                // Set up Spinner Adapter
                val adapter = ArrayAdapter(this@AddStudents, android.R.layout.simple_spinner_dropdown_item, busList)
                spinnerBuses.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddStudents, "Error fetching buses: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addStudentToDatabase() {
        val name = editName.text.toString().trim()
        val email = editEmail.text.toString().trim()
        val phone = editPhone.text.toString().trim()
        val studentID = editStudentID.text.toString().trim()
        val password = editPassword.text.toString().trim()
        val selectedBus = spinnerBuses.selectedItem?.toString() ?: ""

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || studentID.isEmpty() || password.isEmpty() || selectedBus.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            return
        }

        // Create Student Data
        val studentData = mapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "studentID" to studentID,
            "password" to password
        )

        // Store student under selected bus
        database.child(selectedBus).child("Students").child(studentID).setValue(studentData)
            .addOnSuccessListener {
                Toast.makeText(this, "Student Added to Bus $selectedBus", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        editName.text.clear()
        editEmail.text.clear()
        editPhone.text.clear()
        editStudentID.text.clear()
        editPassword.text.clear()
    }
}
