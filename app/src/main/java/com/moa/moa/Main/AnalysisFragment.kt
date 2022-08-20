package com.moa.moa.Main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.compose.animation.core.snap
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.moa.moa.Data.HomeNotYetSecondSection
import com.moa.moa.Data.HomeThirdSection
import com.moa.moa.Data.Person
import com.moa.moa.Home.HomeFirstSectionRecyclerViewAdapter
import com.moa.moa.Home.HomeNotYetSecondRecyclerViewAdapter
import com.moa.moa.R
import com.moa.moa.Utility
import com.moa.moa.databinding.FragmentAnalysisBinding
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class AnalysisFragment : Fragment() {
    private var _binding: FragmentAnalysisBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var analysisActivity: HomeActivity
    private lateinit var groupId :String
    private val utility=Utility()
    val chartList = ArrayList<ChartData>()
    lateinit var chartAdapter:ChartAdapter
    private var monthSelected = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 액티비티로 형변환해서 할당
        analysisActivity = context as HomeActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        groupId= utility.getGroupId(analysisActivity)
        chartAdapter = ChartAdapter(chartList)

        spinnerInit()

        return binding.root
    }

    private fun spinnerInit() {
        val itemList = resources.getStringArray(R.array.spinner_array)
        val adapter = ArrayAdapter(analysisActivity, R.layout.spinner_item, itemList)
        binding.spinner.setSelection(3)

        binding.spinner.dropDownVerticalOffset = dipToPixels(60f).toInt()

        binding.spinner.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        //val chartList = ArrayList<ChartData>()
//        chartList.add(ChartData("asd","나",15))
//        chartList.add(ChartData("asd","아빠",19))
//        chartList.add(ChartData("asd","엄마",25))
//        chartList.add(ChartData("asd","누나",13))
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        binding.spinner.setSelection(currentMonth)

        monthSelected = currentMonth

        val database = Firebase.database.reference
        val tmpGroupId ="-N5i_ow2ftDuKRjGzvgn"
        //Log.i("analysis", tmpGroupId)
        database.root.child("/group/${tmpGroupId}/users").addListenerForSingleValueEvent(object:ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                var name=""
                var starCount = 0
                if(snapshot.value ==null) {
                    Log.i("analysis", "null")
                }else{
                    for(child in snapshot.children){
                        Log.i("analysis", child.key.toString())
                        for(star in child.children){
                            Log.i("analysis1", star.value.toString())
                            if(star.key.toString() == "userName" ){
                                name = star.value.toString()
//                                Log.i("analysis", star.value.toString())
                                Log.i("analysisCD", name + starCount.toString())
                                chartList.add(ChartData("profileImage",name,starCount))
                            }
                            if(star.key.toString() == "starCount"){
                                starCount = star.child(currentMonth.toString()).value.toString().toInt()
//                                Log.i("analysisChild", starCount.toString())

                            }

                        }
                        chartAdapter.notifyDataSetChanged()
                    }
                }
                Log.i("analysis",chartList.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"데이터를 가져오는데 실패했습니다", Toast.LENGTH_SHORT).show()
            }

        })

        binding.chartRecyclerview.adapter =  chartAdapter

        spinnerClicked()
    }

    private fun spinnerClicked() {
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                monthSelected = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("MyTag", "onNothingSelected")
            }
        }
    }

    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}