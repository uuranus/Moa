package com.moa.moa

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

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

    private val PAGE_NUM=2

    private var state:Int=0
        set(value) {
            field = value
            if(value==PAGE_NUM-1){
                profileSaveButton.text= "저장"
            }
            else{
                profileSaveButton.text= "다음"
            }

            backButton.isEnabled = value != 0

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        userEmail=intent.getStringExtra("userEmail")!!

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
            if(viewPager.currentItem!=0){
                viewPager.currentItem--
                state=viewPager.currentItem
            }
        }

        profileSaveButton.isEnabled=false
        backButton.isEnabled=false
        profileSaveButton.setOnClickListener {
            if(viewPager.currentItem==PAGE_NUM-1){

            }
            else{
                viewPager.currentItem++
                state=viewPager.currentItem

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

                1-> BlankFragment()

                else -> ProfileFragment()
            }
        }


    }



}
