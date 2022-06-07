package com.farras.mytrackpro

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.farras.mytrackpro.databinding.ActivityMainBinding
import com.farras.mytrackpro.models.Costumers
import com.farras.mytrackpro.models.Posted
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

private lateinit var binding: ActivityMainBinding
private lateinit var database: DatabaseReference

class MainActivity : AppCompatActivity(), OnChartValueSelectedListener {
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

                showPieChart()
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

    fun showPieChart(){
        var entry = ArrayList<PieEntry>()

        entry.add(PieEntry(25f,"Waiting List"))
        entry.add(PieEntry(10f,"Identifikasi"))
        entry.add(PieEntry(1f,"Pengerjaan"))
        entry.add(PieEntry(10f,"Finishing"))

//        binding.pcTaskCount.setNoDataText("Data tidak tersedia")

        var dataSet = PieDataSet(entry, "Count Task")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f,80f)
        dataSet.selectionShift = 4f
        dataSet.setDrawValues(true)
        binding.pcTaskCount.animateY(1400, Easing.EaseInOutQuad)

        var arrayListColor = ArrayList<Int>()
        arrayListColor.add(Color.rgb(32, 214, 214))
        arrayListColor.add(Color.rgb(240, 188, 91))
        arrayListColor.add(Color.rgb(188, 240, 91))
        arrayListColor.add(Color.rgb(214, 32, 214))
        dataSet.colors = arrayListColor

        var data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(binding.pcTaskCount))

        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)

        binding.pcTaskCount.data = data

        binding.pcTaskCount.setUsePercentValues(true)
        binding.pcTaskCount.setTransparentCircleColor(Color.WHITE)
        binding.pcTaskCount.setTransparentCircleAlpha(110)
        binding.pcTaskCount.transparentCircleRadius = 61f

        binding.pcTaskCount.centerText = generateCenterSpannableText()

        binding.pcTaskCount.holeRadius = 50f
        binding.pcTaskCount.transparentCircleRadius = 60f

        binding.pcTaskCount.setDrawCenterText(true)
        binding.pcTaskCount.rotationAngle = 0f
        binding.pcTaskCount.isRotationEnabled = true


        var legend = binding.pcTaskCount.legend
        legend.isEnabled = false
        binding.pcTaskCount.highlightValue(null)
        binding.pcTaskCount.invalidate()
    }

    private fun generateCenterSpannableText() : SpannableString {
        val s = SpannableString("MyTrack Pro\nFarras Ma'ruf")
        s.setSpan(RelativeSizeSpan(1.2f), 0, 14, 0)
        s.setSpan(StyleSpan(Typeface.NORMAL), 0, 14, 0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 14, s.length - 10, 0)
        s.setSpan(RelativeSizeSpan(.8f), 14, s.length - 10, 0)
        s.setSpan(StyleSpan(Typeface.ITALIC), s.length - 10, s.length, 0)
        s.setSpan(ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length - 10, s.length, 0)
        return s
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {

    }

    override fun onNothingSelected() {

    }
}