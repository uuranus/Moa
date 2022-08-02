package com.moa.moa

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

    fun deletePreviousEvent(workId:String,period: Int,groupId:String){
        val database=FirebaseDatabase.getInstance().reference
        val today=getToday()

        for(i in 0..period) {
            val date=addDateFrom(today, i)
            Log.i("finddate",date)
            val datestr = date.split("-")
            database.child("group").child(groupId).child("log")
                .child(datestr[0]).child(datestr[1]).child(datestr[2]).get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result ?: return@addOnCompleteListener

                        if (it.result.hasChild(workId)) {
                            Log.i("isFind?",date)
                            deleteWork(workId,date,period,groupId,database)
                            return@addOnCompleteListener
                        }
                    }
                }

        }

    }

    private fun deleteWork(workId:String,date:String,period:Int,groupId: String,database:DatabaseReference){
        var curdate=addDateFrom(date,period)
        val dateAfter=getThreeMonthAfter(date)
        while(curdate<dateAfter){
            Log.i("delete....",curdate+" "+workId)
            val dates=curdate.split("-")
            database.child("group").child(groupId).child("log").child(dates[0]).child(dates[1]).child(dates[2])
                .child(workId.toString()).removeValue()

            curdate=addDateFrom(curdate,period)
        }
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