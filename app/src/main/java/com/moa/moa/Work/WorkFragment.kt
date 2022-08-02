package com.moa.moa.Main

import android.content.Intent
import android.content.LocusId
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.moa.moa.Data.Work
import com.moa.moa.R
import com.moa.moa.Utility
import com.moa.moa.Work.WorkActivity
import com.moa.moa.Work.WorkAdapter

class WorkFragment : Fragment() {

    private lateinit var database:DatabaseReference
    private lateinit var groupId: String
    private val utility=Utility()

    private lateinit var adapter:WorkAdapter

    private val workAddButton:TextView by lazy{
        requireView().findViewById(R.id.workAddButton)
    }

    private val workListRecyclerView:RecyclerView by lazy{
        requireView().findViewById(R.id.workListRecyclerView)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_work, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database=FirebaseDatabase.getInstance().reference
        groupId=utility.getGroupId(requireActivity())

        initView()
    }

    override fun onResume() {
        super.onResume()
        initRecyclerView()
    }

    private fun initView(){
        workListRecyclerView.isEnabled=false
        workAddButton.setOnClickListener {
            val intent= Intent(requireContext(),WorkActivity::class.java)
            val size=adapter.currentList[adapter.itemCount-1].workId+1
            intent.putExtra("isEdit",false)
            intent.putExtra("workId",size)
            startActivity(intent)
        }

        adapter=WorkAdapter(workEditClickedListener = {
            val intent=Intent(requireContext(),WorkActivity::class.java)
            intent.putExtra("isEdit",true)
            intent.putExtra("editData",it)
            startActivity(intent)
        })

        workListRecyclerView.adapter=adapter

        workListRecyclerView.layoutManager=LinearLayoutManager(requireContext())

    }

    private fun initRecyclerView(){
        FirebaseDatabase.getInstance().reference.child("group").child(groupId).child("worklist").addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Work>()
                snapshot.children.forEach { dataSnapshot ->

                    dataSnapshot.getValue<Work>()?.let { it2->
                        Log.i("worklist",it2.toString())
                        list.add(it2)
                    }
                }
                adapter.submitList(list)
                workAddButton.isEnabled=true
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"데이터를 가져오는데 실패했습니다",Toast.LENGTH_SHORT).show()
            }

        })
    }
}