package com.zone.chatterz.PreActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.zone.chatterz.MainActivity
import com.zone.chatterz.ManualAuthentication
import com.zone.chatterz.R

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginBtn: Button
    private lateinit var signUp: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var mAuth: FirebaseAuth
    private var signInState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailInput = findViewById(R.id.email_edittext_login)
        passwordInput = findViewById(R.id.password_edittext_login)
        loginBtn = findViewById(R.id.login_signIn_button)
        signUp = findViewById(R.id.signUp_text_clickable)
        progressBar = findViewById(R.id.progressBar_login)

        mAuth = FirebaseAuth.getInstance()

        signUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginBtn.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            loginBtn.visibility = View.INVISIBLE
            resistTouch()
            loginUser()
        }
    }

    private fun loginUser() {
        val userEmailInput = emailInput.text.toString()
        val userPasswordInput = passwordInput.text.toString()

        val isValidEmail =
            ManualAuthentication.validateEmail(userEmailInput)
        val isValidPassword =
            ManualAuthentication.validatePassword(userPasswordInput)

        if (isValidEmail.equals("Valid")) {
            if (isValidPassword.equals("Valid")) {
                signIn(userEmailInput, userPasswordInput)
            } else {
                passwordInput.setText("")
                passwordInput.setError(isValidPassword)
            }
        } else {
            emailInput.setText("")
            emailInput.setError(isValidEmail)
        }
    }

    private fun signIn(userEmail: String, userPassword: String) {
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //Success move to Main Screen
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                } else {
                    //if failed then display a message to the user
                    dismissLogin()
                    Toast.makeText(this, "Sign In Failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun resistTouch() {
        loginBtn.isEnabled = false
        signUp.isEnabled = false
        emailInput.isEnabled = false
        passwordInput.isEnabled = false
        signInState = true
    }

    private fun dismissLogin() {
        loginBtn.visibility = View.VISIBLE
        progressBar.visibility = View.INVISIBLE
        loginBtn.isEnabled = true
        signUp.isEnabled = true
        emailInput.isEnabled = true
        passwordInput.isEnabled = true
        signInState = false
    }

    override fun onBackPressed() {
        if (!signInState) {
            val intent =Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
