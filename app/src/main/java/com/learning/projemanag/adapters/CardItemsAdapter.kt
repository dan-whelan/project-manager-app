package com.learning.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.learning.projemanag.databinding.ItemCardBinding
import com.learning.projemanag.models.BoardModel
import com.learning.projemanag.models.CardModel

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

    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, model: BoardModel)
    }
}