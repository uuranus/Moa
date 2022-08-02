package com.moa.moa.Main

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
import com.moa.moa.R
import com.moa.moa.databinding.FragmentAnalysisBinding

class AnalysisFragment : Fragment() {
    private var _binding: FragmentAnalysisBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var homeActivity: HomeActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 액티비티로 형변환해서 할당
        homeActivity = context as HomeActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)

        spinnerInit()

        return binding.root
    }

    private fun spinnerInit() {
        val itemList = resources.getStringArray(R.array.spinner_array)
        val adapter = ArrayAdapter(homeActivity, R.layout.spinner_item, itemList)
        binding.spinner.setSelection(3)

        binding.spinner.dropDownVerticalOffset = dipToPixels(60f).toInt()

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                when (position) {
                    0 -> {

                    }
                    1 -> {

                    }
                    //...
                    else -> {

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("MyTag", "onNothingSelected")
            }
        }

        binding.spinner.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        val chartlist = ArrayList<ChartData>()
        chartlist.add(ChartData("asd","나",15))
        chartlist.add(ChartData("asd","아빠",19))
        chartlist.add(ChartData("asd","엄마",25))
        chartlist.add(ChartData("asd","누나",13))

        val adapter = ChartAdapter(chartlist)
        binding.chartRecyclerview.adapter =  adapter


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