package com.moa.moa.MyPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.moa.moa.Local.AppDatabase
import com.moa.moa.R
import com.moa.part3.BookReview.AlarmListAdapter

class AlarmActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase

    private val recyclerView:RecyclerView by lazy{
        findViewById(R.id.alarmListRecyclerView)
    }

    private lateinit var adapter:AlarmListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "MoaDB"
        ).fallbackToDestructiveMigration()
            .build()

        adapter= AlarmListAdapter()
        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(this)

        init()

        checked()
    }

    private fun init(){
        Log.i("activity","alarm")
        Thread{
            val alarms=db.alarmDao().getAll().reversed()

            Log.i("alarms", alarms.toString())
            runOnUiThread {
                adapter.submitList(alarms)
                Log.i("alarms22", adapter.itemCount.toString())

            }
        }.start()
    }

    private fun checked(){
        Thread{
            db.alarmDao().setChecked()
        }.start()
    }
}