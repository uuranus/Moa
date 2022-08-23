package com.moa.moa.Home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.moa.moa.Data.*
import com.moa.moa.Main.HomeActivity
import com.moa.moa.R
import com.moa.moa.Utility
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


    private var workInfos= arrayOf<Work?>()//선택한 날짜의 집안일들 정보 인덱스=집안일 번호

    private var notYetWorkList= listOf<HomeNotYetSection>( //아직 배정되지 않았어요 리사이클러뷰용 데이터리스트
        HomeNotYetSection("아직 배정되지 않았어요!", mutableListOf<HomeNotYetSecondSection>()))

    private var workList= listOf<HomeFirstSection>(HomeFirstSection("가족들은 얼마나 했을까요?", mutableListOf())) //RecyclerView에 전달하기 위한 list

    private fun init(){
        groupId= utility.getGroupId(requireActivity())

        initWorkDetail()


        getWorkInfo() //모든 집안일 정보 가져와서 workInfos에 저장
    }

    private fun initWorkDetail() {

        val slidePanel = binding.mainFrame
        binding.assignBtn.setOnClickListener {
            slidePanel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED

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
        val calendarView = binding.homeCalendar

        calendarView.setCalendarDayLayout(R.layout.calendar_day_custom)

        calendarView.setOnPreviousPageChangeListener(object:OnCalendarPageChangeListener{
            override fun onChange() {
                initCalendarMonth()
            }
        })

        calendarView.setOnForwardPageChangeListener(object: OnCalendarPageChangeListener {
            override fun onChange() {
                initCalendarMonth()
            }
        })

        calendarView.setOnDayClickListener(object: OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {

                val calendar=Calendar.getInstance()
                calendar.set(
                    eventDay.calendar.get(Calendar.YEAR),
                    eventDay.calendar.get(Calendar.MONTH),
                    eventDay.calendar.get(Calendar.DATE),
                )

                getTodayWork(calendar.run {
                    add(Calendar.MONTH,1)
                    time
                })
            }

        })

        getTodayWork(Date())

    }

    private fun initCalendarMonth(){ //사용자가 달력을 스크롤할때마다 실행되는 메소드 년도와 월을 변경하고 바뀐 월에 맞춰서 집안일 목록을 가져옴
        val calendarView = binding.homeCalendar

        getMonthWork(calendarView.currentPageDate.get(Calendar.YEAR).toString(),String.format("%02d",(calendarView.currentPageDate.get(Calendar.MONTH)+1))) //0월부터 시작

    }

    private fun getMonthWork(year:String,month:String){ //해당 월의 집안일 목록들을 가져오는 메소드
        val calendarView = binding.homeCalendar

        val userId=utility.getUserId(requireActivity())

        val calendar=Calendar.getInstance()

        firebaseDatabase.child("group").child(groupId).child("log").child(year).child(month).addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //선택된 월의 데이터를 가져오기
                //캘린더에 데이터를 넣어주기
                snapshot.value ?:return


                val list= mutableListOf<EventDay>()
                snapshot.children.forEach {
                    val date=it.key.toString().toInt()
                    var isExist=false
                    for(chld in it.children){
                        for(c in chld.child("manager").children){
                            c?:break
                            if(c.child("userId").value.toString()==userId
                                && c.child("isChecked").value.toString()!="true"){
                                isExist=true
                                break
                            }
                        }
                        if(isExist) break
                    }

                    //담당했는데 안 한 집안일이 있다면
                    if(isExist){
                        calendar.set(year.toInt(),month.toInt()-1,date)
                        list.add(EventDay(
                            calendar,
                            R.drawable.circle
                        ))
                    }

                }

                calendarView.setEvents(list)

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

                        if(workInfos[workId]!!.number > person.size){ //3명 담당인데 1명만 되어있으면 notYetWorkList에 추가
                            val list=notYetWorkList[0].list.toMutableList()
                            list.add(HomeNotYetSecondSection(0,workInfos[workId]!!.title,workId.toString()))
                            tmpList.add(HomeNotYetSecondSection(0,workInfos[workId]!!.title,workId.toString()))
                            notYetWorkList[0].list=list
                        }
                        Log.i("homefragment",person.toString())
                        for(prs in person){
                            for(element in workList[0].list){
                                if(element.userId==prs.userId){
                                    //val list=element.list.toMutableList()
                                    val list=mutableListOf<HomeThirdSection>()
                                    list.add(HomeThirdSection(prs.isChecked,workId,
                                        workInfos[workId]!!.title))
                                    element.list=list
                                }
                            }

                        }
                    }
                }


                //미배정 목록 리사이클러뷰

                val homeAdapter =HomeNotYetSecondRecyclerViewAdapter(tmpList)
                homeAdapter.onItemClickListener = {

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
                binding.homeNotYetRecyclerView.adapter= homeAdapter
                binding.homeNotYetRecyclerView.layoutManager=LinearLayoutManager(homeActivity)



                //배정 목록 리사이클러뷰
                binding.homeRecyclerView.adapter= HomeFirstSectionRecyclerViewAdapter(workList)
                Log.i("homefragment", " binding!")
                binding.homeRecyclerView.layoutManager=LinearLayoutManager(homeActivity)



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

        binding.assignBtn.setOnClickListener {

        }
    }

    private fun getWorkInfo() { //groupId->worklist에 저장되어 있는 집안일 정보를 가져옴

        firebaseDatabase.child("group").child(groupId).child("worklist").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value ==null){
                    //집안일 정보 가져 온 후 사용자 정보 가져오기 --> 가족들은 얼마나 했을까요?에서 구성원 목록을 미리 만들어놓기 위함
                    getUsers()
                    return
                }

                val len=snapshot.children.last().key!!.toInt()

                workInfos= arrayOfNulls<Work>(len+1)

                snapshot.children.forEach { dataSnapshot ->

                    dataSnapshot.getValue<Work>()?.let {it2->
                        workInfos.set(it2.workId,it2)
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
                        list.add(HomeSecondSection(it2.userId,it2.userName, mutableListOf()))
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

