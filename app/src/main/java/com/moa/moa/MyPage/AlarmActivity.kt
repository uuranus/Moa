package com.moa.moa.MyPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        Thread{
            val alarms=db.alarmDao().getAll().reversed()

            runOnUiThread {
                adapter.submitList(alarms)
            }
        }.start()
    }

    private fun checked(){
        Thread{
            db.alarmDao().setChecked()
        }.start()
    }
}