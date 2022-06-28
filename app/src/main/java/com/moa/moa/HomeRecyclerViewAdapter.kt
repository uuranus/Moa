package com.moa.moa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HomeWorkRecyclerViewAdapter(val workLists:List<TodayWork>):RecyclerView.Adapter<HomeWorkRecyclerViewAdapter.HomeViewHolder>() {

    class HomeViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val checkbox=itemView.findViewById<CheckBox>(R.id.checkbox)
        val workTitle=itemView.findViewById<TextView>(R.id.workTitle)

        fun bind(work:TodayWork){
            checkbox.isChecked=work.isChecked
            workTitle.text=work.workTitle
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.home_recyclerview_row,parent,false))
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(workLists[position])
    }

    override fun getItemCount(): Int {
        return workLists.size
    }

}