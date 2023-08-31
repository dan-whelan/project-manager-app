package com.learning.projectmanager.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.learning.projectmanager.adapters.ColourLabelAdapter
import com.learning.projemanag.databinding.ListDialogBinding

abstract class ColourLabelDialog(
    context: Context,
    private var list: ArrayList<String>,
    private val mSelectedColour: String = "",
    private var title: String = ""
): Dialog(context) {
    private var adapter: ColourLabelAdapter? = null
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
        adapter = ColourLabelAdapter(context, list, mSelectedColour)
        binding.recycler.adapter = adapter
        adapter?.onClickListener =
            object : ColourLabelAdapter.OnClickListener {
                override fun onClick(position: Int, colour: String) {
                    dismiss()
                    onItemSelected(colour)
                }

            }

    }

    protected abstract fun onItemSelected(colour: String)
}