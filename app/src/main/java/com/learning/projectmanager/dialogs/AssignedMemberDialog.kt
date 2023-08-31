package com.learning.projectmanager.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.learning.projectmanager.adapters.MembersAdapter
import com.learning.projectmanager.adapters.ColourLabelAdapter
import com.learning.projectmanager.models.UserModel
import com.learning.projemanag.databinding.ListDialogBinding

abstract class AssignedMemberDialog(
    context: Context,
    private var list: ArrayList<UserModel>,
    private var title: String = ""
): Dialog(context) {
    private var adapter: MembersAdapter? = null
    private lateinit var binding: ListDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ListDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.titleText.text = title
        binding.recycler.layoutManager = LinearLayoutManager(context)
        adapter = MembersAdapter(context, list)
        binding.recycler.adapter = adapter
        adapter?.onClickListener =
            object : MembersAdapter.OnClickListener {
                override fun onClick(position: Int, user: UserModel, action: String) {
                    dismiss()
                    onItemSelected(user, action)
                }
            }

    }

    protected abstract fun onItemSelected(member: UserModel, action: String)
}