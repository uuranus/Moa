package com.moa.moa.Work

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.moa.moa.Data.TitleHistory

@Dao
interface TitleDao{
    @Query("SELECT * FROM titleHistory")
    fun getAll():List<TitleHistory>

    @Insert
    fun insertHistory(history:TitleHistory)

    @Query("DELETE FROM titleHistory where keyword==:keyword")
    fun delete(keyword:String)
}