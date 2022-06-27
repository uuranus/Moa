package com.moa.moa.Data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Group
    (var userList: List<User> = ArrayList<User>(),
     var workList: List<Work> = ArrayList<Work>(),
     var log: Log = Log("",ArrayList<Work>()),
     var userNumber:Int = 1, //구성원 숫자
     var groupName : String =""
     )
{
    @Exclude
    fun toMap() : Map<String,Any?> {
        return mapOf(
            "users" to userList,
            "works" to workList,
            "log" to log,
            "userNumber" to userNumber,
            "groupName" to groupName
        )
    }
}
