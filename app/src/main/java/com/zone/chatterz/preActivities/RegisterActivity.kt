package com.zone.chatterz.preActivities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.iceteck.silicompressorr.SiliCompressor
import com.mikhaellopez.circularimageview.CircularImageView
import com.zone.chatterz.home.MainActivity
import com.zone.chatterz.common.ManualAuthentication
import com.zone.chatterz.R
import com.zone.chatterz.connection.Connection
import com.zone.chatterz.connection.FirebaseMethods
import com.zone.chatterz.connection.RequestCallback
import java.io.ByteArrayOutputStream
import java.io.File

class RegisterActivity : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {

    private val PERMISSION_CODE = 101;
    private val TAKE_PHOTO_GALLERY = 201;
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var display_unique_name_register_layout: LinearLayout
    private lateinit var unique_name_edittext_register: EditText
    private lateinit var signUp_layout: LinearLayout
    private lateinit var next_signup_button: RelativeLayout
    private lateinit var passwordInput: TextInputEditText
    private lateinit var confirmPasswordInput: TextInputEditText
    private lateinit var signUpBtn: Button
    private lateinit var signInBtn: TextView
    private lateinit var progressBar: ProgressBar
    private var selectedImage: Uri? = null
    private var resultPath: String = ""
    private val gender_array = arrayOf<String>("Male", "Female")
    private var choosed_gender = "Male"
    private var choosed_username = ""

    private lateinit var other_info_register_layout: RelativeLayout
    private lateinit var name_user_register_welcome: TextView
    private lateinit var add_profile_photo_register: CircularImageView
    private lateinit var add_proile_photo_btn_register: TextView

    //  private lateinit var skip_btn_register : Button
    private lateinit var next_btn_register: Button
    private lateinit var register_layout: RelativeLayout
    private lateinit var gender_chooose_spinner: Spinner

    private lateinit var display_unique_name: String
    private var signUpState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        usernameInput = findViewById(R.id.username_edittext_register)
        emailInput = findViewById(R.id.email_edittext_register)
        passwordInput = findViewById(R.id.password_edittext_register)
        confirmPasswordInput = findViewById(R.id.confirm_password_edittext_register)

        other_info_register_layout = findViewById(R.id.other_info_register_layout)
        name_user_register_welcome = findViewById(R.id.name_user_register_welcome)
        add_profile_photo_register = findViewById(R.id.add_profile_photo_register)
        add_proile_photo_btn_register = findViewById(R.id.add_proile_photo_btn_register)
        //   skip_btn_register = findViewById(R.id.skip_btn_register)
        next_btn_register = findViewById(R.id.next_btn_register)
        register_layout = findViewById(R.id.register_layout)
        gender_chooose_spinner = findViewById(R.id.gender_chooose_spinner)

        display_unique_name_register_layout = findViewById(R.id.display_unique_name_register_layout)
        unique_name_edittext_register = findViewById(R.id.unique_name_edittext_register)
        signUp_layout = findViewById(R.id.signUp_layout)
        next_signup_button = findViewById(R.id.next_signup_button)

        signUpBtn = findViewById(R.id.register_signup_button)
        signInBtn = findViewById(R.id.signIn_text_clickable)
        progressBar = findViewById(R.id.progressBar_register)

        //editext unique name
        unique_name_edittext_register.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                val query =
                    FirebaseDatabase.getInstance().getReference("Users").orderByChild("displayName")
                        .startAt(text).endAt(text + "\uf8ff")

                query.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (!p0.exists()) {
                            val img: Drawable? = ContextCompat.getDrawable(
                                this@RegisterActivity,
                                R.drawable.seen_icon
                            )
                            unique_name_edittext_register.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                null,
                                img,
                                null
                            )
                        }
                    }
                })
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        //if user clicked in signInBtn then it moves to LoginActivity
        signInBtn.setOnClickListener(this)
        next_signup_button.setOnClickListener(this)
        //if user clicked in signUpBtn then signUp will done
        signUpBtn.setOnClickListener(this)

        next_btn_register.setOnClickListener(this)

        //choose gender spinner
        val adapter =
            ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, gender_array)
        this.gender_chooose_spinner.adapter = adapter

        this.gender_chooose_spinner.onItemSelectedListener = this
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
                    hashMap.put("displayName", display_unique_name)
                    hashMap.put("username", username)
                    hashMap.put("imageUrl", "null")
                    hashMap.put("bio", "null")
                    hashMap.put("gender", choosed_gender)
                    hashMap.put("status", "offline")

                    database.child(firebaseUser.uid).setValue(hashMap)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                //task sucessfull so its moves to next screen: Main Screen of app
                                other_info_register_layout.visibility = View.VISIBLE
                                register_layout.visibility = View.GONE
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
        this.choosed_username = username
    }

    override fun onBackPressed() {
        if (!signUpState) {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_PHOTO_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                this.selectedImage = data?.data
                this.add_profile_photo_register.setImageURI(selectedImage)
                var str = selectedImage?.path
                str = str?.substring(4)
                val file = File(str)
                resultPath = file.path
                uploadProfilePhoto()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadProfilePhoto() {
        val resultBitmap = SiliCompressor.with(this).getCompressBitmap(resultPath)
        val stream = ByteArrayOutputStream()
        resultBitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
        val resultArray = stream.toByteArray()

        val storageRef = FirebaseStorage.getInstance().getReference("profileImages").child(Connection.user+".jpg")

        FirebaseMethods.singleValueEvent(Connection.userRef+File.separator+Connection.user,object : RequestCallback(){
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                val uploadTask = storageRef.putBytes(resultArray)

                uploadTask.addOnSuccessListener {
                    uploadTask.continueWithTask {
                        if(!it.isSuccessful){
                            throw it.exception!!
                        }
                        return@continueWithTask storageRef.downloadUrl
                    }.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val uri = it.result
                            val imgUrl = uri.toString()
                            val hashMap = hashMapOf<String, Any>()
                            hashMap.put("postImage", imgUrl)
                            dataSnapshot.ref.updateChildren(hashMap)
                            return@addOnCompleteListener
                        }
                    }
                }
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                // If request is cancelled, the result arrays are empty.
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            signInBtn -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            next_signup_button -> {
                display_unique_name_register_layout.visibility = View.GONE
                signUp_layout.visibility = View.VISIBLE
                display_unique_name = unique_name_edittext_register.text.toString()
            }
            signUpBtn -> {
                signUpBtn.visibility = View.INVISIBLE
                progressBar.visibility = View.VISIBLE
                resistTouch()
                registerNewUser()
                this.name_user_register_welcome.text = choosed_username
            }
            next_btn_register -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
            add_proile_photo_btn_register -> {
                val permission = arrayOf<String>(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                if ((ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_DENIED)
                    && (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_DENIED)
                ) {
                    ActivityCompat.requestPermissions(this, permission, PERMISSION_CODE)
                }
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, TAKE_PHOTO_GALLERY)
            }
            else -> {
                return
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        this.choosed_gender = gender_array[position]
    }
}

