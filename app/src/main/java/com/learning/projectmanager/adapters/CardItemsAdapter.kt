package com.learning.projectmanager.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.learning.projectmanager.activities.TaskListActivity
import com.learning.projemanag.databinding.ItemCardBinding
import com.learning.projectmanager.models.CardModel
import com.learning.projectmanager.models.SelectedMemberModel

open class CardItemsAdapter(
    private val context: Context,
    private val list: ArrayList<CardModel>
): RecyclerView.Adapter<CardItemsAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    inner class ViewHolder(binding: ItemCardBinding): RecyclerView.ViewHolder(binding.root) {
        val label = binding.viewLabel
        val cardName = binding.cardName
        val cardMembers = binding.assignedMembers
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

        if((context as TaskListActivity).mAssignedMemberList.size > 0) {
            val assignedMembers: ArrayList<SelectedMemberModel> = ArrayList()

            context.mAssignedMemberList.indices.forEach { idx ->
                model.assignedTo.forEach { memberId ->
                    if(context.mAssignedMemberList[idx].id == memberId) {
                        val selectedMember = SelectedMemberModel(
                            context.mAssignedMemberList[idx].id,
                            context.mAssignedMemberList[idx].image
                        )
                        assignedMembers.add(selectedMember)
                    }
                }
            }

            if(assignedMembers.size > 0) {
                if(assignedMembers.size == 1
                    && assignedMembers[0].id == model.createdBy) {
                    holder.cardMembers.visibility = View.GONE
                } else {
                    holder.cardMembers.visibility = View.VISIBLE

                    holder.cardMembers.layoutManager = GridLayoutManager(context, 4)
                    val adapter = AssignedMembersAdapter(context, assignedMembers, false)
                    holder.cardMembers.adapter = adapter
                    adapter.onClickListener =
                        object : AssignedMembersAdapter.OnClickListener {
                            override fun onClick() {
                                if(onClickListener != null) {
                                    onClickListener!!.onClick(position)
                                }
                            }
                        }
                }
            } else {
                holder.cardMembers.visibility = View.GONE
            }
        }

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