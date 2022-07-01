package com.moa.moa.Home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.moa.moa.Data.HomeSecondSection
import com.moa.moa.Data.HomeThirdSection
import com.moa.moa.HomeSecondSectionRecyclerViewAdapter
import com.moa.moa.R

class HomeThirdSectionRecyclerViewAdapter(val list:List<HomeThirdSection>): RecyclerView.Adapter<HomeThirdSectionRecyclerViewAdapter.HomeViewHolder>() {

    class HomeViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val workId:TextView=itemView.findViewById(R.id.workdID)
        val isChecked: CheckBox =itemView.findViewById(R.id.workIsChecked)
        val workName:TextView=itemView.findViewById(R.id.workTitle)

        fun bind(homeThirdSection: HomeThirdSection){
            workId.text=homeThirdSection.workId.toString()
            workName.text=homeThirdSection.workNmae
            isChecked.isChecked=homeThirdSection.isChecked
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeViewHolder {
        return HomeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.home_recyclerview_thirdsection,parent,false))
    }

    override fun onBindViewHolder(
        holder: HomeViewHolder,
        position: Int
    ) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}