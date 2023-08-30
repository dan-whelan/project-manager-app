package com.learning.projectmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.learning.projemanag.R
import com.learning.projemanag.databinding.ItemMemberBinding
import com.learning.projectmanager.models.BoardModel
import com.learning.projectmanager.models.UserModel

open class BoardMembersAdapter(
    val context: Context,
    val list: ArrayList<UserModel>
): RecyclerView.Adapter<BoardMembersAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    inner class ViewHolder(binding: ItemMemberBinding): RecyclerView.ViewHolder(binding.root) {
        val memberImage = binding.profileImg
        val memberName = binding.memberName
        val memberEmail = binding.memberEmail
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMemberBinding.inflate(
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

        Glide
            .with(context)
            .load(model.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(holder.memberImage)

        holder.memberName.text = model.name
        holder.memberEmail.text = model.email
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener{
        fun onClick(position: Int, model: BoardModel)
    }

}