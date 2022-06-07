package com.farras.mytrackpro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farras.mytrackpro.adapter.ListSelectionRecyclerViewAdapter
import com.farras.mytrackpro.data.DataStatus
import com.farras.mytrackpro.databinding.ActivityListBinding
import com.farras.mytrackpro.models.Costumers
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

lateinit var activityListBinding: ActivityListBinding
lateinit var recyclerView: RecyclerView
lateinit var dataMap: List<Costumers>
class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityListBinding = ActivityListBinding.inflate(layoutInflater)
        setContentView(activityListBinding.root)


        recyclerView = activityListBinding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        FirebaseDatabase.getInstance("https://mytrackpro-5209b-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val database = Firebase.database.getReference("costumers")
        val list = mutableListOf<Costumers>()
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // delete mutable list of

                for (data in snapshot.children) {
                    val dataUsed = data.getValue<Costumers>()
                    list.add(Costumers(
                        dataUsed?.costumerName.toString(),
                        dataUsed?.orderDate.toString(),
                        dataUsed?.costumerPhoneNumber.toString(),
                        dataUsed?.costumerTypePhone.toString(),
                        dataUsed?.price.toString(),
                        dataUsed?.status.toString()
                    ))
                }
                when (intent!!.getStringExtra("status")){
                    DataStatus.WAITING_LIST -> dataMap = list.filter { it.status.equals(DataStatus.WAITING_LIST)}
                    DataStatus.IDENTIFICATION -> dataMap = list.filter { it.status.equals(DataStatus.IDENTIFICATION)}
                    DataStatus.ON_GOING -> dataMap = list.filter { it.status.equals(DataStatus.ON_GOING)}
                    DataStatus.FINAL_TOUCH -> dataMap = list.filter { it.status.equals(DataStatus.FINAL_TOUCH)}

                }

                recyclerView.adapter = ListSelectionRecyclerViewAdapter (dataMap)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
}