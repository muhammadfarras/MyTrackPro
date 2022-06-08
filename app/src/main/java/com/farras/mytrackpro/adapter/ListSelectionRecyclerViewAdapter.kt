package com.farras.mytrackpro.adapter

import android.annotation.SuppressLint
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

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ListSelectionViewHolder, position: Int) {
        holder.costuserName.text = "${data[position].costumerName} | ${data[position].status}"

//        Koreksi tipe date
        val formatedDate = data[position].orderDate?.let { dateFormatter(it) }
        holder.costumerDate.text = formatedDate
        holder.costumerTypePhone.text = data[position].costumerTypePhone
        holder.costumerProblemStatus.text = data[position].notes
        holder.costumerPrice.text = data[position].price
        Log.d("MYFRUS", "${data[position]}")
    }

    override fun getItemCount(): Int {
        return data.size
    }

    @SuppressLint("SimpleDateFormat")
    fun dateFormatter(milliseconds: String): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date(milliseconds.toLong())).toString()
    }


}


class ListSelectionViewHolder(var view:View): RecyclerView.ViewHolder(view) {
    val costuserName = view.findViewById<TextView>(R.id.rvh_costumer_name)
    val costumerDate = view.findViewById<TextView>(R.id.rvh_costumer_time)
    val costumerTypePhone = view.findViewById<TextView>(R.id.rvh_costumer_type_phone)
    val costumerProblemStatus = view.findViewById<TextView>(R.id.rvh_costumer_problem_status)
    val costumerPrice = view.findViewById<TextView>(R.id.rvh_costumer_cost)

}