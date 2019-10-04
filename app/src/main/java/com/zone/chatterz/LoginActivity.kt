package com.zone.chatterz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth= FirebaseAuth.getInstance()

        signUp_text_clickable.setOnClickListener {

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }

        checkbox_showpassword_login.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                password_edittext_login.inputType =
                    InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
            } else {
                password_edittext_login.inputType = 129
            }
        }

        login_signIn_button.setOnClickListener {

            progressBar_login.visibility = View.VISIBLE
            login_signIn_button.visibility = View.INVISIBLE
            login_signIn_button.isEnabled = false

            val userEmailInput = email_edittext_login.text.toString()
            val userPasswordInput = password_edittext_login.text.toString()

            val isValidEmail = ManualAuthentication.validateEmail(userEmailInput)
            val isValidPassword = ManualAuthentication.validatePassword(userPasswordInput)

            if (isValidEmail.equals("Valid")) {
                if (isValidPassword.equals("Valid")) {
                    signIn(userEmailInput, userPasswordInput)
                } else {
                    password_edittext_login.setText("")
                    password_edittext_login.setError(isValidPassword)
                }
            } else {
                email_edittext_login.setText("")
                email_edittext_login.setError(isValidEmail)
            }
        }
    }

    private fun signIn(userEmail: String, userPassword: String) {
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //Success move to Main Screen
                    val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                        .child(mAuth.currentUser!!.uid)

                    databaseReference.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            dismissLogin()
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            dismissLogin()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    })
                } else {
                    //if failed then display a message to the user
                    dismissLogin()
                    Toast.makeText(this, "Sign In Failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun dismissLogin() {
        login_signIn_button.visibility = View.VISIBLE
        login_signIn_button.isEnabled = true
        progressBar_login.visibility = View.INVISIBLE
    }
}
