package com.moa.moa.Register

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.content.ContextCompat
import com.moa.moa.R
import de.hdodenhof.circleimageview.CircleImageView


class ProfileFragment : Fragment() {


    private val editNickname: EditText by lazy{
        requireView().findViewById(R.id.editNickname)
    }

    private var isValid:Boolean=false
        set(value) {
            field=value
            val ra=activity as RegisterActivity
            ra.isEnabled(isValid)
        }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init(){

        val ra=activity as RegisterActivity
        ra.isEnabled(isValid)

        editNickname.setOnEditorActionListener { textView, actionID, keyEvent ->
            if(actionID== EditorInfo.IME_ACTION_DONE){
                isValidNickname(editNickname,ra)
            }
            return@setOnEditorActionListener false
        }


    }


    private fun isValidNickname(textView:TextView,ra:RegisterActivity){
        //닉네임 유효성 검사
        if(textView.text.isEmpty()){
            Toast.makeText(context,"닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
            isValid=false
        }
        else if(textView.text.length<2 || textView.text.length>20){
            Toast.makeText(context,"닉네임은 2~20자 사이여야 합니다", Toast.LENGTH_SHORT).show()
            isValid=false

        }
        else{
            ra.nickname=textView.text.toString()
            isValid=true

        }

    }


}

