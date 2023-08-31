package com.learning.projectmanager.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learning.projemanag.databinding.ItemCardBinding
import com.learning.projectmanager.models.CardModel

open class CardItemsAdapter(
    private val context: Context,
    private val list: ArrayList<CardModel>
): RecyclerView.Adapter<CardItemsAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    inner class ViewHolder(binding: ItemCardBinding): RecyclerView.ViewHolder(binding.root) {
        val label = binding.viewLabel
        val cardName = binding.cardName
        val cardMembers = binding.cardMembers
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCardBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        holder.cardName.text = model.title

        if(model.cardColour.isNotEmpty())
            holder.label.let {
                it.visibility = View.VISIBLE
                it.setBackgroundColor(Color.parseColor(model.cardColour))
            }
        else holder.label.visibility = View.GONE

        holder.itemView.setOnClickListener {
            if(onClickListener != null) {
                onClickListener!!.onClick(position)
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int)
    }
}