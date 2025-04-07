package com.example.bus

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SignUpOption : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signupoption) // Make sure this matches your XML filename

        // Find ImageView elements by their IDs
        val studentImage = findViewById<ImageView>(R.id.box1)
        val universityImage = findViewById<ImageView>(R.id.box2)
        val busDriverImage = findViewById<ImageView>(R.id.box3)

        // Set click listeners for each option
        studentImage.setOnClickListener { navigateToLogin("Student") }
        universityImage.setOnClickListener { navigateToLogin("University") }
        busDriverImage.setOnClickListener { navigateToLogin("Bus Driver") }
    }

    private fun navigateToLogin(userType: String) {
        val intent = Intent(this, Login::class.java)
        intent.putExtra("USER_TYPE", userType) // Pass selected option to signup page
        startActivity(intent)
    }
}
