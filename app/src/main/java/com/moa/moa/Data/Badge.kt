package com.moa.moa.Data

import java.io.Serializable

data class Badge(
    val name:String="",
    val description:String="",
    val get:Boolean=false,
):Serializable
