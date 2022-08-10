package com.moa.moa.Main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.moa.moa.R
import com.moa.moa.databinding.AnalysisRowBinding

class ChartAdapter(private val items: ArrayList<ChartData>) :
    RecyclerView.Adapter<ChartAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(data: ChartData)
    }

    var itemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(val binding: AnalysisRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {    //root 말고 바인딩된 아이디 사용할 것~
                itemClickListener?.onItemClick(items[adapterPosition])//adapterposition 또 넘겨줄 수도 있음
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // binding 객체 만들고 inflate -> MyViewHolder return
        val binding = AnalysisRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //한 줄마다 data에 따라 보여주는 방법 정의. holder.binding. 으로  id를 이용 가능
        holder.binding.apply {
            //items[position]으로 접근 가능
            holder.binding.name.text = items[position].name
            holder.binding.proportion.data = listOf(items[position].stars)
            holder.binding.proportion.colors = listOf(Color.Black, Color.Red, Color.Green, Color.Blue)
            holder.binding.proportion.strokeWidth = 30f
            holder.binding.proportion.modifier = Modifier.fillMaxSize()
            holder.binding.proportion.animate = true

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

