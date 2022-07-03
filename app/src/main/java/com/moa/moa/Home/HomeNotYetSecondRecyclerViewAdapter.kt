package com.moa.moa.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.moa.moa.Data.HomeNotYetSecondSection
import com.moa.moa.R

class HomeNotYetSecondRecyclerViewAdapter(val list:List<HomeNotYetSecondSection>): RecyclerView.Adapter<HomeNotYetSecondRecyclerViewAdapter.HomeViewHolder>() {

    class HomeViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val workdID=itemView.findViewById<TextView>(R.id.workdID)
        val workdColor=itemView.findViewById<View>(R.id.workdColor)
        val workTitle=itemView.findViewById<TextView>(R.id.workTitle)
        val workDetailButton=itemView.findViewById<ImageButton>(R.id.workDetailButton)

        fun bind(homeNotYetSecondsecion:HomeNotYetSecondSection){
            workdID.text=homeNotYetSecondsecion.workId
            workTitle.text=homeNotYetSecondsecion.title
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeNotYetSecondRecyclerViewAdapter.HomeViewHolder {
        return HomeNotYetSecondRecyclerViewAdapter.HomeViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.home_notyet_recyclerview_secondsection, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: HomeNotYetSecondRecyclerViewAdapter.HomeViewHolder,
        position: Int
    ) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}