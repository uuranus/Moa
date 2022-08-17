package com.moa.part3.BookReview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moa.moa.Local.AlarmHistory
import com.moa.moa.R

class AlarmListAdapter() : ListAdapter<AlarmHistory, AlarmListAdapter.AlarmViewHolder>(diffUtil) {

    inner class AlarmViewHolder(view: View): RecyclerView.ViewHolder(view){

        private val title=view.findViewById<TextView>(R.id.alarmTitle)
        private val date=view.findViewById<TextView>(R.id.alarmDate)
        fun bind(history: AlarmHistory){
            title.text=history.title
            date.text=history.date.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        return AlarmViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.alarm_list_row,parent,false))
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val diffUtil=object: DiffUtil.ItemCallback<AlarmHistory>(){
            override fun areItemsTheSame(oldItem: AlarmHistory, newItem: AlarmHistory): Boolean {
                return oldItem==newItem
            }

            override fun areContentsTheSame(oldItem: AlarmHistory, newItem: AlarmHistory): Boolean {
                return oldItem.uid==newItem.uid
            }

        }
    }
}