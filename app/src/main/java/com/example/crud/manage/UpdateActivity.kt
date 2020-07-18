package com.example.crud.manage

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.crud.MainActivity
import com.example.crud.R
import com.example.crud.model.Koleksi
import com.example.crud.utils.MySharedPreferences
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_update.*

class UpdateActivity : AppCompatActivity() {
    private lateinit var mLoading: ProgressDialog
    private lateinit var mDatabase: DatabaseReference
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var userId: String
    private lateinit var koleksiId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        // Initialize variabel
        mLoading = ProgressDialog(this@UpdateActivity)
        mLoading.setCancelable(false)
        mLoading.setMessage("Loading ...")

        mDatabase = FirebaseDatabase.getInstance().getReference("Koleksi")
        myPreferences = MySharedPreferences(this@UpdateActivity)
        userId = myPreferences.getValue("id")!!

        if (intent != null) {
            koleksiId = intent.getStringExtra("koleksiId")
        }

        setValue()

        btnUpdate.setOnClickListener {
            if (validate()) {
                val mNama = etNama.text.toString()
                val mJumlah = etJumlah.text.toString().toInt()
                create(mNama, mJumlah)
            }
        }

    }

    private fun setValue() {
        mLoading.show()
        mDatabase.child(userId).child(koleksiId)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    mLoading.dismiss()
                    Toast.makeText(
                        this@UpdateActivity,
                        "${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    mLoading.dismiss()
                    val koleksi = snapshot.getValue(Koleksi::class.java)
                    etNama.setText(koleksi!!.nama)
                    etJumlah.setText(koleksi.jumlah.toString())
                }

            })
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
        val koleksi = Koleksi(koleksiId, mNama, mJumlah)
        mDatabase.child(userId).child(koleksiId).setValue(koleksi)
        val goMain = Intent(this@UpdateActivity, MainActivity::class.java)
        startActivity(goMain)
        finish()
        mLoading.dismiss()
    }
}