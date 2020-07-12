package com.example.mygram.models

import com.google.firebase.firestore.PropertyName

data class Posts(
    var descreption:String="",
    @get:PropertyName("image_url") @set:PropertyName("image_url") var imageUrl:String="",
    @get:PropertyName("current_time") @set:PropertyName("current_time") var currentTime:Long=0,
    var user:Users?=null
)