package com.moa.moa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.moa.moa.Home.HomeActivity

class RegisterActivity : FragmentActivity() {
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

        }

    var isExisting : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // userEmail=intent.getStringExtra("userEmail")!!
        userEmail="asd123"

        init()

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
        profileSaveButton.setOnClickListener {
            if(viewPager.currentItem==PAGE_NUM-1){ //마지막 페이지에서 save버튼 눌렀을 때 HomeActivity로 넘어가면 된다.
                val intent= Intent(this@RegisterActivity,HomeActivity::class.java)
                startActivity(intent)
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
