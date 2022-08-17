package com.moa.moa.MyPage

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
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
import com.moa.moa.Local.AppDatabase
import com.moa.moa.R
import com.moa.moa.Register.RegisterActivity
import com.moa.moa.Utility
import com.navercorp.nid.NaverIdLoginSDK
import org.w3c.dom.Text


class MyPageFragment : Fragment() {

    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private val utility = Utility()
    private lateinit var groupId: String
    private lateinit var userKey: String
    private lateinit var database: DatabaseReference

    private lateinit var db:AppDatabase

    private var isDefaultImg:Boolean=true


    private val profileImg: ImageView by lazy {
        requireView().findViewById(R.id.profileImg)
    }

    private val nickname: TextView by lazy {
        requireView().findViewById(R.id.nickname)
    }

    private val emailId: TextView by lazy {
        requireView().findViewById(R.id.emailId)
    }

    private val bellImage:ImageButton by lazy{
        requireView().findViewById(R.id.alarmButton)
    }

    private val alarmCount:TextView by lazy{
        requireView().findViewById(R.id.alarmNotReadCount)
    }

    private val moreButton:ImageButton by lazy{
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

        gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)


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
                        Glide.with(requireContext())
                            .load(snapshot.child("profileImage"))
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

            val quit=view.findViewById<TextView>(R.id.quit)
            val edit=view.findViewById<TextView>(R.id.editProfile)

            quit.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setMessage("정말로 탈퇴하시겠습니까?")
                    .setPositiveButton("네"){ _,_->
                        NaverIdLoginSDK.logout()
                        mGoogleSignInClient.signOut()

                        database.child("group").child(groupId).child("users").child(userKey).removeValue()

                        database.child("group").child(groupId).child("userNumber").get().addOnCompleteListener {
                            if(it.isSuccessful){
                                val result = it.result.value.toString().toInt()-1
                                database.child("group").child(groupId).child("userNumber").setValue(result)

                            }
                        }

                        requireActivity().finish()
                    }
                    .setNegativeButton("아니오"){_,_->

                    }
            }

            edit.setOnClickListener {

            }

            val builder= AlertDialog.Builder(requireContext())
                .setView(view).create()


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

    private fun showEditProfileDialog(){
        val view=LayoutInflater.from(requireContext()).inflate(R.layout.edit_profile_dialog,null)

        editProfileImage=view.findViewById<ImageView>(R.id.profileImg)
        val nicknameEdit=view.findViewById<EditText>(R.id.editNicknameDialog)
        val groupId=view.findViewById<TextView>(R.id.groupId)

        val okay=view.findViewById<Button>(R.id.editProfileOkay)

        editProfileImage.setOnClickListener {

            if(!isDefaultImg){

                val profile_view=LayoutInflater.from(requireContext()).inflate(R.layout.activity_register_profileimg_alertdialog,null)

                val dialog= android.app.AlertDialog.Builder(requireContext()).setView(profile_view).create()

                val chooseDefaultTextView: TextView =profile_view.findViewById(R.id.profileChooseDefault)
                val chooseGalleryTextView: TextView =profile_view.findViewById(R.id.profileChooseGallery)

                chooseDefaultTextView.setOnClickListener {
                    val uri= Uri.parse("android.resource://"+requireActivity().packageName+"/"+ R.drawable.ic_baseline_person_add_alt_1_24)
                    editProfileImage.setImageURI(uri)
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


        okay.setOnClickListener {
            //저장....

            initUser()
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
                editProfileImage.setImageURI(selectedImgUri)
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