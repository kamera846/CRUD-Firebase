package com.example.crud.auth

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.crud.MainActivity
import com.example.crud.R
import com.example.crud.model.User
import com.example.crud.utils.MySharedPreferences
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.etEmail
import kotlinx.android.synthetic.main.activity_sign_in.etPassword
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.text.SimpleDateFormat
import java.util.*

class SignInActivity : AppCompatActivity() {
    private lateinit var mLoading: ProgressDialog
    private lateinit var mDatabase: DatabaseReference
    private lateinit var myPreferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        // Initialize variabel
        mLoading = ProgressDialog(this@SignInActivity)
        mLoading.setCancelable(false)
        mLoading.setMessage("Loading ...")

        mDatabase = FirebaseDatabase.getInstance().getReference("User")
        myPreferences = MySharedPreferences(this@SignInActivity)

        // Cek apakah user sudah Sign In atau belum
        // Jika sudah maka akan langsung Intent ke MainActivity
        if (myPreferences.getValue("user").equals("signIn")) {
            val goMain = Intent(this@SignInActivity, MainActivity::class.java)
            startActivity(goMain)
            finish()
            return // Agar program di bawah line ini tidak di jalankan
        }

        tvSignUp.setOnClickListener {
            // To Intent (Pindah Activity)
            val goSignIn = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(goSignIn)
            finish() // To destroy last activity
        }

        btnSignIn.setOnClickListener {
            // To get value from Edit Text
            val mEmail = etEmail.text.toString()
            val mPassword = etPassword.text.toString()

            // Menjalankan program pada 'validate()'
            // Jika form sudah terisi semua, maka program pada 'signUp()' akan di jalankan
            if (validate()) {
                signUp(mEmail, mPassword)
            }
        }

    }

    private fun validate(): Boolean {
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

    private fun signUp(mEmail: String, mPassword: String) {
        // Menampilkan Loading
        mLoading.show()

        // Cek apakah email sudah terdaftar atau belum
        val cekEmail = mDatabase.orderByChild("email").equalTo(mEmail)

        cekEmail.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                // Menghilangkan Loading
                mLoading.dismiss()
                Toast.makeText(this@SignInActivity, "${error.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    var user: User? = null

                    for (item in snapshot.children) {
                        user = item.getValue(User::class.java) // Mengisi variabel pada model User
                    }

                    if (user!!.password == mPassword) {
                        // Menyimpan data bahwa user telah berhasil masuk
                        myPreferences.setValue("user", "signIn")

                        // Menyimpan data user yang sudah masuk
                        myPreferences.setValue("id", user.id)
                        myPreferences.setValue("firstname", user.firstName)
                        myPreferences.setValue("lastname", user.lastName)
                        myPreferences.setValue("email", user.email)
                        myPreferences.setValue("password", user.password)

                        // Intent ke MainActivity
                        val goMain = Intent(this@SignInActivity, MainActivity::class.java)
                        startActivity(goMain)
                        finish()

                        // Menghilangkan Loading
                        mLoading.dismiss()
                    } else {
                        // Menghilangkan Loading
                        mLoading.dismiss()
                        Toast.makeText(this@SignInActivity, "Password salah", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    // Menghilangkan Loading
                    mLoading.dismiss()
                    Toast.makeText(this@SignInActivity, "Email belum terdaftar", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        })
    }
}