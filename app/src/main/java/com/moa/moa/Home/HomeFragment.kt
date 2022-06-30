package com.moa.moa.Home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.moa.moa.Data.*
import com.moa.moa.R
import com.moa.moa.Utility

import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    private val utility=Utility()

    private lateinit var groupId :String

    private lateinit var firebaseDatabase:DatabaseReference

    private val calenderMonthTextView: TextView by lazy{
        requireView().findViewById(R.id.homeCalendarMonthTextView)
    }

    private val calendarView: CompactCalendarView by lazy{
        requireView().findViewById(R.id.homeCalendar)
    }

    private val recyclerView: RecyclerView by lazy{
        requireView().findViewById(R.id.homeRecyclerView)
    }

    private var workInfos= mutableListOf<Work>()//선택한 날짜의 집안일들 정보 인덱스=집안일 번호

    private var workList= mutableListOf<HomeFirstSection>(HomeFirstSection("아직 배정되지 않았어요!",
        listOf(HomeSecondSection("0","미배정", mutableListOf()))),
            HomeFirstSection("가족들은 얼마나 했을까요?", mutableListOf())
            ) //RecyclerView에 전달하기 위한 list

    private val dateFormatForMonth: SimpleDateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault()) //달력의 월과 년도 표시용

    private fun initCalendar(){
        groupId= utility.getGroupId(requireActivity())

        getWorkInfo() //모든 집안일 정보 가져와서 workInfos에 저장
    }

    private fun init(){

        calenderMonthTextView.text=(dateFormatForMonth.format(calendarView.firstDayOfCurrentMonth))
        getMonthWork(getYear(calendarView.firstDayOfCurrentMonth),getMonth(calendarView.firstDayOfCurrentMonth)) //0월부터 시작

        calendarView.setListener(object:CompactCalendarView.CompactCalendarViewListener{
            override fun onDayClick(dateClicked: Date?) {
                val events=calendarView.getEvents(dateClicked)

                Log.i("events",workInfos.toString())

                //집안일 목록 초기화
                workList[0].list[0].list= mutableListOf()
                for(user in workList[1].list){
                    user.list= mutableListOf()
                }

                getTodayWork(dateClicked!!)

            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                calenderMonthTextView.text=(dateFormatForMonth.format(calendarView.firstDayOfCurrentMonth))
                getMonthWork(getYear(calendarView.firstDayOfCurrentMonth),getMonth(calendarView.firstDayOfCurrentMonth)) //0월부터 시작
            }

        })

        getTodayWork(Date())

    }

    private fun getMonthWork(year:String,month:String){

        firebaseDatabase.child(groupId).child("log").child(year).child(month).get().addOnCompleteListener {
            if(it.isSuccessful){
                //선택된 월의 데이터를 가져오기
                //캘린더에 데이터를 넣어주기

                    val format=SimpleDateFormat("yyyy-mm-dd")
                  Log.i("date",format.format(format.parse("2022-07-22"))+groupId)
                val event1=Event(Color.GREEN,SimpleDateFormat("yyyy-mm-dd").parse("2022-07-22").time,"0")


                calendarView.addEvent(event1)

                it.result ?:return@addOnCompleteListener


            }
        }
    }

    private fun getTodayWork(dateClicked:Date){

        firebaseDatabase.child("group").child(groupId).child("log").child(getYear(dateClicked)).child(getMonth(dateClicked)).child(getDate(dateClicked)).get().addOnCompleteListener {
            if(it.isSuccessful){
                it.result ?: return@addOnCompleteListener

                Log.i("info",getYear(dateClicked)+" "+getMonth(dateClicked)+" "+getDate(dateClicked))
                Log.i("it",it.result.toString())
                for(child in it.result.children){
                    val workId=child.key.toString().toInt()
                    Log.i("workId",workId.toString())
                    val person= mutableListOf<Person>()

                    for(child2 in child.child("manager").children){
                        Log.i("child2",child2.toString())
                        person.add(Person(child2.key.toString(),child2.child("userName").value.toString(),child2.child("isChecked").value.toString().toBoolean()))
                    }

                    if(workInfos[workId].number > person.size){ //3명 담당인데 1명만 되어있으면 2개를 추가
                        for(i in 1..workInfos[workId].number -person.size){
                            Log.i("not","in charged${workList[0].list[0].userName}")
                            val list=workList[0].list[0].list.toMutableList()
                            list.add(HomeThirdSection(false,workId,
                                workInfos[workId].title))
                            workList[0].list[0].list=list
                        }

                    }

                    Log.i("nmiddle",workList.toString())

                    for(prs in person){
                        Log.i("prs",prs.toString())

                        for(element in workList[1].list){
                            if(element.userId==prs.userId){
                                val list=element.list.toMutableList()
                                list.add(HomeThirdSection(prs.isChecked,workId,
                                    workInfos[workId].title))
                                element.list=list
                            }
                        }


                    }
                }

                recyclerView.adapter= HomeFirstSectionRecyclerViewAdapter(workList)
                recyclerView.layoutManager=LinearLayoutManager(requireContext())
            }
        }

    }

    private fun getWorkInfo() {

        firebaseDatabase.child("group").child(groupId).child("worklist").get().addOnCompleteListener {
            if(it.isSuccessful){
                it.result ?:return@addOnCompleteListener

                it.result.children.forEach { dataSnapshot ->

                    Log.i("success",dataSnapshot.toString())
                    dataSnapshot.getValue<Work>()?.let {it2->
                        workInfos.add(it2.workId,it2)
                    }
                }

                getUsers()
            }
        }
        
    }

    private fun getUsers(){
        Log.i("getsuers","getUsers")

        firebaseDatabase.child("group").child(groupId).child("users").get().addOnCompleteListener {
            if(it.isSuccessful){
                it.result?:return@addOnCompleteListener

                it.result.children.forEach { dataSnapshot ->

                    dataSnapshot.getValue<User>()?.let {it2->
                        val list=workList[1].list.toMutableList()
                        list.add(HomeSecondSection(it2.email,it2.nickName,
                            mutableListOf()))
                        workList[1].list=list

                    }
                }

                init()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.i("fragment","createView")
        val instance=FirebaseDatabase.getInstance()
        firebaseDatabase=instance.reference
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initCalendar()

    }

    private fun getYear(date:Date):String{
        val dateFormat=SimpleDateFormat("yyyy")
        return dateFormat.format(date)
    }

    private fun getMonth(date:Date):String{
        val dateFormat=SimpleDateFormat("MM")
        return dateFormat.format(date)
    }

    private fun getDate(date:Date):String{
        val dateFormat=SimpleDateFormat("dd")
        return dateFormat.format(date)
    }

}



