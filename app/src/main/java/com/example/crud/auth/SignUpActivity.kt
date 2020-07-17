package com.example.crud.auth

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.crud.MainActivity
import com.example.crud.R
import com.example.crud.model.User
import com.example.crud.utils.MySharedPreferences
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.text.SimpleDateFormat
import java.util.*

class SignUpActivity : AppCompatActivity() {
    // Global variabel
    private lateinit var mLoading: ProgressDialog
    private lateinit var mDatabase: DatabaseReference
    private lateinit var myPreferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        // Initialize variabel
        mLoading = ProgressDialog(this@SignUpActivity)
        mLoading.setCancelable(false)
        mLoading.setMessage("Loading ...")

        mDatabase = FirebaseDatabase.getInstance().getReference("User")
        myPreferences = MySharedPreferences(this@SignUpActivity)

        // OnClick text (Sudah punya akun? klik untuk masuk)
        tvSignIn.setOnClickListener {
            // To Intent (Pindah Activity)
            val goSignIn = Intent(
                this@SignUpActivity, SignInActivity::class.java
            )
            startActivity(goSignIn)
            finish() // To destroy last activity
        }

        // OnClick button SIGN UP
        btnSignUp.setOnClickListener {
            // Menjalankan program pada 'validate()'
            // Jika form sudah terisi semua, maka program pada 'signUp()' akan di jalankan
            if (validate()) {
                // To get value from Edit Text
                val mFirstName = etFirstName.text.toString()
                val mLastName = etLastName.text.toString()
                val mEmail = etEmail.text.toString()
                val mPassword = etPassword.text.toString()

                signUp(mFirstName, mLastName, mEmail, mPassword)
            }
        }

    }

    private fun validate(): Boolean {
        // Cek apakah form sudah terisi atau belum
        if (etFirstName.text.isEmpty()) {
            etFirstName.requestFocus()
            etFirstName.error = "Enter your First Name"
            return false
        }
        if (etLastName.text.isEmpty()) {
            etLastName.requestFocus()
            etLastName.error = "Enter your Last Name"
            return false
        }
        if (etEmail.text.isEmpty()) {
            etEmail.requestFocus()
            etEmail.error = "Enter your Email"
            return false
        }
        if (etPassword.text.isEmpty()) {
            etPassword.requestFocus()
            etPassword.error = "Enter your Password"
            return false
        }
        return true
    }

    private fun signUp(
        mFirstName: String, mLastName: String, mEmail: String, mPassword: String
    ) {
        // Menampilkan Loading
        mLoading.show()

        // Cek apakah email sudah digunakan atau belum
        val cekEmail = mDatabase.orderByChild("email").equalTo(mEmail)

        cekEmail.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                // Menghilangkan Loading
                mLoading.dismiss()
                Toast.makeText(
                    this@SignUpActivity,
                    "${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null) {
                    // Get Date Time Now
                    val mCurrentTime =
                        SimpleDateFormat("yyyyMMdd:HHmmss", Locale.getDefault())
                            .format(Date())

                    val user = User(
                        mCurrentTime, mFirstName, mLastName, mEmail, mPassword
                    ) // Mengisi variabel pada model User
                    mDatabase.child(mCurrentTime).setValue(user)

                    // Menyimpan data ke shared preferences bahwa user telah berhasil masuk
                    myPreferences.setValue("user", "signIn")

                    // Menyimpan data user yang sudah masuk ke shared preferences
                    myPreferences.setValue("id", user.id)
                    myPreferences.setValue("firstname", user.firstName)
                    myPreferences.setValue("lastname", user.lastName)
                    myPreferences.setValue("email", user.email)
                    myPreferences.setValue("password", user.password)

                    // Intent ke MainActivity
                    val goMain = Intent(
                        this@SignUpActivity, MainActivity::class.java
                    )
                    startActivity(goMain)
                    finish()

                    // Menghilangkan Loading
                    mLoading.dismiss()

                } else {
                    // Menghilangkan Loading
                    mLoading.dismiss()
                    Toast.makeText(
                        this@SignUpActivity,
                        "Email sudah digunakan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })
    }

}