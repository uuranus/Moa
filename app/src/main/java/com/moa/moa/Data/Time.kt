package com.moa.moa.Data

data class Time(val hour:Int, val minute:Int){
    val timeText:String
    get() {
        val h="%02d".format(hour)
        val m="%"
    }
}
