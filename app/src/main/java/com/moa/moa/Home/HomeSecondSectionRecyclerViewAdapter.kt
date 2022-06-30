package com.moa.moa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moa.moa.Data.HomeSecondSection
import com.moa.moa.Data.HomeThirdSection
import com.moa.moa.Home.HomeThirdSectionRecyclerViewAdapter

class HomeSecondSectionRecyclerViewAdapter(val list:List<HomeSecondSection>):RecyclerView.Adapter<HomeSecondSectionRecyclerViewAdapter.HomeViewHolder>() {

    class HomeViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val homeSecondId:TextView=itemView.findViewById(R.id.homeSecondUserID)
        private val homeSecondText:TextView=itemView.findViewById(R.id.homeSecondRecyclerViewText)
        private val homeSecondImage:ImageButton=itemView.findViewById(R.id.homeSecondImageButton)
        private val homeSecondRecyclerView:RecyclerView=itemView.findViewById(R.id.homeSecondRecyclerView)

        fun bind(secondSection: HomeSecondSection){
            homeSecondId.text=secondSection.userId
            homeSecondText.text=secondSection.userName
            homeSecondImage.setOnClickListener {
                homeSecondRecyclerView.isGone=!homeSecondRecyclerView.isGone
            }
            homeSecondRecyclerView.adapter=HomeThirdSectionRecyclerViewAdapter(secondSection.list)
            homeSecondRecyclerView.layoutManager=LinearLayoutManager(itemView.context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.home_recyclerview_secondsection,parent,false))
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val secondSection=list[position]

        holder.bind(secondSection)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}