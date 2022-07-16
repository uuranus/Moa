package com.moa.moa.Home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.moa.moa.Data.*
import com.moa.moa.Main.HomeActivity
import com.moa.moa.R
import com.moa.moa.RegisterActivity
import com.moa.moa.Utility
import com.moa.moa.databinding.FragmentGroupBinding
import com.moa.moa.databinding.FragmentHomeBinding
import com.sothree.slidinguppanel.SlidingUpPanelLayout

import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class HomeFragment : Fragment() {
    private val utility=Utility()

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var homeActivity: HomeActivity
    private lateinit var groupId :String
    private  lateinit var _dateClicked: Date
    private var _workClicked by Delegates.notNull<Int>()
    private lateinit var firebaseDatabase:DatabaseReference

    private val calenderMonthTextView: TextView by lazy{
        requireView().findViewById(R.id.homeCalendarMonthTextView)
    }

    private val calendarView: CompactCalendarView by lazy{
        requireView().findViewById(R.id.homeCalendar)
    }

    private val beforeMonth:ImageButton by lazy{
        requireView().findViewById(R.id.beforeMonth)
    }

    private val nextMonth:ImageButton by lazy{
        requireView().findViewById(R.id.nextMonth)
    }

    private val recyclerView: RecyclerView by lazy{
        requireView().findViewById(R.id.homeRecyclerView)
    }

    private val notYetRecyclerView:RecyclerView by lazy{
        requireView().findViewById(R.id.homeNotYetRecyclerView)
    }


    private var workInfos= mutableListOf<Work>()//선택한 날짜의 집안일들 정보 인덱스=집안일 번호

    private var notYetWorkList= listOf<HomeNotYetSection>( //아직 배정되지 않았어요 리사이클러뷰용 데이터리스트
        HomeNotYetSection("아직 배정되지 않았어요!", mutableListOf<HomeNotYetSecondSection>()))

    private var workList= listOf<HomeFirstSection>(HomeFirstSection("가족들은 얼마나 했을까요?", mutableListOf())) //RecyclerView에 전달하기 위한 list

    private val dateFormatForMonth: SimpleDateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault()) //달력의 월과 년도 표시용

    private fun init(){
        groupId= utility.getGroupId(requireActivity())


        initWorkDetail()


        getWorkInfo() //모든 집안일 정보 가져와서 workInfos에 저장
    }

    private fun initWorkDetail() {
        val slidePanel = binding.mainFrame
        binding.assignBtn.setOnClickListener {
            slidePanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED

            Log.i("home", workList.size.toString())

            firebaseDatabase.child("group").child(groupId).child("log")
                .child(getYear(_dateClicked)).child(getMonth(_dateClicked)).child(getDate(_dateClicked)).child(_workClicked.toString()).child("manager").get().addOnSuccessListener {
                    val childrenCount = it.childrenCount.toString()
                    val insertPoint = firebaseDatabase.child("group").child(groupId).child("log")
                        .child(getYear(_dateClicked)).child(getMonth(_dateClicked)).child(getDate(_dateClicked)).child(_workClicked.toString())
                        .child("manager").child(childrenCount)
                    insertPoint.child("isChecked").setValue("false")
                    insertPoint.child("userId").setValue(utility.getUserId(requireActivity()))
                    //수정필요 userName받아와야함
                    for(i in 0 until workList[0].list.size){
                        if(workList[0].list[i].userId==utility.getUserId(requireActivity())){
                            insertPoint.child("userName").setValue(workList[0].list[i].userName)
                        }
                    }
                }

        }


    }

    private fun initWorkList(){
        notYetWorkList[0].list= emptyList()

        for(section in workList[0].list){
            section.list= emptyList()
        }
    }

    private fun initCalendar(){
        initCalendarMonth()

        calendarView.setUseThreeLetterAbbreviation(true)

        beforeMonth.setOnClickListener {
            calendarView.scrollLeft()
            initCalendarMonth()
        }
        nextMonth.setOnClickListener {
            calendarView.scrollRight()
            initCalendarMonth()
        }

        calendarView.setListener(object:CompactCalendarView.CompactCalendarViewListener{
            override fun onDayClick(dateClicked: Date?) {
                val events=calendarView.getEvents(dateClicked)

                initCalendarMonth()

                getTodayWork(dateClicked!!)

            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                calenderMonthTextView.text=(dateFormatForMonth.format(calendarView.firstDayOfCurrentMonth))
                getMonthWork(getYear(calendarView.firstDayOfCurrentMonth),getMonth(calendarView.firstDayOfCurrentMonth)) //0월부터 시작
            }

        })

        getTodayWork(Date())

    }

    private fun initCalendarMonth(){ //사용자가 달력을 스크롤할때마다 실행되는 메소드 년도와 월을 변경하고 바뀐 월에 맞춰서 집안일 목록을 가져옴
        calenderMonthTextView.text=(dateFormatForMonth.format(calendarView.firstDayOfCurrentMonth))
        getMonthWork(getYear(calendarView.firstDayOfCurrentMonth),getMonth(calendarView.firstDayOfCurrentMonth)) //0월부터 시작

    }

    private fun getMonthWork(year:String,month:String){ //해당 월의 집안일 목록들을 가져오는 메소드

        firebaseDatabase.child(groupId).child("log").child(year).child(month).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //선택된 월의 데이터를 가져오기
                //캘린더에 데이터를 넣어주기

                val format=SimpleDateFormat("yyyy-mm-dd")
                val event1=Event(Color.GREEN,SimpleDateFormat("yyyy-mm-dd").parse("2022-07-22").time,"0")


                calendarView.addEvent(event1)

                calendarView.invalidate()
                snapshot.value ?:return

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"데이터를 가져오는데 실패했습니다",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getTodayWork(dateClicked:Date){ //사용자가 선택한 날짜에 해당하는 집안일 정보를 가져오는 메소드 <-- 리사이클러뷰에 뿌려줄 정보를 만듦

        firebaseDatabase.child("group").child(groupId).child("log").child(getYear(dateClicked)).child(getMonth(dateClicked)).child(getDate(dateClicked)).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val tmpList= mutableListOf<HomeNotYetSecondSection>()
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

                        if(workInfos[workId].number > person.size){ //3명 담당인데 1명만 되어있으면 notYetWorkList에 추가
                            val list=notYetWorkList[0].list.toMutableList()
                            list.add(HomeNotYetSecondSection(0,workInfos[workId].title,workId.toString()))
                            tmpList.add(HomeNotYetSecondSection(0,workInfos[workId].title,workId.toString()))
                            notYetWorkList[0].list=list
                        }

                        for(prs in person){
                            for(element in workList[0].list){
                                if(element.userId==prs.userId){
                                    val list=element.list.toMutableList()
                                    list.add(HomeThirdSection(prs.isChecked,workId,
                                        workInfos[workId].title))
                                    element.list=list
                                }
                            }

                        }
                    }
                }

                //미배정 목록 리사이클러뷰
                val homeAdapter =HomeNotYetSecondRecyclerViewAdapter(tmpList)
                homeAdapter.onItemClickListener = {
                    Log.i("home adapter","$it clicked! in homefrag")

                    val slidePanel = binding.mainFrame
                    val state = slidePanel.panelState
                    // 닫힌 상태일 경우 열기
                    if (state == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                        slidePanel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
                    }

                    setDetailPage(it)

                    _dateClicked = dateClicked
                    _workClicked = it

                }
                notYetRecyclerView.adapter= homeAdapter
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

    private fun setDetailPage(workNum:Int) {
        firebaseDatabase.child("group").child(groupId).child("worklist").child(workNum.toString()).child("title").get().addOnSuccessListener {
            binding.workDetailTitle.text = it.value.toString()

        }
        firebaseDatabase.child("group").child(groupId).child("worklist").child(workNum.toString()).child("stars").get().addOnSuccessListener {
            binding.starNum.text = it.value.toString()
            val starNum=it.value.toString().toInt()
            when(starNum){
                1->{
                    binding.star2.visibility = View.GONE
                    binding.star3.visibility = View.GONE
                    binding.star4.visibility = View.GONE
                    binding.star5.visibility = View.GONE
                }

                2->{
                    binding.star3.visibility = View.GONE
                    binding.star4.visibility = View.GONE
                    binding.star5.visibility = View.GONE
                }

                3->{
                    binding.star4.visibility = View.GONE
                    binding.star5.visibility = View.GONE
                }

                4->{
                    binding.star5.visibility = View.GONE
                }
            }

        }
        firebaseDatabase.child("group").child(groupId).child("worklist").child(workNum.toString()).child("description").get().addOnSuccessListener {
            binding.workDetailContents.text = it.value.toString()

        }

    }

    private fun getWorkInfo() { //groupId->worklist에 저장되어 있는 집안일 정보를 가져옴

        firebaseDatabase.child("group").child(groupId).child("worklist").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.value ?:return

                snapshot.children.forEach { dataSnapshot ->

                    dataSnapshot.getValue<Work>()?.let {it2->
                        workInfos.add(it2.workId,it2)
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

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 액티비티로 형변환해서 할당
        homeActivity = context as HomeActivity


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val instance=FirebaseDatabase.getInstance()
        firebaseDatabase=instance.reference
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

       //binding.workDetailLayout.background = ColorDrawable(Color.TRANSPARENT)
        val slidePanel = binding.mainFrame
        slidePanel.shadowHeight = 0

        return binding.root
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



