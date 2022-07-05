package com.moa.moa

import android.content.Context
import androidx.fragment.app.FragmentActivity


class Utility () {

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
}