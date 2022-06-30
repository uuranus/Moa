package com.moa.moa

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*
import okhttp3.internal.wait
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class Utility () {

     fun getGroupId(activity: FragmentActivity):String {
//         val sharedPreferences=activity.getSharedPreferences("Info", Context.MODE_PRIVATE)
//         val groupId=sharedPreferences.getString("groupID","")
//
//         return groupId ?: ""
         return "-N5i_ow2ftDuKRjGzvgn"
     }

    fun getUserId(activity: FragmentActivity):String{
//        val sharedPreferences=activity.getSharedPreferences("Info", Context.MODE_PRIVATE)
//        val userId=sharedPreferences.getString("userID","")
//
//        return userId ?: ""

        return "asd123"
    }
}