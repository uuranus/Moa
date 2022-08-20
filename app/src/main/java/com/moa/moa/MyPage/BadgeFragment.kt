package com.moa.moa.MyPage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import com.google.firebase.database.*
import com.moa.moa.Data.Badge
import com.moa.moa.R
import com.moa.moa.Utility

class BadgeFragment : Fragment() {

    private val thisMonthBadge: TextView by lazy{
        requireView().findViewById(R.id.thisMonthBadgeCount)
    }
    private val thisMonthFamilyBadge:TextView by lazy{
        requireView().findViewById(R.id.thisMonthFamilyCount)
    }

    private val badgeMoreButton:TextView by lazy{
        requireView().findViewById(R.id.badgeMoreButton)
    }

    private val badgeGridLayout: GridView by lazy{
        requireView().findViewById(R.id.badgeGridLayout)
    }

    private val utility= Utility()
    private lateinit var database: DatabaseReference
    private lateinit var groupId:String
    private lateinit var userKey:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_badge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupId=utility.getGroupId(requireActivity())
        userKey=utility.getUserKey(requireActivity())
        database= FirebaseDatabase.getInstance().reference

        initCount()

        initGridLayout()
    }

    private fun initCount(){

        val thisMonth=utility.getToday().substring(5,7).toInt()

        database.child("group").child(groupId).child("users").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.value?:return

                var me=0
                var fam=0
                var famCount=0

                for(family in snapshot.children){

                    family.child("badges").value?:continue
                    if(family.key==userKey) {
                        family.child("badges").children.forEach {
                            if (it.child("second").value.toString().toBoolean()) {
                                me++
                            }
                        }
                    }
                    else{
                        family.child("badges").children.forEach {
                            if (it.child("second").value.toString().toBoolean()) {
                                fam++
                            }
                        }
                        famCount++
                    }
                }

                thisMonthBadge.text="$me"
                if(famCount!=0) thisMonthFamilyBadge.text="${(fam/famCount)}"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun initGridLayout(){

        database.child("group").child(groupId).child("users").child(userKey).child("badges").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.value?:return

                val badges= mutableListOf<Badge>()
                snapshot.children.forEach {
                    val index=it.key.toString().toInt()
                    val name=it.child("name").value.toString()
                    val description=it.child("description").value.toString()
                    val get=it.child("get").value.toString().toBoolean()
                    badges.add(index,Badge(name, description, get))
                }

                badgeGridLayout.adapter=GridAdapter(requireContext(),badges)
                badgeGridLayout.setOnTouchListener { _, _ ->
                    return@setOnTouchListener true
                }

                badgeMoreButton.setOnClickListener {
                    val intent= Intent(requireContext(),BadgeInfoActivity::class.java)

                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    inner class GridAdapter(private val context: Context, private val badges:List<Badge>): BaseAdapter() {

        override fun getCount(): Int {
            return badges.size
        }

        override fun getItem(position: Int): Any {
            return badges[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
            val inflater:LayoutInflater=context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val gridView:View
            if(view==null){
                gridView=inflater.inflate(R.layout.badge_row,null)

                val badeImage=gridView.findViewById<ImageView>(R.id.badgeImage)
                if(badges[position].get) badeImage.setImageResource(R.drawable.ic_baseline_insert_emoticon_24)
                else badeImage.setImageResource(R.drawable.ic_baseline_insert_emoticon_24)

                val badgeName=gridView.findViewById<TextView>(R.id.badgeName)
                badgeName.text=badges[position].name
            }
            else{
                gridView=view
            }

            return gridView
        }

    }

}

