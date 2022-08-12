package com.moa.moa.Main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.moa.moa.Home.HomeFragment
import com.moa.moa.MyPage.MyPageFragment
import com.moa.moa.R

class HomeActivity : AppCompatActivity() {
    private var doubleBackToExit = false

    private val frame: FrameLayout by lazy { // activity_main의 화면 부분
        findViewById(R.id.fl_container)
    }
    private val bottomNavigationView: BottomNavigationView by lazy { // 하단 네비게이션 바
        findViewById(R.id.bnv_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportFragmentManager.beginTransaction().add(frame.id, HomeFragment()).commit()
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeFragment -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.workFragment -> {
                    replaceFragment(WorkFragment())
                    true
                }
                R.id.analysisFragment -> {
                    replaceFragment(AnalysisFragment())
                    true
                }
                R.id.myPageFragment -> {
                    replaceFragment(MyPageFragment())
                    true
                }
                else -> false
            }
        }
    }
    // 화면 전환 구현 메소드
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(frame.id, fragment).commit()
    }
    //두번 탭시 종료
    override fun onBackPressed() {
        if (doubleBackToExit) {
            finishAffinity()

        } else {
            Toast.makeText(this,"앱을 종료합니다.",Toast.LENGTH_SHORT).show()
            doubleBackToExit = true
            runDelayed(1500L) {
                doubleBackToExit = false
            }
        }
    }
    private fun runDelayed(millis: Long, function: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(function, millis)
    }
}