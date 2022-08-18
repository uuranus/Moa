package com.moa.moa.MyPage

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.moa.moa.Local.Complement
import com.moa.moa.R

class ComplementRecyclerViewAdapter(
    var complementList: List<Complement>,
) : RecyclerView.Adapter<ComplementRecyclerViewAdapter.ComplementViewHolder>() {

    interface ComplementInterface{
        fun complementAdd(isEdit:Boolean,complement: Complement?)
        fun complementUse(uid:String)
        fun complementDelete(uid:String)
    }

    var complementInterface:ComplementInterface?=null

    inner class ComplementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val complementStar = itemView.findViewById<TextView>(R.id.complementStar)
        private val complementTitle = itemView.findViewById<TextView>(R.id.complementTitle)
        private val complementDescription =
            itemView.findViewById<TextView>(R.id.complementDescription)
        private val complementMore = itemView.findViewById<ImageView>(R.id.complementMore)
        private val complementPlus = itemView.findViewById<ImageView>(R.id.complementPlus)

        fun bind(complement: Complement?, position: Int) {
            if (position == complementList.size) {
                complementPlus.isVisible = true
                complementStar.isVisible = false
                complementTitle.isVisible = false
                complementDescription.isVisible = false
                complementMore.isVisible = false

                complementPlus.setOnClickListener {
                    complementInterface?.complementAdd(false,null)
                }

            } else {
                complementPlus.isVisible = false
                complementStar.isVisible = true
                complementTitle.isVisible = true
                complementDescription.isVisible = true
                complementMore.isVisible = true

                complementStar.text = "${complement?.star}"
                complementTitle.text = complement?.title
                complementDescription.text = complement?.description

                complementMore.setOnClickListener {
                    val view= LayoutInflater.from(itemView.context)
                        .inflate(R.layout.complemt_more, null)

                    val edit=view.findViewById<TextView>(R.id.complementEdit)
                    val delete=view.findViewById<TextView>(R.id.complementDelete)
                    val use=view.findViewById<TextView>(R.id.complementUse)

                    edit.setOnClickListener {
                        complementInterface?.complementAdd(true,complement)
                    }
                    delete.setOnClickListener {
                        complementInterface?.complementDelete(complement?.uid!!)
                    }

                    use.setOnClickListener {
                        complementInterface?.complementUse(complement?.uid!!)
                    }

                    AlertDialog.Builder(itemView.context)
                        .setView(view)
                        .create()
                        .show()

                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplementViewHolder {
        return ComplementViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.complement_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ComplementViewHolder, position: Int) {
        Log.i("bind",position.toString())
        if (position == complementList.size) holder.bind(null, position)
        else holder.bind(complementList[position], position)
    }

    override fun getItemCount(): Int {
        return complementList.size + 1
    }
}
