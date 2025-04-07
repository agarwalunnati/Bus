package com.example.bus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var firstNameField: EditText
    private lateinit var lastNameField: EditText
    private lateinit var emailField: EditText
    private lateinit var phoneField: EditText
    private lateinit var passwordField: EditText
    private lateinit var confirmPasswordField: EditText
    private lateinit var signupButton: Button
    private lateinit var checkBox: CheckBox
    private var userType: String = "Student" // Default user type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize Firebase reference
        database = FirebaseDatabase.getInstance("https://hoponapp-131d1-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users")

        // Bind UI elements
        firstNameField = findViewById(R.id.editfirstname)
        lastNameField = findViewById(R.id.editlastname)
        emailField = findViewById(R.id.EmailAddress)
        phoneField = findViewById(R.id.editPhone)
        passwordField = findViewById(R.id.editpass)
        confirmPasswordField = findViewById(R.id.editconfirmPass)
        signupButton = findViewById(R.id.button_signup)
        checkBox = findViewById(R.id.checkBox)

        // ✅ Get User Type from Intent (Fixing Possible Null Issue)
        userType = intent.getStringExtra("USER_TYPE") ?: "Student"
        Log.d("Signup", "Received UserType: $userType") // Debugging

        // ✅ Add Toast for Debugging if Button is Clicked
        signupButton.setOnClickListener {
            Toast.makeText(this, "Signup Button Clicked!", Toast.LENGTH_SHORT).show()
            validateAndSaveUser()
        }
    }

    private fun validateAndSaveUser() {
        val firstName = firstNameField.text.toString().trim()
        val lastName = lastNameField.text.toString().trim()
        val email = emailField.text.toString().trim()
        val phone = phoneField.text.toString().trim()
        val password = passwordField.text.toString().trim()
        val confirmPassword = confirmPasswordField.text.toString().trim()

        // ✅ Debugging Logs
        Log.d("Signup", "User Type: $userType, Email: $email, Phone: $phone")

        // ✅ Validate Input Fields
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            return
        }

        if (!checkBox.isChecked) {
            Toast.makeText(this, "You must agree to the Terms and Conditions!", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ Ensure Correct Firebase Path
        val userId = database.child(userType).push().key ?: "Unknown"

        // ✅ Create User Data Map
        val userData = mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "phone" to phone,
            "password" to password,
            "userType" to userType
        )

        // ✅ Store Data in Firebase with Logging
        database.child(userType).child(userId).setValue(userData)
            .addOnSuccessListener {
                Log.d("Signup", "User added successfully in Firebase")
                Toast.makeText(this, "Signup Successful!", Toast.LENGTH_SHORT).show()

                // ✅ Redirect to Login Page After Signup
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { error ->
                Log.e("Signup", "Error adding user: ${error.message}")
                Toast.makeText(this, "Signup Failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
