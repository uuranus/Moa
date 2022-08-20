package com.moa.moa.Work

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.moa.moa.Local.TitleHistory
import com.moa.moa.databinding.TitleHistoryBinding

class TitleHistoryAdapter (val workTitleListener:(String)->Unit) : ListAdapter<TitleHistory, TitleHistoryAdapter.HistoryViewHolder>(diffUtil) {

    inner class HistoryViewHolder(private val binding: TitleHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(title: TitleHistory) {
            binding.historyKeywordTextView.text = title.title
            binding.historyKeywordDeleteButton.setOnClickListener {
                workTitleListener(title.title!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(
            TitleHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<TitleHistory>() {
            override fun areItemsTheSame(oldItem: TitleHistory, newItem: TitleHistory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: TitleHistory, newItem: TitleHistory): Boolean {
                return oldItem.uid == newItem.uid
            }

        }
    }
}
