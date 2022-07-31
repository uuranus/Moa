package com.moa.moa.Main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.moa.moa.R
import com.navercorp.nid.NaverIdLoginSDK


class MyPageFragment : Fragment() {

    private lateinit var gso:GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private val logoutButton: Button by lazy{
        requireView().findViewById(R.id.logoutButton)
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

        gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        mGoogleSignInClient= GoogleSignIn.getClient(requireActivity(),gso)

        logoutButton.setOnClickListener {
            NaverIdLoginSDK.logout()
            mGoogleSignInClient.signOut()
        }
    }

}