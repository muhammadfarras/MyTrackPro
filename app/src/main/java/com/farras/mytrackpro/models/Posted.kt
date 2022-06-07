package com.farras.mytrackpro.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Posted(var userName:String?=null, val email:String?=null)
