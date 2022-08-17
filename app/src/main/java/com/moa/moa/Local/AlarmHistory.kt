package com.moa.moa.Local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class AlarmHistory(
    @PrimaryKey val uid:Int?,
    @ColumnInfo(name="alarm") val title:String?,
    @ColumnInfo(name="date") val date: Date?,
    @ColumnInfo(name="isChecked") val isChecked:Int,
)
