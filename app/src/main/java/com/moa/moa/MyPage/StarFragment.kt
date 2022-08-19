package com.moa.moa.MyPage

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.compose.animation.core.snap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.moa.moa.Local.AppDatabase
import com.moa.moa.Local.Complement
import com.moa.moa.R
import com.moa.moa.Utility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StarFragment : Fragment() {

    private val thisStartCount: TextView by lazy{
        requireView().findViewById(R.id.thisMonthStarCount)
    }

    private val thisFamilyCount:TextView by lazy{
        requireView().findViewById(R.id.thisMonthFamilyCount)
    }

    private val starRecyclerView:RecyclerView by lazy{
        requireView().findViewById(R.id.complementRecyclerView)
    }

    private val starUsedRecyclerView:RecyclerView by lazy{
        requireView().findViewById(R.id.complementUsedRecyclerView)
    }

    private val utility= Utility()
    private lateinit var database:DatabaseReference
    private lateinit var groupId:String
    private lateinit var userKey:String

    private lateinit var adapter:ComplementRecyclerViewAdapter
    private lateinit var usedAdapter:ComplementUsedRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_star, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupId=utility.getGroupId(requireActivity())
        userKey=utility.getUserKey(requireActivity())
        database=FirebaseDatabase.getInstance().reference

        initCount()

        getComplements()
    }

    private fun initCount(){

        val thisMonth=utility.getToday().substring(5,7).toInt()

        database.child("group").child(groupId).child("users").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.value?:return

                var me=0
                var fam=0
                var famCount=0

                for(family in snapshot.children){
                    Log.i("fam",family.child("starCount").child(thisMonth.toString()).toString())
                    if(family.key==userKey){
                        me=family.child("starCount").child(thisMonth.toString()).value.toString().toInt()
                    }
                    else{
                        fam+=family.child("starCount").child(thisMonth.toString()).value.toString().toInt()
                        famCount++
                    }
                }

                thisStartCount.text="$me"
                if(famCount!=0) thisFamilyCount.text="${(fam/famCount)}"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun initRecyclerView(list:List<Complement>,usedList:List<Complement>){

        Log.i("listttttt",list.toString())
        Log.i("listttttt",usedList.toString())

        adapter=ComplementRecyclerViewAdapter(list)
        adapter.complementInterface=object:ComplementRecyclerViewAdapter.ComplementInterface{

            override fun complementAdd(isEdit: Boolean, complement: Complement?) {
                showComplementDialog(isEdit,complement)
            }

            override fun complementUse(uid: String) {
                useComplement(uid)
            }

            override fun complementDelete(uid: String) {
                deleteComplement(uid)
            }

        }
        starRecyclerView.adapter=adapter
        starRecyclerView.layoutManager=LinearLayoutManager(requireContext())

        Log.i("adaterSize",adapter.itemCount.toString())

        usedAdapter= ComplementUsedRecyclerViewAdapter(usedList)
        starUsedRecyclerView.adapter=usedAdapter
        starUsedRecyclerView.layoutManager=LinearLayoutManager(requireContext())
    }

    private fun showComplementDialog(isEdit:Boolean, complement: Complement?){

        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.complement_alrerdialog, null)

        val builder=AlertDialog.Builder(requireContext()).setView(view).create()


        val title = view.findViewById<EditText>(R.id.complementAddTitle)
        val description = view.findViewById<EditText>(R.id.complementAddDescription)
        val starCount=view.findViewById<TextView>(R.id.starCount)
        val starMinus=view.findViewById<TextView>(R.id.starMinus)
        val starPlus=view.findViewById<TextView>(R.id.starPlus)

        var star=starCount.text.removeSuffix("개").toString().toInt()

        val star1=view.findViewById<ImageView>(R.id.star1)
        val star2=view.findViewById<ImageView>(R.id.star2)
        val star3=view.findViewById<ImageView>(R.id.star3)
        val star4=view.findViewById<ImageView>(R.id.star4)
        val star5=view.findViewById<ImageView>(R.id.star5)
        val stars= listOf<ImageView>(star1,star2,star3,star4,star5)

        stars[0].isSelected=true

        starMinus.setOnClickListener {
            if(star!=1) star--
            starCount.text="${star}개"
            for(i in 0 until 5){
                stars[i].isSelected= i<star
            }
        }

        starPlus.setOnClickListener {
            if(star!=5) star++
            starCount.text="${star}개"
            for(i in 0 until 5){
                stars[i].isSelected= i<star
            }
        }

        val okayButton = view.findViewById<Button>(R.id.complementAddOkay)

        okayButton.setOnClickListener {
            if(title.text.isEmpty()){
                Toast.makeText(requireContext(),"보상 제목을 입력하세요!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val com=Complement("",star,title.text.toString(), description.text.toString(),0)
            if(isEdit) {

                deleteComplement(complement?.uid!!)
                insertComplement(com)
            }
            else insertComplement(com)

            builder.dismiss()

        }

        if(isEdit){
            title.setText(complement?.title)
            description.setText(complement?.description)
            star=complement?.star!!
        }

        builder.show()

    }

    private fun insertComplement(complement: Complement){
        val uid= database.child("group").child(groupId).child("users").child(userKey).child("complements").push().key!!

        complement.uid=uid

        database.child("group").child(groupId).child("users")
            .child(userKey).child("complements").child(uid).setValue(complement)

        getComplements()

    }

    private fun useComplement(uid:String){
        database.child("group").child(groupId).child("users")
            .child(userKey).child("complements").child(uid).child("used").setValue(1)

        getComplements()

    }

    private fun deleteComplement(uid:String){
        database.child("group").child(groupId).child("users")
            .child(userKey).child("complements").child(uid).removeValue()

    }

    private fun getComplements(){

        database.child("group").child(groupId).child("users")
            .child(userKey).child("complements").addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list= mutableListOf<Complement>()
                    val usedList= mutableListOf<Complement>()

                    Log.i("recyclerview",snapshot.value.toString())
                    Log.i("recyclerView",usedList.toString())

                    if(snapshot.value==null){
                        initRecyclerView(list,usedList)
                        return
                    }


                    snapshot.children.forEach { dataSnapshot ->
                        Log.i("dataSnapshot",dataSnapshot.toString())
                        dataSnapshot.getValue<Complement>().let {
                            it?:return@let

                            if(it.used==1) usedList.add(it)
                            else list.add(it)
                        }
                    }
                    Log.i("recyclerview2",list.toString())
                    Log.i("recyclerView2",usedList.toString())

                    initRecyclerView(list,usedList)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }


}