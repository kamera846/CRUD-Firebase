package com.example.crud.adapter

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.crud.R
import com.example.crud.manage.UpdateActivity
import com.example.crud.model.Koleksi
import com.example.crud.utils.MySharedPreferences
import com.google.firebase.database.*
import java.util.ArrayList

class KoleksiRecyclerAdapter(var mContext: Context, var mData: ArrayList<Koleksi>) :
    RecyclerView.Adapter<KoleksiRecyclerAdapter.ViewHolder>() {

    private lateinit var mLoading: ProgressDialog
    private lateinit var mDatabase: DatabaseReference
    private lateinit var myPreferences: MySharedPreferences
    private lateinit var userId: String

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var mNama = v.findViewById<TextView>(R.id.tvNama)
        var mJumlah = v.findViewById<TextView>(R.id.tvJumlah)
        var btnUpdate = v.findViewById<Button>(R.id.tvUpdate)
        var btnDelete = v.findViewById<Button>(R.id.tvDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext)
            .inflate(R.layout.item_koleksi, parent, false)

        // Initialize variabel
        mLoading = ProgressDialog(mContext)
        mLoading.setCancelable(false)
        mLoading.setMessage("Loading ...")
        mDatabase = FirebaseDatabase.getInstance().getReference("Koleksi")
        myPreferences = MySharedPreferences(mContext)
        userId = myPreferences.getValue("id")!!

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mNama.text = mData[position].nama
        holder.mJumlah.text = mData[position].jumlah.toString()

        holder.btnUpdate.setOnClickListener {
            val goUpdate = Intent(mContext, UpdateActivity::class.java)
                .putExtra("koleksiId", mData[position].id)
            (mContext as Activity).startActivity(goUpdate)
            (mContext as Activity).finish()
        }

        holder.btnDelete.setOnClickListener {
            mLoading.show()
            val delete = mDatabase.child(userId)
                .orderByChild("id")
                .equalTo(mData[position].id)

            delete.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    mLoading.dismiss()
                    Toast.makeText(
                        mContext,
                        "${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    mLoading.dismiss()
                    for (item in snapshot.children) {
                        item.ref.removeValue()
                    }
                    // Refresh data
                    mData.removeAt(position)
                    notifyItemRemoved(position)
                    Toast.makeText(
                        mContext,
                        "Berhasil menghapus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
}