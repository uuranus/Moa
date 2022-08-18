package com.moa.moa.Data

data class User(
    var userId:String="",
    var userName:String="",
    var profileImage:String="",
    var starCount:ArrayList<Int>?=null,
    var badges:ArrayList<Badge>?=null,
)
