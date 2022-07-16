package com.moa.moa.Work

import androidx.room.Database
import androidx.room.RoomDatabase
import com.moa.moa.Data.TitleHistory

@Database(entities=[TitleHistory::class],version=1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun titleDao(): TitleDao
}