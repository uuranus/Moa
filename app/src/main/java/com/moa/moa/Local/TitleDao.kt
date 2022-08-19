package com.moa.moa.Local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TitleDao{
    @Query("SELECT * FROM titleHistory")
    fun getAll():List<TitleHistory>

    @Query("SELECT * FROM titleHistory where title LIKE :title || '%' ")
    fun getHistory(title:String):List<TitleHistory>

    @Insert
    fun insertHistory(history: TitleHistory)

    @Query("DELETE FROM titleHistory where title=:title")
    fun delete(title:String)

    @Query("DELETE FROM titleHistory")
    fun deleteAll()
}