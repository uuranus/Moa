package com.moa.moa.Home

import android.content.Context
import android.text.Layout
import android.util.Log
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
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import okhttp3.internal.notify
import kotlin.properties.Delegates

class HomeNotYetRecyclerViewAdapter (val list:List<HomeNotYetSection>): RecyclerView.Adapter<HomeNotYetRecyclerViewAdapter.HomeViewHolder>(){

    var onItemClickListener : ((Int)->Unit)? = null

    class HomeViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        var itemNumber = -1
        val homeListCategory=itemView.findViewById<TextView>(R.id.homeListCategory)
        val homeNotYetFirstRecyclerView=itemView.findViewById<RecyclerView>(R.id.homeNotYetFirstRecyclerView)

        fun bind(homeNotYetSection: HomeNotYetSection){
            homeListCategory.text=homeNotYetSection.title

            Log.i("home adapater", homeNotYetSection.toString())

            val secondSection = HomeNotYetSecondRecyclerViewAdapter(homeNotYetSection.list)
            secondSection.onItemClickListener = {
                Log.i("home adapter","$it clicked!")

                itemNumber = it
            }
            homeNotYetFirstRecyclerView.adapter=secondSection
            homeNotYetFirstRecyclerView.layoutManager=LinearLayoutManager(itemView.context)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val item = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_notyet_recyclerview_firstsection, parent, false)

        val listViewHolder = HomeViewHolder(item)

        item.setOnClickListener {
            onItemClickListener?.invoke(listViewHolder.itemNumber)
        }

        return listViewHolder
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}