package com.learning.projemanag.activities

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.learning.projemanag.R
import com.learning.projemanag.adapters.BoardMembersAdapter
import com.learning.projemanag.databinding.ActivityBoardMembersBinding
import com.learning.projemanag.databinding.DialogAddNewMembersBinding
import com.learning.projemanag.firebase.FirestoreClass
import com.learning.projemanag.models.BoardModel
import com.learning.projemanag.models.UserModel
import com.learning.projemanag.utils.Constants

class BoardMembersActivity : BaseActivity() {
    private lateinit var binding: ActivityBoardMembersBinding
    private lateinit var db: FirestoreClass

    private lateinit var mBoardDetails: BoardModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardMembersBinding.inflate(layoutInflater)
        db = FirestoreClass()
        setContentView(binding.root)
        if (intent.hasExtra(Constants.BOARD_DETAIL))
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!

        setupActionBar()

        showCustomProgressDialog()
        db.getAssignedMembersListDetails(this, mBoardDetails.assignedTo)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_member_to_board, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_add_member -> {
                searchMember()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.membersToolbar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow_white)
            supportActionBar?.title = resources.getString(R.string.members_txt)
        }
        binding.membersToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun populateMembersListToUI(membersList: ArrayList<UserModel>) {
        hideProgressDialog()
        val adapter = BoardMembersAdapter(this, membersList)
        binding.membersList.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapter
            it.setHasFixedSize(true)
        }
    }

    private fun searchMember() {
        val dialog = Dialog(this)
        val dialogBinding = DialogAddNewMembersBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.addMemberToProject.setOnClickListener {
            val email = dialogBinding.searchEmail.text.toString()
            if(email.isNotEmpty()) {
                dialog.dismiss()
                //TODO
            } else {
                Toast.makeText(
                    this,
                    "Please Enter a valid email address", Toast.LENGTH_SHORT
                ).show()
            }
        }
        dialogBinding.cancelAction.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}