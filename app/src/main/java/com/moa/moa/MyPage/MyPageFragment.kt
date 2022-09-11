package com.moa.moa.MyPage

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.room.Room
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.moa.moa.Local.AppDatabase
import com.moa.moa.R
import com.moa.moa.Utility
import com.navercorp.nid.NaverIdLoginSDK


class MyPageFragment : Fragment() {

//    private lateinit var gso: GoogleSignInOptions
//    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private val utility = Utility()
    private lateinit var groupId: String
    private lateinit var userKey: String
    private lateinit var database: DatabaseReference

//    private val OAUTH_CLIENT_ID:String by lazy {
//        resources.getString(R.string.naver_client_id)
//    }
//    private val OAUTH_CLIENT_SECRET:String by lazy {
//        resources.getString(R.string.naver_client_secret)
//    }
//    private val OAUTH_CLIENT_NAME:String by lazy {
//        resources.getString(R.string.naver_client_name)
//    }

    private lateinit var db:AppDatabase

    private var isDefaultImg:Boolean=true
    private var imageUri:Uri?=null

    private val profileImg: ImageView by lazy {
        requireView().findViewById(R.id.profileImg)
    }

    private val nickname: TextView by lazy {
        requireView().findViewById(R.id.nickname)
    }

    private val emailId: TextView by lazy {
        requireView().findViewById(R.id.emailId)
    }

    private val bellImage:ImageView by lazy{
        requireView().findViewById(R.id.alarmButton)
    }

    private val alarmCount:TextView by lazy{
        requireView().findViewById(R.id.alarmNotReadCount)
    }

    private val moreButton:ImageView by lazy{
        requireView().findViewById(R.id.moreButton)
    }

    private val tabLayout: TabLayout by lazy {
        requireView().findViewById(R.id.tabLayout)
    }

    private val viewPager: ViewPager2 by lazy {
        requireView().findViewById(R.id.viewPager)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        NaverIdLoginSDK.initialize(requireContext(),OAUTH_CLIENT_ID,OAUTH_CLIENT_SECRET,OAUTH_CLIENT_NAME)
//
//
//        gso =
//            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()
//
//        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


        groupId = utility.getGroupId(requireActivity())
        userKey = utility.getUserKey(requireActivity())
        database = FirebaseDatabase.getInstance().reference

        db = Room.databaseBuilder(
            requireActivity().applicationContext,
            AppDatabase::class.java,
            "MoaDB"
        ).fallbackToDestructiveMigration()
            .build()


        initUser()

        initAlarm()

        initTabLayout()
    }

    private fun initUser() {
        database.child("group").child(groupId).child("users")
            .child(userKey).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.child("profileImage").value.toString() != "null") {
                        FirebaseStorage.getInstance().getReferenceFromUrl(snapshot.child("profileImage").value.toString())
                            .downloadUrl.addOnSuccessListener {
                                imageUri=it
                                isDefaultImg=false
                                Glide.with(requireContext())
                                    .load(it)
                                    .circleCrop()
                                    .into(profileImg)
                            }

                    }
                    else{
                        Glide.with(requireContext())
                            .load(R.drawable.default_img)
                            .circleCrop()
                            .into(profileImg)
                    }

                    nickname.text = snapshot.child("userName").value.toString()
                    emailId.text = snapshot.child("userId").value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun initAlarm(){
        bellImage.setOnClickListener {
            //알람 리스트 목록으로 이동
            val intent= Intent(requireActivity(),AlarmActivity::class.java)
            startActivity(intent)
        }

        moreButton.setOnClickListener {
            //탈퇴, 정보 수정 화면
            val view=LayoutInflater.from(requireContext()).inflate(R.layout.more_button_dialog,null)

            val builder = AlertDialog.Builder(requireContext())
                .setView(view)

            val quit=view.findViewById<TextView>(R.id.quit)
            val edit=view.findViewById<TextView>(R.id.editProfile)

            val dialog = builder.create()
            quit.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setMessage("정말로 탈퇴하시겠습니까?")
                    .setPositiveButton("네"){ _,_->
//                        NaverIdLoginSDK.logout()
//                        mGoogleSignInClient.signOut()

                        val sp=requireActivity().getSharedPreferences("Info",Context.MODE_PRIVATE)

                        //둘러보기였으면
                        if(sp.getString("userId","")=="-NBVsFcvYHQLfZOTqTqE"){
                            sp.edit(true){
                                putBoolean("isRegistered",false)
                            }
                        }
                        else{
                            //sharedPrefences 삭제
                            val sharedPreferences=requireActivity().getSharedPreferences("Info",Context.MODE_PRIVATE).edit()
                            sharedPreferences.clear()
                            sharedPreferences.commit()

                            //room database 삭제
                            Thread{
                                db.alarmDao().deleteAll()
                                db.titleDao().deleteAll()
                            }.start()


                            database.child("group").child(groupId).child("users").child(userKey).removeValue()

                            database.child("group").child(groupId).child("userNumber").get().addOnCompleteListener {
                                if(it.isSuccessful){
                                    val result = it.result.value.toString().toInt()-1
                                    database.child("group").child(groupId).child("userNumber").setValue(result)

                                }
                            }
                        }

                        requireActivity().finish()
                    }
                    .setNegativeButton("아니오"){_,_->

                    }.show()
            }

            edit.setOnClickListener {
                showEditProfileDialog()
                dialog.dismiss()
            }

            dialog.show()
        }

