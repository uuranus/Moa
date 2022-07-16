package com.moa.moa.Work

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moa.moa.Data.Work
import com.moa.moa.databinding.WorkListBinding

class WorkAdapter(val workEditClickedListener:(Work)->Unit) : ListAdapter<Work, WorkAdapter.WorkListViewHolder>(diffUtil) {

    inner class WorkListViewHolder(private val binding: WorkListBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(work: Work){
            binding.workTitle.text=work.title
            binding.titleDeleteButton.tag=work
            binding.titleDeleteButton.setOnClickListener {
                workEditClickedListener(work)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkListViewHolder {
        return WorkListViewHolder(WorkListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: WorkListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val diffUtil=object: DiffUtil.ItemCallback<Work>(){
            override fun areItemsTheSame(oldItem: Work, newItem: Work): Boolean {
                return oldItem==newItem
            }

            override fun areContentsTheSame(oldItem: Work, newItem: Work): Boolean {
                return oldItem.workId==newItem.workId
            }

        }
    }
}