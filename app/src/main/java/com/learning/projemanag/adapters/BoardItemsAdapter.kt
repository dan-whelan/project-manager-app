package com.learning.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.learning.projemanag.R
import com.learning.projemanag.databinding.ItemBoardBinding
import com.learning.projemanag.models.BoardModel

open class BoardItemsAdapter(
    private val context: Context,
    private val list: ArrayList<BoardModel>
    ): RecyclerView.Adapter<BoardItemsAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    inner class ViewHolder(binding: ItemBoardBinding): RecyclerView.ViewHolder(binding.root) {
        val boardItemImage = binding.boardImg
        val boardName = binding.boardName
        val boardCreator = binding.boardCreator
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBoardBinding.inflate(
                LayoutInflater.from(parent.context),
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
        Glide
            .with(context)
            .load(model.image)
            .centerCrop()
            .placeholder(R.drawable.grey_bg)
            .into(holder.boardItemImage)

        holder.boardName.text = model.name
        holder.boardCreator.text = "Created By: ${model.createdBy}"

        holder.itemView.setOnClickListener {
            if(onClickListener != null) {
                onClickListener!!.onClick(position, model)
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, model: BoardModel)
    }
}