package com.moa.moa.Local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AlarmDao{
    @Query("SELECT * FROM alarmHistory")
    fun getAll():List<AlarmHistory>

    @Query("SELECT * FROM alarmHistory where isChecked=0")
    fun getNotRead():List<AlarmHistory>

    @Insert
    fun insertHistory(history: AlarmHistory)

    @Query("UPDATE alarmHistory SET isChecked=1")
    fun setChecked()

    @Query("DELETE FROM alarmHistory")
    fun deleteAll()
}