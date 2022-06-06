package com.farras.mytrackpro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.farras.mytrackpro.databinding.ActivityMainBinding
import com.farras.mytrackpro.models.OrderItems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        FirebaseDatabase.getInstance("https://mytrackpro-5209b-default-rtdb.asia-southeast1.firebasedatabase.app/" +
                "")
        val database = Firebase.database
        val myRef = database.getReference("daftar_order")
//        myRef.setValue("User value 1")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

//                val list = snapshot.children.map { it.getValue(Response::class.java) }
                val value = snapshot.getValue()
                Log.d("FIREBASE", "Value is : ${value}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FIREBASE", "Failed to read value.", error.toException())

            }

        })
    }
}