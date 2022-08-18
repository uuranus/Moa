package com.moa.moa.Local

import java.io.Serializable


data class Complement(
    var uid:String="",
    val star:Int=1,
    val title:String="",
    val description:String="",
    val used:Int=1
):Serializable