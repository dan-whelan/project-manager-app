package com.learning.projectmanager.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learning.projemanag.databinding.ItemLabelColourBinding

/*
    Recycler Adapter for Colours that are displayed by Colour Dialog in card details screen
    When user is selecting a label colour
 */
open class ColourLabelAdapter(
    private val context: Context,
    private val list: ArrayList<String>,
    private val mSelectedColour: String
): RecyclerView.Adapter<ColourLabelAdapter.ViewHolder>() {
    var onClickListener: OnClickListener? = null

    inner class ViewHolder(binding: ItemLabelColourBinding): RecyclerView.ViewHolder(binding.root) {
        val mainView = binding.main
        val selectBtn = binding.selectedColour
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLabelColourBinding.inflate(
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
        val item = list[position]
        Log.i(context.javaClass.simpleName, item)
        holder.mainView.setBackgroundColor(Color.parseColor(item))
        if(item == mSelectedColour) {
            holder.selectBtn.visibility = View.VISIBLE
        } else {
            holder.selectBtn.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if(onClickListener != null) {
                onClickListener!!.onClick(position, item)
            }
        }
    }

    interface OnClickListener {
        fun onClick(position: Int, colour: String)
    }
}