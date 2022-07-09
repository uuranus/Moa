package com.moa.moa

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.moa.moa.Data.Group
import com.moa.moa.Data.User
import com.moa.moa.Data.Work
import com.moa.moa.Main.HomeActivity

class RegisterActivity : FragmentActivity() {
    private val database = Firebase.database.reference

    var roomId: String? = null
    var roomName: String? = null
    var roomNumber:Int = 1
    var nickname:String? = null
    private var userEmail:String=""

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

            backButton.isEnabled = value != 0

        }

    var isExisting : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // userEmail=intent.getStringExtra("userEmail")!!
        userEmail="asd123"

        init()

    }

    override fun onBackPressed() {
        if(state!=0){
            viewPager.currentItem--
            state=viewPager.currentItem
        }
        else{
            AlertDialog.Builder(this)
                .setMessage("회원가입을 그만두시겠습니까?")
                .setPositiveButton("네"){_,_ ->
                    super.onBackPressed()
                }
                .setNegativeButton("아니오"){_,_ ->

                }
                .show()
        }

    }
    private fun init() {

        profilePageAdapter = ProfilePageAdapter(this)
        viewPager.adapter = profilePageAdapter

        //swipe 막기
        viewPager.isUserInputEnabled=false

        backButton.setOnClickListener {
            when (viewPager.currentItem) {
                0 -> {
                    viewPager.currentItem=0
                    state=viewPager.currentItem
                }
                3 -> {
                    viewPager.currentItem = 1
                    state=viewPager.currentItem
                }
                else -> {
                    viewPager.currentItem--
                    state=viewPager.currentItem
                }
            }
        }

        profileSaveButton.isEnabled=false
        backButton.isEnabled=false
        profileSaveButton.setOnClickListener {
            if(state==PAGE_NUM-1){ //마지막 페이지에서 save 버튼 눌렀을 때 HomeActivity 로 넘어가면 된다.
                if(viewPager.currentItem == 2){ //enterFragment to homeActivity 데이터베이스에서 사용자가 입력한 방id가 존재하는지 검사
                    database.child("users").child(userEmail).child(roomId!!).get().addOnSuccessListener {
                        Toast.makeText(this," group ID : $roomId",Toast.LENGTH_SHORT).show()
                        Log.i("firebase", "Got value ${it.value}")
                        if(it.value!=null)
                            startHomeActivity()
                    }.addOnFailureListener{
                        Toast.makeText(this,"$roomId 방이 존재하지 않습니다. ",Toast.LENGTH_SHORT).show()
                        Log.e("firebase", "Error getting data", it)
                    }
                }else{ //settingGroupName to homeActivity 방을 생성한 경우이므로 database 에 저장해야한다.
                    if(roomName==null || roomName == ""){
                        Toast.makeText(this,"잘못된 그룹 이름입니다! 다시 입력해주세요",Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    insertNewGroup(nickname!!,"imageurl",roomName!!,roomNumber)
                    startHomeActivity()
                }

            }
            else{
                if(viewPager.currentItem == 1){ //1번 페이지에서 기존방 입장 or 새로운 그룹 생성 분기
                    when (isExisting) {
                        0 -> { //기존방이 존재하지 않는 경우 -> 3번페이지로 SettingGroupNumberFragment
                            profileSaveButton.isEnabled = true
                            viewPager.currentItem = 3
                            state=viewPager.currentItem
                        }
                        1 -> { //기존방이 존재하는 경우 -> 2번페이지로 EnterGroupFragment
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

    private fun startHomeActivity() {
        val intent= Intent(this@RegisterActivity,HomeActivity::class.java)
        intent.putExtra("roomId", roomId)
        intent.putExtra("email", userEmail)
        onDestroy()

        startActivity(intent)
    }

    private fun insertNewGroup(nickname:String, image:String, groupName:String, userNumber:Int){

        val key = database.root.child("group").push().key
        roomId =key
        if (key == null) {
            Log.w("TAG", "Couldn't get push key for posts")
            return
        }

        //key값 sharedPrefenece에 저장하고 있기
        val sharedPreference=getSharedPreferences("Info",Context.MODE_PRIVATE)
        sharedPreference.edit(true){
            putString("groupID",key)
        }


        val user = User(userEmail,nickname,image)
        val users = ArrayList<User>()
        users.add(user)
        val group = Group(users,ArrayList<Work>(),com.moa.moa.Data.Log("",ArrayList<Work>()),userNumber,groupName)
        val groupValues = group.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/group/$key" to groupValues,
            "/users/$userEmail/$key" to groupValues
        )

        database.updateChildren(childUpdates).addOnSuccessListener {
            Toast.makeText(this,"Data insert success!",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this,"Data insert fail!",Toast.LENGTH_SHORT).show()
        }

        //database.root.child("users").child(userEmail).setValue(key)
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

                1-> GroupFragment()

                2-> EnterGroupFragment()

                3-> SettingGroupNumberFragment()

                4-> SettingGroupNameFragment()

                else -> ProfileFragment()
            }
        }


    }



}
