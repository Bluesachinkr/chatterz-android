package com.zone.chatterz.preActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.zone.chatterz.MainActivity
import com.zone.chatterz.ManualAuthentication
import com.zone.chatterz.R
import com.zone.chatterz.adapter.SearchAdapter
import com.zone.chatterz.model.User

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var display_unique_name_register_layout : LinearLayout
    private lateinit var unique_name_edittext_register : EditText
    private lateinit var signUp_layout : LinearLayout
    private lateinit var next_signup_button : RelativeLayout
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var signUpBtn: Button
    private lateinit var signInBtn: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var display_unique_name : String
    private var signUpState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        usernameInput = findViewById(R.id.username_edittext_register)
        emailInput = findViewById(R.id.email_edittext_register)
        passwordInput = findViewById(R.id.password_edittext_register)
        confirmPasswordInput = findViewById(R.id.confirm_password_edittext_register)

        display_unique_name_register_layout = findViewById(R.id.display_unique_name_register_layout)
        unique_name_edittext_register = findViewById(R.id.unique_name_edittext_register)
        signUp_layout = findViewById(R.id.signUp_layout)
        next_signup_button = findViewById(R.id.next_signup_button)

        signUpBtn = findViewById(R.id.register_signup_button)
        signInBtn = findViewById(R.id.signIn_text_clickable)
        progressBar = findViewById(R.id.progressBar_register)
        //if user clicked in signInBtn then it moves to LoginActivity
        signInBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //editext unique name
        unique_name_edittext_register.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                val query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("displayName")
                    .startAt(text).endAt(text + "\uf8ff")

                query.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if(!p0.exists()){
                            unique_name_edittext_register.setCompoundDrawables(null,null,getDrawable(R.drawable.seen_icon),null)
                        }
                    }
                })
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        //if user clicked in signUpBtn then signUp will done
        signUpBtn.setOnClickListener {
            signUpBtn.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            resistTouch()
            registerNewUser()
        }

        next_signup_button.setOnClickListener {
            display_unique_name_register_layout.visibility = View.GONE
            signUp_layout.visibility = View.VISIBLE
            display_unique_name = unique_name_edittext_register.text.toString()
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
                    hashMap.put("displayName",display_unique_name)
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

