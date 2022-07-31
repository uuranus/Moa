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
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.moa.moa.R
import de.hdodenhof.circleimageview.CircleImageView


class ProfileImageFragment : Fragment() {
    private val editProfileImg: ImageButton by lazy{
        requireView().findViewById(R.id.editProfileImg)
    }

    private var isDefaultImg:Boolean=true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init(){
        editProfileImg.setOnClickListener {

            if(!isDefaultImg){

                val view=LayoutInflater.from(requireContext()).inflate(R.layout.activity_register_profileimg_alertdialog,null)

                val dialog= AlertDialog.Builder(requireContext()).setView(view).create()

                val chooseDefaultTextView: TextView =view.findViewById(R.id.profileChooseDefault)
                val chooseGalleryTextView: TextView =view.findViewById(R.id.profileChooseGallery)

                chooseDefaultTextView.setOnClickListener {
                    val uri= Uri.parse("android.resource://"+requireActivity().packageName+"/"+ R.drawable.ic_baseline_person_add_alt_1_24)
                    editProfileImg.setImageURI(uri)
                    val ra=activity as RegisterActivity
                    ra.imageUri=null
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
                val ra=activity as RegisterActivity
                ra.imageUri=selectedImgUri
                isDefaultImg=false
            }
        }

    }

    private fun getProfileImg(){
        val intent= Intent(Intent.ACTION_GET_CONTENT)
        intent.type="image/*"
        startActivityForResult(intent,2000)
    }
}