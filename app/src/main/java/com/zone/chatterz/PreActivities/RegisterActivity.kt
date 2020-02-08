package com.zone.chatterz.PreActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.zone.chatterz.MainActivity
import com.zone.chatterz.ManualAuthentication
import com.zone.chatterz.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var signUpBtn: Button
    private lateinit var signInBtn: TextView
    private lateinit var progressBar: ProgressBar
    private var signUpState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        usernameInput = findViewById(R.id.username_edittext_register)
        emailInput = findViewById(R.id.email_edittext_register)
        passwordInput = findViewById(R.id.password_edittext_register)
        confirmPasswordInput = findViewById(R.id.confirm_password_edittext_register)
        signUpBtn = findViewById(R.id.register_signup_button)
        signInBtn = findViewById(R.id.signIn_text_clickable)
        progressBar = findViewById(R.id.progressBar_register)
        //if user clicked in signInBtn then it moves to LoginActivity
        signInBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        //if user clicked in signUpBtn then signUp will done
        signUpBtn.setOnClickListener {
            signUpBtn.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            resistTouch()
            registerNewUser()
        }
    }

    private fun createNewUser(username: String, emailInput: String, passwordInput: String) {

        mAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //SignUp Sucess ,Update UI with userObject
                    database =
                        FirebaseDatabase.getInstance().getReference().child("Users")
                    firebaseUser = mAuth.currentUser!!
                    var hashMap = java.util.HashMap<String, Any>()
                    hashMap.put("id", firebaseUser.uid)
                    hashMap.put("username", username)
                    hashMap.put("imageUrl", "null")
                    hashMap.put("bio", "null")
                    hashMap.put("status", "offline")

                    database.child(firebaseUser.uid).setValue(hashMap).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            //task sucessfull so its moves to next screen: Main Screen of app
                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                            finish()
                        } else {
                            dismissSignUp()
                            Toast.makeText(this, "Failed to Load", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    //if signup Failed , display a message to the user
                    dismissSignUp()
                    Toast.makeText(this, "Sign Up Failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun dismissSignUp() {
        usernameInput.isEnabled = true
        emailInput.isEnabled = true
        passwordInput.isEnabled = true
        confirmPasswordInput.isEnabled = true
        signUpBtn.isEnabled = true
        signUpBtn.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
        signUpState = false
    }

    private fun resistTouch() {
        usernameInput.isEnabled = false
        emailInput.isEnabled = false
        passwordInput.isEnabled = false
        confirmPasswordInput.isEnabled = false
        signUpBtn.isEnabled = false
        signInBtn.isEnabled = false
        signUpState = true
    }

    private fun registerNewUser() {
        val username = usernameInput.text.toString()
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()
        val isEmailValid =
            ManualAuthentication.validateEmail(email)
        val isPasswordValid =
            ManualAuthentication.validatePassword(password)
        if (isEmailValid.equals("Valid")) {
            if (isPasswordValid.equals("Valid")) {
                if (password.equals(confirmPassword)) {
                    createNewUser(username, email, password)
                } else {
                    confirmPasswordInput.setText("")
                    dismissSignUp()
                    confirmPasswordInput.setError("Password didn't match")
                }
            } else {
                dismissSignUp()
                passwordInput.setText("")
                passwordInput.setError(isPasswordValid)
            }
        } else {
            dismissSignUp()
            emailInput.setText("")
            emailInput.setError(isEmailValid)
        }
    }

    override fun onBackPressed() {
        if (!signUpState) {
            val intent =Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

