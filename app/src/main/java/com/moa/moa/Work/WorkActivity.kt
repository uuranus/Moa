package com.moa.moa.Work

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.slider.Slider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.moa.moa.Data.AlarmReceiver
import com.moa.moa.Data.Time
import com.moa.moa.Data.TitleHistory
import com.moa.moa.Data.Work
import com.moa.moa.R
import com.moa.moa.Utility
import com.moa.moa.databinding.ActivityWorkBinding
import java.text.SimpleDateFormat
import java.util.*

class WorkActivity : AppCompatActivity() {

    private val utility= Utility()
    private lateinit var groupId:String
    private var isEdit=false
    private var editData: Work?=null
    private lateinit var db:AppDatabase
    private lateinit var database:DatabaseReference
    private lateinit var adapter:TitleHistoryAdapter
    private var curWorkId:Int=0

    private var currentPeopleNumber:Int=1
    set(value) {
        field=value
        currentPeople.text="${currentPeopleNumber}명"
    }
    private var maxPeople=3


    private var star=0
    set(value) {
        field=value
        val starList= listOf<ImageView>(star1,star2,star3,star4,star5)
        for(i in starList.indices){
            starList[i].isSelected=i<star
        }
    }
    private val backButton:ImageButton by lazy{
        findViewById(R.id.backButton)
    }

    private val saveButton:TextView by lazy{
        findViewById(R.id.saveButton)
    }

    private val titleEditText:EditText by lazy{
        findViewById(R.id.workTitleEditText)
    }

    private val titleHistoryRecyclerView:RecyclerView by lazy{
        findViewById(R.id.titleHistoryRecyclerView)
    }

    private val descriptionEditText:EditText by lazy{
        findViewById(R.id.workDescriptionEditText)
    }

    private val periodSlider: Slider by lazy{
        findViewById(R.id.workPeriodSlider)
    }

    private val periodSliderText:TextView by lazy{
        findViewById(R.id.workPeriodValue)
    }

    private val periodStartPicker:TextView by lazy{
        findViewById(R.id.workPeriodStartPicker)
    }

    private val alarmSwitch: Switch by lazy{
        findViewById(R.id.workAlarmSwitch)
    }

    private val alarmPicker:TimePicker by lazy{
        findViewById(R.id.workAlarmPicker)
    }

    private val minusButton:ImageButton by lazy{
        findViewById(R.id.workMinusButton)
    }

    private val plusButton:ImageButton by lazy{
        findViewById(R.id.workPlusButton)
    }

    private val currentPeople:TextView by lazy{
        findViewById(R.id.workCurrentPeople)
    }

    private val star1:ImageView by lazy{
        findViewById(R.id.star1)
    }

    private val star2:ImageView by lazy{
        findViewById(R.id.star2)
    }

    private val star3:ImageView by lazy{
        findViewById(R.id.star3)
    }

    private val star4:ImageView by lazy{
        findViewById(R.id.star4)
    }

    private val star5:ImageView by lazy {
        findViewById(R.id.star5)
    }

    private val workDeleteButton:Button by lazy {
        findViewById(R.id.workDeleteButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work)

        groupId=utility.getGroupId(this)
        db= Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "WorkDB"
        ).fallbackToDestructiveMigration()
            .build()
        database=FirebaseDatabase.getInstance().reference

        isEdit=intent.getBooleanExtra("isEdit",false)
        curWorkId=intent.getIntExtra("workId",0)
        if(intent.hasExtra("editData")){
            editData=intent.getSerializableExtra("editData") as Work
        }


