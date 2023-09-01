package com.learning.projectmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.learning.projemanag.R
import com.learning.projemanag.databinding.ItemMemberBinding
import com.learning.projectmanager.models.UserModel
import com.learning.projectmanager.utils.Constants

/*
    Recycler Adapter for Members that are displayed by Member Dialog in card details screen
    When user is selecting a member to assign
 */
open class MembersAdapter(
    val context: Context,
    val list: ArrayList<UserModel>
): RecyclerView.Adapter<MembersAdapter.ViewHolder>() {
    var onClickListener: OnClickListener? = null

    inner class ViewHolder(binding: ItemMemberBinding): RecyclerView.ViewHolder(binding.root) {
        val memberImage = binding.profileImg
        val memberName = binding.memberName
        val memberEmail = binding.memberEmail
        val selectedMember = binding.selectedMember
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

        if(model.selected)
            holder.selectedMember.visibility = View.VISIBLE
        else
            holder.selectedMember.visibility = View.GONE

        holder.itemView.setOnClickListener {
            if(onClickListener != null)
                if(model.selected)
                    onClickListener!!.onClick(position, model, Constants.UNSELECT)
                else
                    onClickListener!!.onClick(position, model, Constants.SELECT)
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, user: UserModel, action: String)
    }

}