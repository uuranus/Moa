package com.moa.moa

import android.content.Context
import androidx.fragment.app.FragmentActivity
import java.text.SimpleDateFormat
import java.util.*


class Utility  {

     fun getGroupId(activity: FragmentActivity):String {
         val sharedPreferences=activity.getSharedPreferences("Info", Context.MODE_PRIVATE)
         val groupId=sharedPreferences.getString("groupId","")

         return groupId ?: ""

     }

    fun getUserId(activity: FragmentActivity):String{
        val sharedPreferences=activity.getSharedPreferences("Info", Context.MODE_PRIVATE)
        val userId=sharedPreferences.getString("userId","")

        return userId ?: ""

    }

    fun getToday():String{
        val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }


    fun getThreeMonthAfter(date:String):String{
        val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal=Calendar.getInstance()
        val temp=date.split("-")
        cal.set(temp[0].toInt(),(temp[1].toInt()-1),temp[2].toInt())
        cal.add(Calendar.MONTH,3)
        return dateFormat.format(cal.time)
    }

    fun addDateFrom(date:String,period:Int):String{
        val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal=Calendar.getInstance()
        val temp=date.split("-")
        cal.set(temp[0].toInt(),(temp[1].toInt()-1),temp[2].toInt())
        cal.add(Calendar.DATE,period)
        return dateFormat.format(cal.time)
    }

    fun getDateString(date:Date):String{
        val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }


}