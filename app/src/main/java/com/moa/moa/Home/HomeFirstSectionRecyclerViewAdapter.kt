package com.moa.moa.Home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moa.moa.Data.HomeFirstSection
import com.moa.moa.HomeSecondSectionRecyclerViewAdapter
import com.moa.moa.R

class HomeFirstSectionRecyclerViewAdapter(val list:List<HomeFirstSection>) :RecyclerView.Adapter<HomeFirstSectionRecyclerViewAdapter.HomeCategoryViewHolder>(){
    class HomeCategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val homeListCategory:TextView=itemView.findViewById(R.id.homeListCategory)
        private val homeFirstRecyclerView:RecyclerView=itemView.findViewById(R.id.homeFirstRecyclerView)

        @SuppressLint("ResourceAsColor")
        fun bind(firstsection:HomeFirstSection){
            android.util.Log.i("loggggg",firstsection.toString())
            homeListCategory.text=firstsection.title
            if(firstsection.title.equals("아직 배정되지 않았어요!")) homeListCategory.setTextColor(ContextCompat.getColor(itemView.context,R.color.textColorPrimary))
            homeFirstRecyclerView.adapter=HomeSecondSectionRecyclerViewAdapter(firstsection.list)
            homeFirstRecyclerView.layoutManager=LinearLayoutManager(itemView.context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoryViewHolder {
        return HomeCategoryViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.home_recyclerview_firstsection,parent,false))
    }

    override fun onBindViewHolder(holder: HomeCategoryViewHolder, position: Int) {
        val firstsection=list[position]

        holder.bind(firstsection)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}