        initViews()
        initListener()

    }

    private fun initViews(){
        val pendingIntentt=PendingIntent.getBroadcast(this, 0,
            Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE
        )

        if( pendingIntentt==null) Log.i("pendingIntent","null")
       else  Log.i("pendingIntent",pendingIntentt.creatorUid.toString())

        if(isEdit){
            titleEditText.setText(editData?.title)
            descriptionEditText.setText(editData?.description)
            periodSlider.value=editData?.period!!.toFloat()
            periodSliderText.text="${editData?.period}일"
            if(editData?.time!="-1:-1"){
                alarmSwitch.isChecked=true
                val times=editData?.time!!.split(":")
                alarmPicker.hour=times[0].toInt()
                alarmPicker.minute=times[1].toInt()
            }

            currentPeopleNumber= editData?.number!!
            star=editData?.stars!!

            curWorkId=editData?.workId!!

            workDeleteButton.isVisible=true
        }
    }

    private fun initListener(){

        getMaxPeople()

        backButton.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            if(isEdit){
                deleteWork()
                addWork()
            }
            else{
                addWork()
            }
            finish()
        }

        workDeleteButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage("집안일을 삭제하시겠습니까?")
                .setPositiveButton("네"){_,_->
                    deleteWork()
                    deleteAlarm()
                    finish()
                }
                .setNegativeButton("아니오"){_,_->

                }.show()
        }

        adapter=TitleHistoryAdapter(workTitleListener = {
            deleteSearchKeyword(it)
        })
        titleHistoryRecyclerView.adapter=adapter
        titleHistoryRecyclerView.layoutManager=LinearLayoutManager(this)

        titleEditText.setOnFocusChangeListener { view, isFocused ->

            if (isFocused) {
                Log.i("모션", "제목 터치함")
                showHistoryView(titleEditText.text.toString())
            } else {
                Log.i("모션", "제목 터치 떠남")
                hideHistoryView()
            }
        }

        titleEditText.addTextChangedListener {
            Log.i("textedit",it.toString())
            it?:return@addTextChangedListener

            showHistoryView(it.toString())
        }


        descriptionEditText.setOnTouchListener { view, motionEvent ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            return@setOnTouchListener false
        }

        periodSlider.addOnSliderTouchListener(object:Slider.OnSliderTouchListener{
            override fun onStartTrackingTouch(slider: Slider) {

            }

            override fun onStopTrackingTouch(slider: Slider) {
                periodSliderText.text="${slider.value.toInt()}일"
            }

        })

        val today=utility.getToday()
        periodStartPicker.text=today
        periodStartPicker.setOnClickListener {
            val curdate=periodStartPicker.text.toString().split("-")
            DatePickerDialog(this,object:DatePickerDialog.OnDateSetListener{
                override fun onDateSet(p0: DatePicker?, year: Int, month: Int, date: Int) {
                    periodStartPicker.text="%04d-%02d-%02d".format(year,month+1,date)
                }
            },
                curdate[0].toInt(),
                curdate[1].toInt()-1,
                curdate[2].toInt()
            )
        }

        alarmSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
            alarmPicker.isVisible=isChecked
        }

        alarmPicker.isVisible=alarmSwitch.isChecked

        minusButton.setOnClickListener {
            if(currentPeopleNumber!=1) currentPeopleNumber--
        }

        plusButton.setOnClickListener {
            if(currentPeopleNumber!=maxPeople) currentPeopleNumber++
        }

        star1.setOnClickListener {
            star=1
        }
        star2.setOnClickListener {
            star=2
        }
        star3.setOnClickListener {
            star=3
        }
        star4.setOnClickListener {
            star=4
        }
        star5.setOnClickListener {
            star=5
        }
    }

    private fun addWork(){

        if(titleEditText.text.isEmpty()){
            Toast.makeText(this,"제목을 입력해주세요!",Toast.LENGTH_SHORT).show()
            return
        }
        else if(titleEditText.text.length>20){
            Toast.makeText(this,"제목은 20자 이내여야 합니다",Toast.LENGTH_SHORT).show()
            return
        }
        else if(descriptionEditText.text.length>100){
            Toast.makeText(this,"세부사항은 100자 이내여야 합니다",Toast.LENGTH_SHORT).show()
            return
        }

        val time=if(alarmSwitch.isChecked){
            Time(alarmPicker.hour,alarmPicker.minute)
        }
        else{
            Time(-1,-1)
        }

        //집안일 제목 저장
        saveSearchKeyword(titleEditText.text.toString())

        //집안일 정보 추가
        database.child("group").child(groupId).child("worklist").child(curWorkId.toString()).setValue(
            Work(titleEditText.text.toString(),
            descriptionEditText.text.toString(),
            periodSlider.value.toInt(),
            time.timeText,
                currentPeopleNumber,
                star,
                curWorkId,
                null
            )
        )

        //3개월 이후의 집안일을 추가해놓기
        saveWorkForThreeMonth()

        //알림 설정
        if(time.timeText!="-1:-1"){
            setAlarm(time,periodStartPicker.text.toString())
        }

    }

    private fun saveWorkForThreeMonth(){
        val startDay=periodStartPicker.text.toString()
        val period=periodSlider.value.toInt()

        var curdate=utility.addDateFrom(startDay,period)
        val dateAfter=utility.getThreeMonthAfter(startDay)
        while(curdate<dateAfter){
            val dates=curdate.split("-")
            database.child("group").child(groupId).child("log").child(dates[0]).child(dates[1]).child(dates[2])
                .child(curWorkId.toString()).child("workId").setValue(curWorkId.toString())

            curdate=utility.addDateFrom(curdate,period)
        }
    }

    private fun deleteWork(){
         //현재 날짜 이후부터 시작하는 집안일의 날짜를 가져옴
        val period=periodSlider.value.toInt()

        //그 날짜부터 시작해서 3개월 뒤의 집안일까지 다 삭제

        utility.deletePreviousEvent(curWorkId.toString(),period,groupId)

        //집안일 정보 자체도 삭제
        database.child("group").child(groupId).child("worklist").child(curWorkId.toString()).removeValue()
    }

    private fun setAlarm(startTime:Time,startDay:String){
        //이미 알람이 설정되어 있으면 새로 업데이트
        val calendar= Calendar.getInstance().apply{
            set(Calendar.HOUR_OF_DAY,startTime.hour)
            set(Calendar.MINUTE,startTime.minute)

            val dates=startDay.split("-")
            set(Calendar.YEAR, dates[0].toInt())
            set(Calendar.MONTH, dates[1].toInt()-1) //calendar는 0월부터 셈
            set(Calendar.DATE, dates[2].toInt())

        }

        val dateFormatForMonth: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) //달력의 월과 년도 표시용
        Log.i("alarm calendar",dateFormatForMonth.format(calendar.time))
        val alarmManager=getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent= Intent(this, AlarmReceiver::class.java)
        intent.putExtra("workTitle",titleEditText.text.toString())
        intent.putExtra("workId",curWorkId)

        val pendingIntent= PendingIntent.getBroadcast(this, 1000,
            intent, PendingIntent.FLAG_CANCEL_CURRENT) //기존 게 있으면 cancel하고 새로 생성 하겠다
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME, //경과한 시간을 기준으로 시간 카운트
            calendar.timeInMillis,
            periodSlider.value.toLong(),
            pendingIntent
        )


        val pendingIntentt=PendingIntent.getBroadcast(this, 1000,
            Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE
        )
        Log.i("setAlamr???", pendingIntentt.creatorUid.toString())
    }

    private fun deleteAlarm(){
        //현재 알림 설정되어있는 거 삭제
        val pendingIntent=PendingIntent.getBroadcast(this, 1000,
            Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.cancel()
    }

    //제목 최근기록을 위한 메소드들
    private fun deleteSearchKeyword(keyword:String){ //keyword는 삭제하고자 하는 최근기록
        Thread{
            db.titleDao().delete(keyword)
            showHistoryView(titleEditText.text.toString())
        }.start()
    }

    private fun saveSearchKeyword(keyword:String){
        Thread{
            db.titleDao().insertHistory(TitleHistory(null,keyword))
        }.start()
    }

    private fun showHistoryView(title:String){
        Thread{

            val keywords=if(title.isEmpty()){ db.titleDao().getAll().reversed()}
            else{
                db.titleDao().getHistory(title).reversed()
            }
            Log.i("history",keywords.toString())

            runOnUiThread {
                titleHistoryRecyclerView.isVisible=true
                if(keywords.isEmpty()) hideHistoryView()
                else adapter.submitList(keywords)
            }

        }.start()
        titleHistoryRecyclerView.isVisible=true
    }
    private fun hideHistoryView(){
        titleHistoryRecyclerView.isVisible=false
    }


    //담당인원 최대 수를 위해 현 그룹의 인원 수 가져오기
    private fun getMaxPeople(){
        database.child("group").child(groupId).child("userNumber").get().addOnCompleteListener {
            if(it.isSuccessful){
                maxPeople=it.result.value.toString().toInt()
            }
        }
    }

}