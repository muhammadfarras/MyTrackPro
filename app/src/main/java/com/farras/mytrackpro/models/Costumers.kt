package com.farras.mytrackpro.models

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class Costumers(
    var costumerName:String?="",
    var orderDate:String?="",
    var costumerPhoneNumber:String?="",
    var costumerTypePhone:String?="",
    var price:String?="",
    var status:String?="",
    var notes:String?=""
)
