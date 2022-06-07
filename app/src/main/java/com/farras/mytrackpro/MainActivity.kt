package com.farras.mytrackpro

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.farras.mytrackpro.databinding.ActivityMainBinding
import com.farras.mytrackpro.models.Costumers
import com.farras.mytrackpro.models.Posted
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

private lateinit var binding: ActivityMainBinding
private lateinit var database: DatabaseReference

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Setup firebase database
        FirebaseDatabase.getInstance("https://mytrackpro-5209b-default-rtdb.asia-southeast1.firebasedatabase.app/")
        database = Firebase.database.getReference("costumers")

        var mutableList = mutableMapOf<String, Costumers>()


        binding.fabAddCostumers.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.add_costumers_modal,null)
            val dialog = AlertDialog.Builder(this)
            dialog.setView(view)

            val costName = view.findViewById<EditText>(R.id.costumer_name)
            val costPhoneNumber = view.findViewById<EditText>(R.id.costumerPhoneNumber)
            val costTypePhone = view.findViewById<EditText>(R.id.costumerTypePhone)
            val costPrice = view.findViewById<EditText>(R.id.costumerPrice)


            dialog.setPositiveButton("Enter", DialogInterface.OnClickListener { _, _ ->

                writeNewCstumer(getRandomString(5),costName.text.toString(), System.currentTimeMillis().toString(),
            costPhoneNumber.text.toString(),costTypePhone.text.toString(),costPrice.text.toString())

            Toast.makeText(this, "Updated", Toast.LENGTH_LONG).show()
            })

            dialog.show()

        }

//        FirebaseDatabase.getInstance("https://mytrackpro-5209b-default-rtdb.asia-southeast1.firebasedatabase.app/" +
//                "")
//
//        database = Firebase.database.getReference("costumers")
//
//        val key= database.push().key

//        database.child("Jun2").addValueEventListener(object: ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                var user = snapshot.getValue<Costumers>()
//                Log.d("FireBase", "${user?.orderDate} - ${user?.costumerTypePhone}")
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })

        // readeing all data
        database.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // delete mutable list of
                mutableList.clear()
                for (data in snapshot.children){
                    val dataUsed = data.getValue<Costumers>()
                    mutableList[data.key.toString()] = Costumers(dataUsed?.costumerName.toString(),dataUsed?.orderDate.toString(),dataUsed?.costumerPhoneNumber.toString()
                        ,dataUsed?.costumerTypePhone.toString(),dataUsed?.price.toString(),dataUsed?.status.toString())

//                    Log.d("FireBase", "${data.key.toString()}")
//                    Log.d("FireBase", "${data.getValue<Costumers>()?.costumerName}")
                }

                Log.d("FIREBASE MAP", mutableList.filterValues {
                    it.status.equals("Waiting List")
                }.size.toString())
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })




    }

    fun writeNewCstumer(
        userId: String?, costumerName:String, orderDate:String,
        phoneNumber:String, phoneType:String,
        price: String
    ){
        val costumer = Costumers(costumerName, orderDate, phoneNumber, phoneType, price,"Waiting List")
        if (userId != null) {
            database.child(userId).setValue(costumer)
        }
    }

    fun getRandomString(length: Int) : String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return List(length) { charset.random() }
            .joinToString("")
    }
}