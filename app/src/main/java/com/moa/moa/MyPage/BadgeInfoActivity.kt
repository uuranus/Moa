package com.moa.moa.MyPage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.*
import com.moa.moa.Data.Badge
import com.moa.moa.R
import com.moa.moa.Utility
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import kotlin.math.absoluteValue

class BadgeInfoActivity : AppCompatActivity() {
    private val backButton: ImageView by lazy {
        findViewById(R.id.backButton)
    }

    private val viewPager: ViewPager2 by lazy {
        findViewById(R.id.viewPager2)
    }

    private val indicator: WormDotsIndicator by lazy {
        findViewById(R.id.indicator)
    }

    private val utility = Utility()
    private lateinit var database: DatabaseReference
    private lateinit var groupId: String
    private lateinit var userKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badge_info)

        groupId = utility.getGroupId(this)
        userKey = utility.getUserKey(this)
        database = FirebaseDatabase.getInstance().reference

        init()

    }

    private fun init() {
        backButton.setOnClickListener {
            finish()
        }

        database.child("group").child(groupId).child("users").child(userKey).child("badges")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.value ?: return

                    val badges = mutableListOf<Badge>()
                    snapshot.children.forEach {
                        val index = it.key.toString().toInt()
                        val name = it.child("name").value.toString()
                        val description = it.child("description").value.toString()
                        val get = it.child("get").value.toString().toBoolean()
                        badges.add(index, Badge(name, description, get))
                    }

                    viewPager.adapter = BadgeInfoAdapter(badges)
                    viewPager.setPageTransformer { page, position ->
                        when {
                            position.absoluteValue >= 1.0F -> {
                                page.alpha = 0F
                            }
                            position == 0F -> {
                                page.alpha = 1F
                            }
                            else -> {
                                page.alpha = 1F - 2 * position.absoluteValue
                            }
                        }
                    }
                    indicator.setViewPager2(viewPager)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


    }

    inner class BadgeInfoAdapter(
        private val infos: List<Badge>
    ) : RecyclerView.Adapter<BadgeInfoAdapter.BadgeInfoViewHolder>() {

        inner class BadgeInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val badgeImage: ImageView = itemView.findViewById(R.id.pagerBadgeImage)
            private val badgeName: TextView = itemView.findViewById(R.id.pagerBadgeName)
            private val badgeDescription: TextView =
                itemView.findViewById(R.id.pagerBadgeDescription)


            fun bind(badge: Badge, position: Int) {
                when (position) {
                    0 -> {
                        badgeImage.setImageResource(R.drawable.ic_baseline_insert_emoticon_24)
                    }
                    1 -> {
                        badgeImage.setImageResource(R.drawable.ic_baseline_insert_emoticon_24)
                    }
                    2 -> {
                        badgeImage.setImageResource(R.drawable.ic_baseline_insert_emoticon_24)
                    }
                    3 -> {
                        badgeImage.setImageResource(R.drawable.ic_baseline_insert_emoticon_24)
                    }
                    4 -> {
                        badgeImage.setImageResource(R.drawable.ic_baseline_insert_emoticon_24)
                    }
                    5 -> {
                        badgeImage.setImageResource(R.drawable.ic_baseline_insert_emoticon_24)
                    }
                }

                badgeName.text = badge.name
                badgeDescription.text = badge.description

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeInfoViewHolder {
            return BadgeInfoViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.badge_info_viewpager, parent, false)
            )
        }

        override fun onBindViewHolder(holder: BadgeInfoViewHolder, position: Int) {
            holder.bind(infos[position], position)
        }

        override fun getItemCount(): Int {
            return infos.size
        }

    }
}