package com.moa.moa.Work

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.moa.moa.Data.TitleHistory

@Database(entities=[TitleHistory::class],version=2)
abstract class AppDatabase : RoomDatabase(){
    abstract fun titleDao(): TitleDao
}