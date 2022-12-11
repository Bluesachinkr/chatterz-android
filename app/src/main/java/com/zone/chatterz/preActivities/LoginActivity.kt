package com.zone.chatterz.preActivities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.zone.chatterz.home.MainActivity
import com.zone.chatterz.common.ManualAuthentication
import com.zone.chatterz.R

class LoginActivity : AppCompatActivity(), View.OnClickListener {

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
        signUp = findViewById(R.id.signUp_button)
        progressBar = findViewById(R.id.progressBar_login)

        mAuth = FirebaseAuth.getInstance()

        loginBtn.setOnClickListener(this)
        signUp.setOnClickListener(this)
    }

    private fun loginUser() {
        val userEmailInput = emailInput.text.toString()
        val userPasswordInput = passwordInput.text.toString()

        val isValidEmail =
            ManualAuthentication.validateEmail(userEmailInput)
        val isValidPassword =
            ManualAuthentication.validatePassword(userPasswordInput)

        if (isValidEmail == "Valid") {
            if (isValidPassword == "Valid") {
                signIn(userEmailInput, userPasswordInput)
            } else {
                passwordInput.setText("")
                passwordInput.setError(isValidPassword)
            }
        } else {
            emailInput.setText("")
            emailInput.error = isValidEmail
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
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            loginBtn -> {
                progressBar.visibility = View.VISIBLE
                loginBtn.visibility = View.INVISIBLE
                resistTouch()
                loginUser()
            }
            signUp -> {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
            else -> {
                return
            }
        }
    }
}
