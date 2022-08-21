package com.moa.moa.Register

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.moa.moa.Data.Badge
import com.moa.moa.Data.Group
import com.moa.moa.Data.User
import com.moa.moa.Data.Work
import com.moa.moa.Main.HomeActivity
import com.moa.moa.R
import com.moa.moa.Utility
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class RegisterActivity : FragmentActivity() {
    private val database = Firebase.database.reference

    var roomId: String? = null
    var roomName: String? = null
    var roomNumber:Int = 1
    var nickname:String? = null
    private var userEmail:String=""
    var imageUri: Uri?=null

    private var profilePageAdapter:FragmentStateAdapter?=null

    private val backButton:ImageButton by lazy{
        findViewById(R.id.registerBackButton)
    }

    private val profileSaveButton:Button by lazy{
        findViewById(R.id.profileSaveButton)
    }

    private val viewPager: ViewPager2 by lazy{
        findViewById(R.id.profileViewPager)
    }

    private val indicator:WormDotsIndicator by lazy {
        findViewById(R.id.dotsIndicator)
    }

    private val progressBar:ProgressBar by lazy {
        findViewById(R.id.progressBar)
    }

    private val PAGE_NUM=5

    private var state:Int=0
        set(value) {
            field = value
            if(value==PAGE_NUM-1){
                profileSaveButton.text= "확인"
            }
            else{
                profileSaveButton.text= "다음"
            }

        }

    var isExisting : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        userEmail=Utility().getUserId(this)

        init()

    }

    private fun init() {
        profilePageAdapter = ProfilePageAdapter(this)
        viewPager.adapter = profilePageAdapter

        //swipe 막기
        viewPager.isUserInputEnabled=false

        indicator.setViewPager2(viewPager)
        progressBar.visibility= View.GONE

        backButton.setOnClickListener {
            when (viewPager.currentItem) {
                0 -> {

                }
                4 -> {
                    viewPager.currentItem = 2
                    state=viewPager.currentItem
                }
                else -> {
                    viewPager.currentItem--
                    state=viewPager.currentItem
                }
            }
        }

        profileSaveButton.isEnabled=false
        profileSaveButton.setOnClickListener {
            if(state==PAGE_NUM-1){ //마지막 페이지에서 save 버튼 눌렀을 때 HomeActivity 로 넘어가면 된다.
                if(viewPager.currentItem == 3){ //enterFragment to homeActivity 데이터베이스에서 사용자가 입력한 방id가 존재하는지 검사
                    if(roomId==null|| roomId!!.isEmpty()){
                        Toast.makeText(this,"잘못된 그룹 ID입니다! 다시 입력해주세요",Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    progressBar.visibility= View.VISIBLE
                    database.child("group").child(roomId!!).get().addOnSuccessListener {
                        Toast.makeText(this," group ID : $roomId",Toast.LENGTH_SHORT).show()

                        if(it.value!=null){
                            insertUser()
                        }
                        else{
                            Toast.makeText(this,"$roomId 방이 존재하지 않습니다. ",Toast.LENGTH_SHORT).show()
                            progressBar.visibility= View.GONE
                        }

                    }.addOnFailureListener{
                        Toast.makeText(this,"$roomId 방이 존재하지 않습니다. ",Toast.LENGTH_SHORT).show()
                        progressBar.visibility= View.GONE
                    }
                }else{ //settingGroupName to homeActivity 방을 생성한 경우이므로 database 에 저장해야한다.
                    if(roomName==null || roomName == ""){
                        Toast.makeText(this,"잘못된 그룹 이름입니다! 다시 입력해주세요",Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    else if(roomName!!.length<2 || roomName!!.length>20){
                        Toast.makeText(this,"그룹이름은 2~20자 사이여야 합니다", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    progressBar.visibility= View.VISIBLE
                    insertNewGroup(nickname!!,roomName!!,roomNumber)

                }

            }
            else{
                if(viewPager.currentItem == 2){ //2번 페이지에서 기존방 입장 or 새로운 그룹 생성 분기
                    when (isExisting) {
                        0 -> { //기존방이 존재하지 않는 경우 -> 4번페이지로 SettingGroupNumberFragment
                            profileSaveButton.isEnabled = true
                            viewPager.currentItem = 4
                            state=viewPager.currentItem
                        }
                        1 -> { //기존방이 존재하는 경우 -> 3번페이지로 EnterGroupFragment
                            profileSaveButton.isEnabled = true
                            viewPager.currentItem++
                            state = PAGE_NUM-1
                        }
                        else -> { // 오류
                            Toast.makeText(this,"group 오류",Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    viewPager.currentItem++
                    state=viewPager.currentItem
                }
            }
        }
    }

    override fun onBackPressed() {
        if(state==0){
            AlertDialog.Builder(this)
                .setMessage("프로필 설정을 종료하시겠습니까?")
                .setPositiveButton("예") { _, _ ->
                    val intent=Intent(this,LoginActivity::class.java)
                    intent.putExtra("cancelRegister",false)
                    startActivity(intent)
                }
                .setNegativeButton("아니오") { _, _ ->

                }.show()

        }
        else{
            viewPager.currentItem--
            state=viewPager.currentItem
        }

    }

    private fun startHomeActivity() {
        //key값 sharedPrefenece에 저장하고 있기
        val sharedPreference=getSharedPreferences("Info",Context.MODE_PRIVATE)
        sharedPreference.edit(true){
            putString("groupId",roomId)
            putBoolean("isRegistered",true)
        }

        database.child("group").child(roomId!!).child("userNumber").get().addOnCompleteListener {
            if(it.isSuccessful){
                val number=it.result.value.toString().toInt()+1
                database.child("group").child(roomId!!).child("userNumber").setValue(number)

                progressBar.visibility= View.GONE
                val intent= Intent(this@RegisterActivity,HomeActivity::class.java)
                intent.putExtra("roomId", roomId)
                intent.putExtra("email", userEmail)
                onDestroy()

                intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }


    }

    private fun insertNewGroup(nickname:String, groupName:String, userNumber:Int){

        val key = database.root.child("group").push().key
        roomId =key
        if (key == null) {
            return
        }

        val users = ArrayList<User>()
        val group = Group(users,ArrayList<Work>(),com.moa.moa.Data.Log("",ArrayList<Work>()),userNumber,groupName)
        val groupValues = group.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/group/$key" to groupValues,
        )

        database.updateChildren(childUpdates).addOnSuccessListener {
            insertUser()
        }.addOnFailureListener {
            Toast.makeText(this,"데이터 저장에 실패했습니다",Toast.LENGTH_SHORT).show()
            progressBar.visibility= View.GONE
        }


        //database.root.child("users").child(userEmail).setValue(key)
    }

    private fun insertUser(){
        if(imageUri!=null){
            val ref=FirebaseStorage.getInstance().reference.child("profileImages/"+userEmail+"_profileimg.jpg")
            ref.putFile(imageUri!!).addOnSuccessListener {

                ref.downloadUrl.addOnSuccessListener {
                    val uri = it.toString()

                    val starCount = ArrayList<Int>()
                    for(i in 1..12){
                        starCount.add(0)
                    }

                    val badges= ArrayList<Badge>().apply{
                        add(Badge("초보","집안일 1개 이상 완료!",false))
                        add(Badge("중수","집안일 10개 이상 완료!",false))
                        add(Badge("고수","집안일 50개 이상 완료!",false))
                        add(Badge("알리미","알림 1번 이상 울림",false))
                        add(Badge("알리미 프로","알림 10번 이상 울림",false))
                        add(Badge("알리미 마스터","알림 50번 이상 울림",false))
                        add(Badge("이끔이","집안일 1개 이상 추가",false))
                        add(Badge("이끔이 프로","집안일 5개 이상 추가",false))
                        add(Badge("이끔이 마스터","집안일 10개 이상 추가",false))
                    }

                    val user = User(userEmail,nickname!!, uri,starCount, badges)

                    val userKey=database.child("group").child(roomId!!).child("users").push().key!!
                    database.child("group").child(roomId!!).child("users").child(userKey).setValue(user)
                    val sharedPreference=getSharedPreferences("Info",Context.MODE_PRIVATE)
                    sharedPreference.edit(true){
                        putString("userKey",userKey)
                    }

                    startHomeActivity()

                }

            }
        }
        else{
            val starCount = ArrayList<Int>()
            for(i in 1..12){
                starCount.add(0)
            }

            val badges= ArrayList<Badge>().apply{
                add(Badge("초보","집안일 1개 이상 완료!",false))
                add(Badge("중수","집안일 10개 이상 완료!",false))
                add(Badge("고수","집안일 50개 이상 완료!",false))
                add(Badge("알리미","알림 1번 이상 울림",false))
                add(Badge("알리미 프로","알림 10번 이상 울림",false))
                add(Badge("알리미 마스터","알림 50번 이상 울림",false))
                add(Badge("이끔이","집안일 1개 이상 추가",false))
                add(Badge("이끔이 프로","집안일 5개 이상 추가",false))
                add(Badge("이끔이 마스터","집안일 10개 이상 추가",false))
            }

            val user = User(userEmail,nickname!!, "null",starCount, badges)

            val userKey=database.child("group").child(roomId!!).child("users").push().key!!
            database.child("group").child(roomId!!).child("users").child(userKey).setValue(user)
            val sharedPreference=getSharedPreferences("Info",Context.MODE_PRIVATE)
            sharedPreference.edit(true){
                putString("userKey",userKey)
            }
            startHomeActivity()
        }

    }

    fun isEnabled(isValid:Boolean){
        profileSaveButton.isEnabled=isValid
    }

    inner class ProfilePageAdapter(fm: FragmentActivity): FragmentStateAdapter(fm){


        override fun getItemCount(): Int {
            return PAGE_NUM
        }

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0-> ProfileFragment()

                1-> ProfileImageFragment()

                2-> GroupFragment()

                3-> EnterGroupFragment()

                //4-> SettingGroupNumberFragment()

                4-> SettingGroupNameFragment()

                else -> ProfileFragment()
            }
        }
    }



}
