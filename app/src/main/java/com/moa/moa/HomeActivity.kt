package com.moa.moa

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private val calenderMonthTextView: TextView by lazy{
        findViewById(R.id.homeCalendarMonthTextView)
    }

    private val calendarView:CompactCalendarView by lazy{
        findViewById(R.id.homeCalendar)
    }

    private val recyclerView: RecyclerView by lazy{
        findViewById(R.id.homeRecyclerView)
    }

    private var workList:List<TodayWork> = emptyList()

    private val dateFormatForMonth: SimpleDateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        init()
    }

    private fun init(){
        calenderMonthTextView.text=(dateFormatForMonth.format(calendarView.firstDayOfCurrentMonth))
        getMonthWork(calendarView.firstDayOfCurrentMonth.year,calendarView.firstDayOfCurrentMonth.month+1)

        recyclerView.adapter=HomeWorkRecyclerViewAdapter(workList)
    }

    private fun getMonthWork(year:Int,month:Int){
        val firebaseDatabase=FirebaseDatabase.getInstance().getReference()

        Log.i("year and month","$year $month")

        firebaseDatabase.child(year.toString()).child(month.toString()).get().addOnCompleteListener {
            if(it.isSuccessful){
                it.result ?:return@addOnCompleteListener


            }
        }
    }


}