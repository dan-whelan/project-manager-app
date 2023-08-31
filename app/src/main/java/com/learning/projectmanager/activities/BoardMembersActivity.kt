package com.learning.projectmanager.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.learning.projemanag.R
import com.learning.projectmanager.adapters.MembersAdapter
import com.learning.projemanag.databinding.ActivityBoardMembersBinding
import com.learning.projemanag.databinding.DialogAddNewMembersBinding
import com.learning.projectmanager.firebase.FirestoreClass
import com.learning.projectmanager.models.BoardModel
import com.learning.projectmanager.models.UserModel
import com.learning.projectmanager.utils.Constants

class BoardMembersActivity : BaseActivity() {
    private lateinit var binding: ActivityBoardMembersBinding
    private lateinit var db: FirestoreClass

    private lateinit var mBoardDetails: BoardModel
    private lateinit var mAssignedMembersList: ArrayList<UserModel>
    private var mChangesMade: Boolean = false
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
            this.onBackPressed()
        }
    }

    override fun onBackPressed() {
        if(mChangesMade) {
            setResult(Activity.RESULT_OK)
        }
        onBackPressedDispatcher.onBackPressed()
    }

    fun populateMembersListToUI(membersList: ArrayList<UserModel>) {
        mAssignedMembersList = membersList

        hideProgressDialog()
        val adapter = MembersAdapter(this, membersList)
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
                db.getMemberDetails(this, email)
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

    fun memberDetails(user: UserModel) {
        mBoardDetails.assignedTo.add(user.id)
        db.assignMemberToBoard(this, mBoardDetails, user)
    }

    fun memberAssignSuccessCall(member: UserModel) {
        hideProgressDialog()
        mAssignedMembersList.add(member)

        mChangesMade = true
        populateMembersListToUI(mAssignedMembersList)
    }
}