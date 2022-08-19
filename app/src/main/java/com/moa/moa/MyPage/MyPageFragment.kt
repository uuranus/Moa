package com.moa.moa.MyPage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.moa.moa.R
import com.navercorp.nid.NaverIdLoginSDK


class MyPageFragment : Fragment() {

    private lateinit var gso: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private val logoutButton: Button by lazy {
        requireView().findViewById(R.id.logoutButton)
    }

    private val profileImg: ImageView by lazy {
        requireView().findViewById(R.id.profileImg)
    }

    private val nickname: TextView by lazy {
        requireView().findViewById(R.id.nickname)
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

        logoutButton.setOnClickListener {
            NaverIdLoginSDK.logout()
            mGoogleSignInClient.signOut()
        }

        initTabLayout()
    }

    private fun initTabLayout(){
        val array= arrayOf("별","뱃지")

        viewPager.adapter=ViewPagerAdapter(childFragmentManager, lifecycle)

        TabLayoutMediator(tabLayout,viewPager) { tab, position ->
            tab.text = array[position]
        }.attach()
    }

    inner class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            when (position) {
                0 -> StarFragment()
                1 -> BadgeFragment()
            }

            return StarFragment()
        }

    }

}