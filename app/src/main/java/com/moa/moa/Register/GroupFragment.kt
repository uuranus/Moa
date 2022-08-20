package com.moa.moa.Register

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moa.moa.R
import com.moa.moa.databinding.FragmentGroupBinding


class GroupFragment : Fragment() {
    private var _binding: FragmentGroupBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var registerActivity: RegisterActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 2. Context를 액티비티로 형변환해서 할당
        registerActivity = context as RegisterActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGroupBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        binding.createBtn.isSoundEffectsEnabled = false
        binding.enterBtn.isSoundEffectsEnabled = false

        //val viewPager=registerActivity.findViewById<ViewPager2>(R.id.profileViewPager)
        binding.newImg.setOnClickListener {
            binding.createBtn.performClick()
        }
        binding.existingImg.setOnClickListener {
            binding.enterBtn.performClick()
        }
        registerActivity.isExisting = 0
        binding.isExistingGroup.setOnCheckedChangeListener { radioGroup, i ->

            if(binding.enterBtn.isChecked){
                binding.newImg.setImageResource(R.drawable.new_group)
                binding.existingImg.setImageResource(R.drawable.existing_focus)
                registerActivity.isExisting = 1
            }
            if(binding.createBtn.isChecked){
                binding.newImg.setImageResource(R.drawable.new_focus)
                binding.existingImg.setImageResource(R.drawable.existing_group)
                registerActivity.isExisting = 0
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}