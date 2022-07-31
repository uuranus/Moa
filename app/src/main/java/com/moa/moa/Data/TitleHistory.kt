package com.moa.moa.Data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TitleHistory(
    @PrimaryKey val uid:Int?,
    @ColumnInfo(name="title") val title:String?
)
