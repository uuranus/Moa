package com.moa.moa.Home

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moa.moa.Data.HomeNotYetSecondSection
import com.moa.moa.Data.HomeNotYetSection
import com.moa.moa.Data.HomeThirdSection
import com.moa.moa.HomeSecondSectionRecyclerViewAdapter
import com.moa.moa.R

class HomeNotYetRecyclerViewAdapter (val list:List<HomeNotYetSection>): RecyclerView.Adapter<HomeNotYetRecyclerViewAdapter.HomeViewHolder>(){

    class HomeViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val homeListCategory=itemView.findViewById<TextView>(R.id.homeListCategory)
        val homeNotYetFirstRecyclerView=itemView.findViewById<RecyclerView>(R.id.homeNotYetFirstRecyclerView)

        fun bind(homeNotYetSection: HomeNotYetSection){
            homeListCategory.text=homeNotYetSection.title
            homeNotYetFirstRecyclerView.adapter=HomeNotYetSecondRecyclerViewAdapter(homeNotYetSection.list)
            homeNotYetFirstRecyclerView.layoutManager=LinearLayoutManager(itemView.context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeNotYetRecyclerViewAdapter.HomeViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.home_notyet_recyclerview_firstsection, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}