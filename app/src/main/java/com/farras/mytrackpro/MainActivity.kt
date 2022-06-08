package com.farras.mytrackpro

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.farras.mytrackpro.data.DataStatus
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
var isFabOpen: Boolean = false
var isFirstAnime: Boolean = true
var isBackClick: Boolean = false
const val DURATION_OF_ME = 300

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


        binding.fabMenus.setOnClickListener {
            if (!isFabOpen){
                fabShow()
            }
            else {
                fabHide()
            }
        }

        // add status
        binding.fabAdd.setOnClickListener {
            fabHide()
            val view = layoutInflater.inflate(R.layout.add_costumers_modal, null)
            val dialog = AlertDialog.Builder(this)
            dialog.setView(view)

            val costName = view.findViewById<EditText>(R.id.costumer_name)
            val costPhoneNumber = view.findViewById<EditText>(R.id.costumerPhoneNumber)
            val costTypePhone = view.findViewById<EditText>(R.id.costumerTypePhone)
            val costPrice = view.findViewById<EditText>(R.id.costumerPrice)


            dialog.setPositiveButton("Enter", DialogInterface.OnClickListener { _, _ ->

                writeNewCstumer(
                    getRandomString(5),
                    costName.text.toString(),
                    System.currentTimeMillis().toString(),
                    costPhoneNumber.text.toString(),
                    costTypePhone.text.toString(),
                    costPrice.text.toString()
                )

                Toast.makeText(this, "Updated", Toast.LENGTH_LONG).show()
            })
            dialog.show()
        }

        // show all status
        binding.fabList.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("status", DataStatus.ALL)
            startActivity(intent)
        }

        binding.mcvWaitingList.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("status",DataStatus.WAITING_LIST)
            startActivity(intent)
        }
        binding.mcvIdentify.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("status",DataStatus.IDENTIFICATION)
            startActivity(intent)
        }
        binding.mcvOnProcess.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("status",DataStatus.ON_GOING)
            startActivity(intent)
        }
        binding.mcvOnFinalTouch.setOnClickListener {
            val intent = Intent(this, ListActivity::class.java)
            intent.putExtra("status",DataStatus.FINAL_TOUCH)
            startActivity(intent)
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

//                    Log.d("FireBase", "${data.key.toString()}")
//                    Log.d("FireBase", "${data.getValue<Costumers>()?.costumerName}")
                }

                Log.d("FIREBASE MAP", mutableList.filterValues {
                    it.status.equals("Waiting List")
                }.size.toString())

                // set up data to dashboard
                val countWaitingList = mutableList.filterValues { it.status.equals(DataStatus.WAITING_LIST) }.size
                val countIdentify = mutableList.filterValues { it.status.equals(DataStatus.IDENTIFICATION) }.size
                val countOnProses = mutableList.filterValues { it.status.equals(DataStatus.ON_GOING) }.size
                val countFinal = mutableList.filterValues { it.status.equals(DataStatus.FINAL_TOUCH) }.size

                binding.jmlOrder.text = mutableList.size.toString()
                binding.countWl.text = countWaitingList.toString()
                binding.countIdentify.text = countIdentify.toString()
                binding.countOnProcess.text = countOnProses.toString()
                binding.countOnFinish.text = countFinal.toString()
                showPieChart(countWaitingList,countIdentify,countOnProses,countFinal, isFirstAnime)
                isFirstAnime = false
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

    fun showPieChart(waitingListNumbers:Int, identificationNumbers:Int, onPrecessNumbers:Int, finalTouchNumbers:Int,firstAnim: Boolean = true) {
        var entry = ArrayList<PieEntry>()

        entry.add(PieEntry(waitingListNumbers.toFloat(), "Waiting List"))
        entry.add(PieEntry(identificationNumbers.toFloat(), "Identification"))
        entry.add(PieEntry(onPrecessNumbers.toFloat(), "On Processing"))
        entry.add(PieEntry(finalTouchNumbers.toFloat(), "Final Touch"))

//        binding.pcTaskCount.setNoDataText("Data tidak tersedia")

        val dataSet = PieDataSet(entry, "Count Task")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 80f)
        dataSet.selectionShift = 4f
        dataSet.setDrawValues(true)
        if (isFirstAnime){
            binding.pcTaskCount.animateY(1400, Easing.EaseInOutQuad)
        }



        val arrayListColor = ArrayList<Int>()
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

    private fun fabShow(): Unit {
        isFabOpen = true
        binding.fabMenus.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_clear_18))

//        set translation X
        binding.cardfab.animate().translationX(-resources.getDimension(R.dimen.standar_90))

        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {

                //Change dp to px
                val myScale = resources.displayMetrics.density
                var myPx = (60 * interpolatedTime * myScale + 0.5f).toInt()

                //set left margin
                val paramsMargin: FrameLayout.LayoutParams =
                    binding.fabAdd.layoutParams as FrameLayout.LayoutParams
                paramsMargin.setMargins(myPx, 0, 0, 0)
                binding.fabAdd.layoutParams = paramsMargin
            }
        }
        animation.duration = DURATION_OF_ME.toLong()
        binding.fabMenus.startAnimation(animation)
    }

    private fun fabHide ():Unit {
        isFabOpen = false
        binding.fabMenus.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_baseline_menu_open_24))


        val animationClose = object : Animation () {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                //animasi translation x
                var currentTranslationX = binding.cardfab.translationX - (binding.cardfab.translationX * interpolatedTime)
                binding.cardfab.animate().translationX(currentTranslationX)

                //set left margin to 0
                val paramsMargin: FrameLayout.LayoutParams = binding.fabAdd.layoutParams as FrameLayout.LayoutParams
                // get curent margin left and reduce as iterpolated time
                var currentMargin = paramsMargin.leftMargin - (paramsMargin.leftMargin * interpolatedTime).toInt()
                paramsMargin.setMargins(currentMargin,0,0,0)
                binding.fabAdd.layoutParams = paramsMargin
                Log.d("fabShow","${currentMargin}")
            }
        }

        animationClose.duration = DURATION_OF_ME.toLong()
        binding.fabMenus.startAnimation(animationClose)
    }
}