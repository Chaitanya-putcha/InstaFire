package com.example.instafire.models

import com.google.firebase.database.PropertyName
import com.google.firebase.firestore.auth.User
import com.google.j2objc.annotations.Property

data class Post(
    var description:String = "",
    var image_url:String = "",
    var creation_time:Long = 0,
    var user: Users? = null
)