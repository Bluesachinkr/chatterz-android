package com.zone.chatterz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        signIn_text_clickable.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        register_signup_button.setOnClickListener {

            register_signup_button.visibility = View.INVISIBLE
            progressBar_register.visibility = View.VISIBLE

            val emailInput = email_edittext_register.text.toString()
            val passwordInput = password_edittext_register.text.toString()
            val confirmPasswordInput = confirm_password_edittext_register.text.toString()

            val isEmailValid = ManualAuthentication.validateEmail(emailInput)
            val isPasswordValid = ManualAuthentication.validatePassword(passwordInput)
            if (isEmailValid.equals("Valid")) {
                if (isPasswordValid.equals("Valid")) {
                    if (passwordInput.equals(confirmPasswordInput)) {
                        createNewUser(emailInput, passwordInput)
                    } else {
                        confirm_password_edittext_register.setText("")
                        dismissSignUp()
                        confirm_password_edittext_register.setError("Password didn't match")
                    }
                } else {
                    dismissSignUp()
                    password_edittext_register.setText("")
                    password_edittext_register.setError(isPasswordValid)
                }
            } else {
                dismissSignUp()
                email_edittext_register.setText("")
                email_edittext_register.setError(isEmailValid)
            }
        }
    }

    private fun createNewUser(emailInput: String, passwordInput: String) {

        mAuth.createUserWithEmailAndPassword(emailInput, passwordInput)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //SignUp Sucess ,Update UI with userObject
                    val firebaseUser = mAuth.currentUser
                    val userId = firebaseUser?.uid

                    database = FirebaseDatabase.getInstance().getReference().child("Users").child(userId!!)

                    val usernameInput = username_edittext_register.text.toString()
                    var hashMap = java.util.HashMap<String, Any>()
                    userId?.let {
                        hashMap.put("id", it)
                        hashMap.put("email", emailInput)
                        hashMap.put("fullName", usernameInput)
                        hashMap.put("isVerified", "Not Verified")
                    }

                    database.setValue(hashMap).addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            //task sucessfull so its moves to next screen: Main Screen of app
                            dismissSignUp()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
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
        register_signup_button.visibility = View.VISIBLE
        register_signup_button.isEnabled = true
        progressBar_register.visibility = View.INVISIBLE
    }

}
