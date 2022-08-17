package com.moa.moa.Local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


//@Dao
//interface ComplementDao{
//    @Query("SELECT * FROM complement")
//    fun getAll():List<Complement>
//
//    @Query("SELECT * FROM complement where isUsed=:isUsed")
//    fun getComplements(isUsed:Boolean):List<Complement>
//
//    @Insert
//    fun insertComplement(complement: Complement)
//
//    @Query("UPDATE complement SET isUsed='true' where uid=:uid")
//    fun useComplement(uid:Int)
//
//    @Query("DELETE FROM complement where uid=:id")
//    fun delete(id:Int)
//}