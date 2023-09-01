package com.learning.projectmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.learning.projectmanager.models.BoardModel
import com.learning.projectmanager.models.SelectedMemberModel
import com.learning.projemanag.R
import com.learning.projemanag.databinding.AssignedMembersBinding

/*
    Recycler Adapter for Members assigned to cards, displays user profile images
    of those that have been assigned to the card
 */
open class AssignedMembersAdapter(
    val context: Context,
    val list: ArrayList<SelectedMemberModel>,
    private val assignMembers: Boolean
): RecyclerView.Adapter<AssignedMembersAdapter.ViewHolder>() {
    var onClickListener: OnClickListener? = null

    inner class ViewHolder(binding: AssignedMembersBinding): RecyclerView.ViewHolder(binding.root) {
        val addAssignee = binding.addAssignee
        val memberProfileImg = binding.memberProfileImg
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AssignedMembersBinding.inflate(
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
        if(position == list.size - 1 && assignMembers) {
            holder.addAssignee.visibility = View.VISIBLE
            holder.memberProfileImg.visibility = View.GONE
        } else {
            holder.addAssignee.visibility = View.GONE
            holder.memberProfileImg.visibility = View.VISIBLE

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.memberProfileImg)
        }

        holder.itemView.setOnClickListener {
            if(onClickListener != null)
                onClickListener!!.onClick()
        }
    }

    interface OnClickListener{
        fun onClick()
    }
}