package com.moa.moa

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.content.ContextCompat
import de.hdodenhof.circleimageview.CircleImageView


class ProfileFragment : Fragment() {

    private val editProfileImg: CircleImageView by lazy{
        requireView().findViewById(R.id.editProfileImg)
    }
    private val editNickname: EditText by lazy{
        requireView().findViewById(R.id.editNickname)
    }

    private var isValid:Boolean=false
        set(value) {
            field=value
            val ra=activity as RegisterActivity
            ra.isEnabled(isValid)
        }

    private var isDefaultImg:Boolean=true

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
                    isValid=true

                }

            }

            return@setOnEditorActionListener false
        }

        editProfileImg.setOnClickListener {

            if(!isDefaultImg){

                val view=LayoutInflater.from(requireContext()).inflate(R.layout.activity_register_profileimg_alertdialog,null)

                val dialog=AlertDialog.Builder(requireContext()).setView(view).create()

                val chooseDefaultTextView:TextView=view.findViewById(R.id.profileChooseDefault)
                val chooseGalleryTextView:TextView=view.findViewById(R.id.profileChooseGallery)

                chooseDefaultTextView.setOnClickListener {
                    val uri=Uri.parse("android.resource://"+requireActivity().packageName+"/"+R.drawable.default_img)
                    editProfileImg.setImageURI(uri)
                    isDefaultImg=true
                    dialog.dismiss()
                }

                chooseGalleryTextView.setOnClickListener {
                    getProfileImg()
                    dialog.dismiss()
                }

                dialog.show()

            }
            else{
                //갤러리로 이동

                if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    getProfileImg()
                }
                else{
                    shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),200)
                }
            }



        }



    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==200){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getProfileImg()
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==2000){
            val selectedImgUri: Uri?=data?.data
            if(selectedImgUri!=null){
                editProfileImg.setImageURI(selectedImgUri)
                isDefaultImg=false
            }
        }

    }

    private fun getProfileImg(){
        val intent=Intent(Intent.ACTION_GET_CONTENT)
        intent.type="image/*"
        startActivityForResult(intent,2000)
    }
}

