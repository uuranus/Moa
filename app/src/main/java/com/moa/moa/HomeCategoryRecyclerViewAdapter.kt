package com.moa.moa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HomeCategoryRecyclerViewAdapter(val categories:List<TodayWorkCategory>) :RecyclerView.Adapter<HomeCategoryRecyclerViewAdapter.HomeCategoryViewHolder>(){
    class HomeCategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val categoryID=itemView.findViewById<TextView>(R.id.categoryID)
        val categoryName=itemView.findViewById<TextView>(R.id.homeListCategory)
        val workList=itemView.findViewById<RecyclerView>(R.id.homeWorkList)

        fun bind(todaywork:TodayWork,position:Int){


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoryViewHolder {
        return HomeCategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.home_recyclerview_category,parent,false))
    }

    override fun onBindViewHolder(holder: HomeCategoryViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return categories.size
    }

}