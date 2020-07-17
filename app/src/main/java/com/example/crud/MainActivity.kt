package com.example.crud

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.crud.auth.SignInActivity
import com.example.crud.model.User
import com.example.crud.utils.MySharedPreferences
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var myPreferences: MySharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myPreferences = MySharedPreferences(this@MainActivity)

        tvUsername.text = myPreferences.getValue("firstname")

        btnLogout.setOnClickListener {
            // Menyimpan data bahwa user telah berhasil masuk
            myPreferences.setValue("user", "")

            // Menyimpan data user yang sudah masuk
            myPreferences.setValue("id", "")
            myPreferences.setValue("firstname", "")
            myPreferences.setValue("lastname", "")
            myPreferences.setValue("email", "")
            myPreferences.setValue("password", "")

            // To Intent (Pindah Activity)
            val goSignIn = Intent(this@MainActivity, SignInActivity::class.java)
            startActivity(goSignIn)
            finish() // To destroy last activity
        }
    }
}