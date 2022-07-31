package com.moa.moa.Register

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.moa.moa.databinding.FragmentSettingGroupNameBinding


class SettingGroupNameFragment : Fragment() {
    private var _binding: FragmentSettingGroupNameBinding? = null
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
        _binding = FragmentSettingGroupNameBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init(){
        binding.editTextTextGroupName.addTextChangedListener {
            registerActivity.roomName=binding.editTextTextGroupName.text.toString()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}