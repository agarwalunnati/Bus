package com.example.bus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class Login : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupTextView: TextView
    private lateinit var forgetPasswordTextView: TextView
    private lateinit var database: DatabaseReference

    private var universityName: String = "" // Store university name for verification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.EmailAddress)
        passwordEditText = findViewById(R.id.Password)
        loginButton = findViewById(R.id.btn_login)
        signupTextView = findViewById(R.id.signup)
        forgetPasswordTextView = findViewById(R.id.forgetpass)

        database = FirebaseDatabase.getInstance()
            .getReference("Buses") // Reference to bus details

        fetchUniversityName() // ✅ Fetch university before login

        loginButton.setOnClickListener {
            loginUser()
        }

        signupTextView.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        forgetPasswordTextView.setOnClickListener {
            val intent = Intent(this, Forgetpass::class.java)
            startActivity(intent)
        }
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Authenticate with Firebase
            database.child("Users").orderByChild("email").equalTo(email).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        for (user in snapshot.children) {
                            val storedPassword = user.child("password").value.toString()
                            if (storedPassword == password) {
                                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java)) // Redirect to Main
                                finish()
                                return@addOnSuccessListener
                            }
                        }
                        Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }

    /**
     * ✅ Fetch University Name (Stored in Database)
     */
    private fun fetchUniversityName() {
        val universityRef = FirebaseDatabase.getInstance()
            .getReference("UniversityDetails")
            .child("university_name") // Assuming university name is stored globally

        universityRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                universityName = snapshot.value.toString()
            } else {
                Toast.makeText(this, "University not found!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error fetching university: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * ✅ Login Function with University Verification
     */
    private fun loginUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("LoginDebug", "Login button clicked - Email: $email, Password: $password")

        // Search for the user in all buses
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("LoginDebug", "Firebase database fetched successfully!")

                var userFound = false
                var correctPassword = false
                var userUniversity = ""

                for (bus in snapshot.children) {
                    val studentsRef = bus.child("Students")

                    for (student in studentsRef.children) {
                        val studentEmail = student.child("email").value.toString()
                        val studentPassword = student.child("password").value.toString()
                        val studentUniversity = bus.child("university").value.toString()

                        Log.d("LoginDebug", "Checking student: $studentEmail under $studentUniversity")

                        if (studentEmail == email) {
                            userFound = true
                            if (studentPassword == password) {
                                correctPassword = true
                                userUniversity = studentUniversity
                                break
                            }
                        }
                    }

                    if (correctPassword) break
                }

                // ✅ Check Login Conditions
                when {
                    !userFound -> {
                        Log.d("LoginDebug", "Email not found in database!")
                        Toast.makeText(this@Login, "Email not registered!", Toast.LENGTH_SHORT).show()
                    }
                    !correctPassword -> {
                        Log.d("LoginDebug", "Incorrect password!")
                        Toast.makeText(this@Login, "Incorrect Password!", Toast.LENGTH_SHORT).show()
                    }
                    userUniversity != universityName -> {
                        Log.d("LoginDebug", "University mismatch! User's university: $userUniversity, Expected: $universityName")
                        Toast.makeText(this@Login, "Access Denied! University mismatch.", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Log.d("LoginDebug", "Login Successful! Redirecting...")
                        Toast.makeText(this@Login, "Login Successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@Login, MainActivity::class.java))
                        finish()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("LoginDebug", "Database error: ${error.message}")
                Toast.makeText(this@Login, "Database error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

}
