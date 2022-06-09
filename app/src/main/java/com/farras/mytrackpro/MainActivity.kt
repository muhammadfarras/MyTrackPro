package com.farras.mytrackpro

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farras.mytrackpro.adapter.ListSelectionRecyclerViewAdapter
import com.farras.mytrackpro.data.DataStatus
import com.farras.mytrackpro.databinding.ActivityMainBinding
import com.farras.mytrackpro.models.Costumers
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

private lateinit var binding: ActivityMainBinding
private lateinit var database: DatabaseReference
var isFabOpen: Boolean = false
var isFirstAnime: Boolean = true
lateinit var recyclerView: RecyclerView
const val DURATION_OF_ME = 300

class MainActivity : AppCompatActivity(), OnChartValueSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup firebase database
        FirebaseDatabase.getInstance("https://mytrackpro-5209b-default-rtdb.asia-southeast1.firebasedatabase.app/")
        database = Firebase.database.getReference("costumers")

        val mutableList = mutableMapOf<String, Costumers>()
        val realMutableList = mutableListOf<Costumers>()


        showBarChart()


        // readeing all data
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // delete mutable list of
                mutableList.clear()
                for (data in snapshot.children) {
                    val dataUsed = data.getValue<Costumers>()
                    mutableList[data.key.toString()] = Costumers(
                        dataUsed?.costumerName.toString(),
                        dataUsed?.orderDate.toString(),
                        dataUsed?.costumerPhoneNumber.toString(),
                        dataUsed?.costumerTypePhone.toString(),
                        dataUsed?.price.toString(),
                        dataUsed?.status.toString()
                    )

                    realMutableList.add(Costumers(
                        dataUsed?.costumerName.toString(),
                        dataUsed?.orderDate.toString(),
                        dataUsed?.costumerPhoneNumber.toString(),
                        dataUsed?.costumerTypePhone.toString(),
                        dataUsed?.price.toString(),
                        dataUsed?.status.toString(),
                        dataUsed?.notes.toString()
                    )

                    )

//                    Log.d("FireBase", "${data.key.toString()}")
//                    Log.d("FireBase", "${data.getValue<Costumers>()?.costumerName}")
                }

                Log.d("FIREBASE MAP", mutableList.filterValues {
                    it.status.equals("Waiting List")
                }.size.toString())
                recyclerView.adapter = ListSelectionRecyclerViewAdapter (realMutableList, applicationContext)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })




    }

    fun writeNewCstumer(
        userId: String?, costumerName: String, orderDate: String,
        phoneNumber: String, phoneType: String,
        price: String
    ) {
        val costumer =
            Costumers(costumerName, orderDate, phoneNumber, phoneType,
                price, DataStatus.WAITING_LIST,"")
        if (userId != null) {
            database.child(userId).setValue(costumer)
        }
    }

    fun getRandomString(length: Int): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        return List(length) { charset.random() }
            .joinToString("")
    }

    fun showBarChart() {
        // instance bar chart
        binding.bcTaskCount.setDrawBarShadow(false)
        binding.bcTaskCount.description.isEnabled = false
        val daysOnWeek = listOf<String>("Mon","Tue","Wed","Thu","Fri","Sat","Sun")

        // maksimum entries
        binding.bcTaskCount.setMaxVisibleValueCount(60)
        binding.bcTaskCount.setBackgroundColor(Color.WHITE)
        binding.bcTaskCount.setGridBackgroundColor(Color.WHITE)
        binding.bcTaskCount.isDoubleTapToZoomEnabled = false
        binding.bcTaskCount.setDrawGridBackground(false)
        binding.bcTaskCount.setPinchZoom(false)
        binding.bcTaskCount.setDrawBorders(false)
        binding.bcTaskCount.xAxis.setDrawAxisLine(false)
        binding.bcTaskCount.xAxis.setDrawGridLines(false)
        binding.bcTaskCount.xAxis.setDrawLimitLinesBehindData(false)
        binding.bcTaskCount.axisLeft.isEnabled = false
        binding.bcTaskCount.axisRight.isEnabled = false
        binding.bcTaskCount.axisLeft.axisMinimum = 0f
        binding.bcTaskCount.xAxis.valueFormatter = IndexAxisValueFormatter(daysOnWeek)
        binding.bcTaskCount.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.bcTaskCount.legend.isEnabled = false
        // create value
        val values = arrayListOf<BarEntry>(
            BarEntry(0f,19f),
            BarEntry(1f,22f),
            BarEntry(2f,18f),
            BarEntry(3f,23f),
            BarEntry(4f,19f),
            BarEntry(5f,20f),
            BarEntry(6f,17f),
        )

        val set1 = BarDataSet(values,"")
        val dataSets = arrayListOf<IBarDataSet>()
        dataSets.add(set1)
        val barData = BarData(dataSets)
        barData.barWidth = 0.7f
        binding.bcTaskCount.data = barData
    }

    private fun generateCenterSpannableText(): SpannableString {
        val s = SpannableString("MyTrack Pro\nby Madina")
        s.setSpan(RelativeSizeSpan(1.2f), 0, 12, 0)
        s.setSpan(StyleSpan(Typeface.NORMAL), 0, 12, 0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 12, s.length - 7, 0)
        s.setSpan(RelativeSizeSpan(.8f), 12, s.length - 7, 0)
        s.setSpan(StyleSpan(Typeface.ITALIC), s.length - 7, s.length, 0)
        s.setSpan(ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length - 7, s.length, 0)
        return s
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {

    }

    override fun onNothingSelected() {

    }

}