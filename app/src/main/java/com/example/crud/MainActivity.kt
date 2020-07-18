package com.example.crud

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crud.adapter.KoleksiRecyclerAdapter
import com.example.crud.auth.SignInActivity
import com.example.crud.manage.CreateActivity
import com.example.crud.model.Koleksi
import com.example.crud.utils.MySharedPreferences
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var mLoading: ProgressDialog
    private lateinit var mDatabase: DatabaseReference
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Initialize variabel
        mLoading = ProgressDialog(this@MainActivity)
        mLoading.setCancelable(false)
        mLoading.setMessage("Loading ...")

        mDatabase = FirebaseDatabase.getInstance().getReference("Koleksi")
        myPreferences = MySharedPreferences(this@MainActivity)
        userId = myPreferences.getValue("id")!!

        // Mengambil data dari shared preferences
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
            val goSignIn = Intent(
                this@MainActivity, SignInActivity::class.java
            )
            startActivity(goSignIn)
            finish() // To destroy last activity
        }

        btnCreate.setOnClickListener {
            val createKoleksi = Intent(
                this@MainActivity, CreateActivity::class.java
            ).putExtra("userId", userId) // Pindah activity sambil mengirim data
            startActivity(createKoleksi)
            finish()
        }

        setRecylcerView()

    }

    private fun setRecylcerView() {
        mLoading.show()
        mDatabase.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                // Menghilangkan Loading
                mLoading.dismiss()
                Toast.makeText(
                    this@MainActivity,
                    "${error.message}", Toast.LENGTH_SHORT
                ).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val listData = ArrayList<Koleksi>()
                    for (item in snapshot.children) {
                        val koleksi = item.getValue(Koleksi::class.java)
                        listData.add(koleksi!!)
                    }
                    rvItem.apply {
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        adapter = KoleksiRecyclerAdapter(this@MainActivity, listData)
                    }
                    mLoading.dismiss()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Anda belum menambahkan koleksi",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    mLoading.dismiss()
                }
            }

        })
    }

}