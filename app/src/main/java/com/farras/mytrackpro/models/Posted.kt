package com.farras.mytrackpro.models

data class Posted(var orderList: ArrayList<OrderItems> = arrayListOf())

data class OrderItems(
    var biaya : Int = 0,
    var jenis_handphone : String = "",
    var nama : String = "",
    var nomor_hp : String = "",
    var nomor_order : String = "",
    var status : String = "",
    var waktu_order : Int = 0
)
