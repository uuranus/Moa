package com.moa.moa.Home

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import com.github.sundeepk.compactcalendarview.CompactCalendarView
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


    private val calendarView: CalendarView by lazy{
        requireView().findViewById(R.id.homeCalendar)
    }


    private val recyclerView: RecyclerView by lazy{
        requireView().findViewById(R.id.homeRecyclerView)
    }

    private val notYetRecyclerView:RecyclerView by lazy{
        requireView().findViewById(R.id.homeNotYetRecyclerView)
    }

    private var workInfos= arrayOf<Work?>()//선택한 날짜의 집안일들 정보 인덱스=집안일 번호

    private var notYetWorkList= listOf<HomeNotYetSection>( //아직 배정되지 않았어요 리사이클러뷰용 데이터리스트
        HomeNotYetSection("아직 배정되지 않았어요!", mutableListOf<HomeNotYetSecondSection>()))

    private var workList= listOf<HomeFirstSection>(HomeFirstSection("가족들은 얼마나 했을까요?", mutableListOf())) //RecyclerView에 전달하기 위한 list

    private val dateFormatForMonth: SimpleDateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault()) //달력의 월과 년도 표시용

    private fun init(){
        groupId= utility.getGroupId(requireActivity())

        Log.i("infofofofo",groupId)
        getWorkInfo() //모든 집안일 정보 가져와서 workInfos에 저장
    }

    private fun initWorkList(){
        notYetWorkList[0].list= emptyList()

        for(section in workList[0].list){
            section.list= emptyList()
        }
    }

    private fun initCalendar(){

        initCalendarMonth()

        val events = mutableListOf<EventDay>()

        val calendar1 = Calendar.getInstance()
        val calendar2= Calendar.getInstance()
        val calendar3= Calendar.getInstance()
//        events.add( EventDay(calendar, R.drawable.circle));

//or if you want to specify event label color
        Log.i("calendar",calendar1.get(Calendar.DATE).toString())
        calendar1.set(2022,6,11)
        events.add( EventDay(calendar1, R.drawable.circle))
        Log.i("events",events.toString())
        calendar2.set(2022,6,31)
        Log.i("calendar",calendar2.get(Calendar.DATE).toString())
        events.add( EventDay(calendar2, R.drawable.circle))

        calendar3.set(2022,6,22)
        Log.i("calendar",calendar3.get(Calendar.DATE).toString())
        events.add( EventDay(calendar3, R.drawable.circle))

        calendarView.setCalendarDayLayout(R.layout.calendar_day_custom)

        Log.i("events",events.toString())
        calendarView.setEvents(events)



        calendarView.setOnPreviousPageChangeListener(object:OnCalendarPageChangeListener{
            override fun onChange() {
                initCalendarMonth()
            }
        })

        calendarView.setOnForwardPageChangeListener(object:OnCalendarPageChangeListener{
            override fun onChange() {
                initCalendarMonth()
            }
        })


        calendarView.setOnDayClickListener(object:OnDayClickListener{
            override fun onDayClick(eventDay: EventDay) {
//
//                Log.i("selectedDates",calendarView.selectedDates.toString())
//                val events=eventDay
//
//                println("event@!!!"+events)
//
//                initCalendarMonth()
//
                getTodayWork(eventDay.calendar.run {
                    add(Calendar.MONTH,1)
                    time
                })
            }

        })


        getTodayWork(Date())

    }

    private fun initCalendarMonth(){ //사용자가 달력을 스크롤할때마다 실행되는 메소드 년도와 월을 변경하고 바뀐 월에 맞춰서 집안일 목록을 가져옴
        Log.i("selectedDates",calendarView.selectedDates.toString())
        getMonthWork(calendarView.currentPageDate.get(Calendar.YEAR).toString(),calendarView.currentPageDate.get(Calendar.MONTH).toString()) //0월부터 시작
    }

    private fun getMonthWork(year:String,month:String){ //해당 월의 집안일 목록들을 가져오는 메소드

        firebaseDatabase.child(groupId).child("log").child(year).child(month).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //선택된 월의 데이터를 가져오기
                //캘린더에 데이터를 넣어주기

//                val format=SimpleDateFormat("yyyy-mm-dd")
//                val event1=Event(Color.GREEN,SimpleDateFormat("yyyy-mm-dd").parse("2022-07-22").time,"0")
//
//
//                calendarView.addEvent(event1)
//
//                calendarView.invalidate()
                snapshot.value ?:return

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"데이터를 가져오는데 실패했습니다",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getTodayWork(dateClicked: Date){ //사용자가 선택한 날짜에 해당하는 집안일 정보를 가져오는 메소드 <-- 리사이클러뷰에 뿌려줄 정보를 만듦

        Log.i("todaywork",dateClicked.toString())
        firebaseDatabase.child("group").child(groupId).child("log").child(getYear(dateClicked)).child(getMonth(dateClicked)).child(getDate(dateClicked)).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value ==null) {
                    initWorkList()

                }
                else {
                    for(child in snapshot.children){
                        val workId=child.key.toString().toInt()
                        val person= mutableListOf<Person>()
                        for(child2 in child.child("manager").children){
                            person.add(Person(child2.child("userId").value.toString(),child2.child("userName").value.toString(),child2.child("isChecked").value.toString().toBoolean()))
                        }

                        for(work in workInfos){
                            Log.i("workInfo",work.toString())
                        }
                        Log.i("null", workId.toString()+" "+workInfos[workId].toString())
                        if(workInfos[workId]!!.number > person.size){ //3명 담당인데 1명만 되어있으면 notYetWorkList에 추가
                            val list=notYetWorkList[0].list.toMutableList()
                            list.add(HomeNotYetSecondSection(0,workInfos[workId]!!.title,workId.toString()))
                            notYetWorkList[0].list=list
                        }

                        for(prs in person){
                            for(element in workList[0].list){
                                if(element.userId==prs.userId){
                                    val list=element.list.toMutableList()
                                    list.add(HomeThirdSection(prs.isChecked,workId,
                                        workInfos[workId]!!.title))
                                    element.list=list
                                }
                            }

                        }
                    }
                }

                //미배정 목록 리사이클러뷰
                notYetRecyclerView.adapter= HomeNotYetRecyclerViewAdapter(notYetWorkList)
                notYetRecyclerView.layoutManager=LinearLayoutManager(requireContext())

                //배정 목록 리사이클러뷰
                recyclerView.adapter= HomeFirstSectionRecyclerViewAdapter(workList)
                recyclerView.layoutManager=LinearLayoutManager(requireContext())


            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"데이터를 가져오는데 실패했습니다",Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun getWorkInfo() { //groupId->worklist에 저장되어 있는 집안일 정보를 가져옴

        firebaseDatabase.child("group").child(groupId).child("worklist").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value ==null){
                    //집안일 정보 가져 온 후 사용자 정보 가져오기 --> 가족들은 얼마나 했을까요?에서 구성원 목록을 미리 만들어놓기 위함
                    getUsers()
                    return
                }

                Log.i("last",snapshot.key.toString())
                val len=snapshot.children.last().key!!.toInt()
                Log.i("len",len.toString())
                workInfos= arrayOfNulls<Work>(len+1)

                snapshot.children.forEach { dataSnapshot ->

                    dataSnapshot.getValue<Work>()?.let {it2->
                        workInfos.set(it2.workId,it2)
                        Log.i("workInfooooo",workInfos.toString())
                    }
                }

                //집안일 정보 가져 온 후 사용자 정보 가져오기 --> 가족들은 얼마나 했을까요?에서 구성원 목록을 미리 만들어놓기 위함
                getUsers()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"데이터를 가져오는데 실패했습니다",Toast.LENGTH_SHORT).show()
            }


        })
        
    }

    private fun getUsers(){ //group->users에 저장되어 있는 유저들 정보를 가져옴

        firebaseDatabase.child("group").child(groupId).child("users").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.value?:return

                snapshot.children.forEach { dataSnapshot ->

                    dataSnapshot.getValue<User>()?.let {it2->
                        val list=workList[0].list.toMutableList()
                        list.add(HomeSecondSection(it2.userId,it2.userName,
                            mutableListOf()))
                        workList[0].list=list

                    }
                }

                initCalendar() //사용자 정보 다 가져왔으면 캘린더 초기화
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"데이터를 가져오는데 실패했습니다",Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val instance=FirebaseDatabase.getInstance()
        firebaseDatabase=instance.reference
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

    }

    @SuppressLint("SimpleDateFormat")
    private fun getYear(date:Date):String{
        val dateFormat=SimpleDateFormat("yyyy")
        return dateFormat.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getMonth(date:Date):String{
        val dateFormat=SimpleDateFormat("MM")
        return dateFormat.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDate(date:Date):String{
        val dateFormat=SimpleDateFormat("dd")
        return dateFormat.format(date)
    }

}



