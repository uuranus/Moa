package com.moa.moa.Local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities=[TitleHistory::class,AlarmHistory::class],version=5)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun titleDao(): TitleDao
    abstract fun alarmDao(): AlarmDao
}