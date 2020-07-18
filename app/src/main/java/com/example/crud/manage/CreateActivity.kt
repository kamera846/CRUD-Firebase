package com.example.crud.manage

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.crud.MainActivity
import com.example.crud.R
import com.example.crud.model.Koleksi
import com.example.crud.utils.MySharedPreferences
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create.*
import java.text.SimpleDateFormat
import java.util.*

class CreateActivity : AppCompatActivity() {
    private lateinit var mLoading: ProgressDialog
    private lateinit var mDatabase: DatabaseReference
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        // Initialize variabel
        mLoading = ProgressDialog(this@CreateActivity)
        mLoading.setCancelable(false)
        mLoading.setMessage("Loading ...")

        mDatabase = FirebaseDatabase.getInstance().getReference("Koleksi")
        myPreferences = MySharedPreferences(this@CreateActivity)

        // mengambil data yang di kirim melalui intent
        if (intent != null) {
            userId = intent.getStringExtra("userId")
        }

        btnTambah.setOnClickListener {
            if (validate()) {
                val mNama = etNama.text.toString()
                val mJumlah = etJumlah.text.toString().toInt()

                create(mNama, mJumlah)
            }
        }

    }

    private fun validate(): Boolean {
        // Cek apakah form sudah terisi atau belum
        if (etNama.text.isEmpty()) {
            etNama.requestFocus()
            etNama.error = "Masukan nama koleksi"
            return false
        }
        if (etJumlah.text.isEmpty()) {
            etJumlah.requestFocus()
            etJumlah.error = "Masukan jumlah koleksi"
            return false
        }
        return true
    }

    private fun create(mNama: String, mJumlah: Int) {
        mLoading.show()

        // Get Date Time Now
        val mCurrentTime = SimpleDateFormat("yyyyMMdd:HHmmss", Locale.getDefault())
            .format(Date())

        // Mengisi variabel pada model Koleksi
        val koleksi = Koleksi(mCurrentTime, mNama, mJumlah)
        mDatabase.child(userId).child(mCurrentTime).setValue(koleksi)

        // Intent ke MainActivity
        val goMain = Intent(
            this@CreateActivity, MainActivity::class.java
        )
        startActivity(goMain)
        finish()

        // Menghilangkan Loading
        mLoading.dismiss()
    }

}