package com.farras.mytrackpro.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.farras.mytrackpro.R
import com.farras.mytrackpro.models.Costumers
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ListSelectionRecyclerViewAdapter(var data:List<Costumers>): RecyclerView.Adapter<ListSelectionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListSelectionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_selection_view_holder,
            parent, false)
        return ListSelectionViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ListSelectionViewHolder, position: Int) {
//        holder.testNama.text = data.get(position).costumerNam/e
        holder.costuserName.text = data.get(position).costumerName

//        Koreksi tipe date
//        holder.costumerDate.text = stringtoDate()
        holder.costumerTypePhone.text = data.get(position).costumerTypePhone
        holder.costumerProblemStatus.text = data.get(position).notes
        holder.costumerPrice.text = data.get(position).price
//        Log.d("MYFRUS", "${data.get(position).costumerName}")
    }

    override fun getItemCount(): Int {
        return data.size
    }


    fun stringtoDate(dates: String): Date {
        val sdf = SimpleDateFormat("EEE, MMM dd yyyy",
            Locale.ENGLISH)
        var date: Date? = null
        try {
            date = sdf.parse(dates)
            println(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date!!
    }


}


class ListSelectionViewHolder(var view:View): RecyclerView.ViewHolder(view) {
    val costuserName = view.findViewById<TextView>(R.id.rvh_costumer_name)
    val costumerDate = view.findViewById<TextView>(R.id.rvh_costumer_time)
    val costumerTypePhone = view.findViewById<TextView>(R.id.rvh_costumer_type_phone)
    val costumerProblemStatus = view.findViewById<TextView>(R.id.rvh_costumer_problem_status)
    val costumerPrice = view.findViewById<TextView>(R.id.rvh_costumer_cost)

}