        Thread{
            val alarms=db.alarmDao().getNotRead()

            requireActivity().runOnUiThread {
                if(alarms.isEmpty()) alarmCount.isVisible=false
                else{
                    alarmCount.text="${alarms.size}"
                    alarmCount.isVisible=true
                }

            }
        }.start()
    }

    private fun initTabLayout() {
        val array = arrayOf("별", "뱃지")

        viewPager.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = array[position]
        }.attach()
    }

    inner class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> StarFragment()
                1 -> BadgeFragment()
                else -> StarFragment()
            }

        }

    }

    private lateinit var editProfileImage:ImageView
    private var selectedImgUri:Uri?=null

    private fun showEditProfileDialog(){
        val view=LayoutInflater.from(requireContext()).inflate(R.layout.edit_profile_dialog,null)

        val builder=AlertDialog.Builder(requireContext()).setView(view).create()

        editProfileImage=view.findViewById<ImageView>(R.id.profileImg)
        val nicknameEdit=view.findViewById<EditText>(R.id.editNicknameDialog)
        val groupIdText=view.findViewById<TextView>(R.id.groupId)

        if(imageUri!=null){
            Glide.with(requireContext())
                .load(imageUri)
                .into(editProfileImage)

        }

        nicknameEdit.setText(nickname.text)
        groupIdText.text=groupId

        val okay=view.findViewById<Button>(R.id.editProfileOkay)

        editProfileImage.setOnClickListener {

            if(!isDefaultImg){

                val profile_view=LayoutInflater.from(requireContext()).inflate(R.layout.activity_register_profileimg_alertdialog,null)

                val dialog= android.app.AlertDialog.Builder(requireContext()).setView(profile_view).create()

                val chooseDefaultTextView: TextView =profile_view.findViewById(R.id.profileChooseDefault)
                val chooseGalleryTextView: TextView =profile_view.findViewById(R.id.profileChooseGallery)

                chooseDefaultTextView.setOnClickListener {
                    val uri= Uri.parse("android.resource://"+requireActivity().packageName+"/"+ R.drawable.default_img)
                    editProfileImage.setImageURI(uri)
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

        okay.setOnClickListener {
            if(nicknameEdit.length()==0) {
                Toast.makeText(requireContext(),"닉네임을 입력하세요",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //저장....
            if(!isDefaultImg){
                val ref=FirebaseStorage.getInstance().reference.child("profileImages/${emailId.text}_profileimg.jpg")
                ref.putFile(selectedImgUri!!).addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        val uri=it

                        database.child("group").child(groupId).child("users").child(userKey)
                            .child("userName").setValue(nicknameEdit.text.toString())
                        database.child("group").child(groupId).child("users").child(userKey)
                            .child("profileImage").setValue(uri.toString())

                        initUser()

                        builder.dismiss()
                    }

                }
            }
            else{
                database.child("group").child(groupId).child("users").child(userKey)
                    .child("userName").setValue(nicknameEdit.text.toString())
                database.child("group").child(groupId).child("users").child(userKey)
                    .child("profileImage").setValue("null")

                initUser()

                builder.dismiss()
            }


        }

        builder.show()
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
            selectedImgUri=data?.data
            if(selectedImgUri!=null){
                editProfileImage.setImageURI(selectedImgUri)
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