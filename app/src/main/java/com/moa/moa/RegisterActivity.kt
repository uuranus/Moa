package com.moa.moa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import de.hdodenhof.circleimageview.CircleImageView

class RegisterActivity : AppCompatActivity() {
    private var userEmail:String=""
    private val editProfileImg:CircleImageView by lazy{
        findViewById(R.id.editProfileImg)
    }
    private val editNickname:EditText by lazy{
        findViewById(R.id.editNickname)
    }
    private val profileSaveButton: Button by lazy{
        findViewById(R.id.profileSaveButton)
    }
    private var isValid:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        userEmail=intent.getStringExtra("userEmail")!!

        init()

    }

    private fun init(){
        editNickname.setOnEditorActionListener { textView, actionID, keyEvent ->
            if(actionID==EditorInfo.IME_ACTION_DONE){
                //닉네임 유효성 검사
                if(textView.text.isEmpty()){
                    Toast.makeText(this,"닉네임을 입력해주세요",Toast.LENGTH_SHORT).show()
                    isValid=false
                }
                else if(textView.text.length<2 || textView.text.length>20){
                    Toast.makeText(this,"닉네임은 2~20자 사이여야 합니다",Toast.LENGTH_SHORT).show()
                    isValid=false
                }
                else{
                    isValid=true
                }

            }

            return@setOnEditorActionListener false
        }

        profileSaveButton.setOnClickListener {
            //갤러리로 이동

        }

        profileSaveButton.setOnClickListener {
            //저장 할 건지 확인 팝업

            //기존 방 생성 화면에다가 값 전달
        }
    }

}