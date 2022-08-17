package com.moa.moa.MyPage

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.moa.moa.Local.Complement
import com.moa.moa.R

class ComplementUsedRecyclerViewAdapter (
    var complementList: List<Complement>,
) : RecyclerView.Adapter<ComplementUsedRecyclerViewAdapter.ComplementUsedViewHolder>() {


    inner class ComplementUsedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val complementStar = itemView.findViewById<TextView>(R.id.complementStar)
        private val complementTitle = itemView.findViewById<TextView>(R.id.complementTitle)
        private val complementDescription =
            itemView.findViewById<TextView>(R.id.complementDescription)

        fun bind(complement: Complement, position: Int) {
            complementStar.text="${complement.star}"
            complementTitle.text=complement.title
            complementDescription.text=complement.description
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplementUsedViewHolder {
        return ComplementUsedViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.complement_used_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ComplementUsedViewHolder, position: Int) {
        holder.bind(complementList[position], position)
    }

    override fun getItemCount(): Int {
        return complementList.size
    }
}
