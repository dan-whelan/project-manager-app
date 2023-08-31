package com.learning.projectmanager.activities

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.learning.projectmanager.dialogs.AssignedMemberDialog
import com.learning.projectmanager.dialogs.ColourLabelDialog
import com.learning.projectmanager.firebase.FirestoreClass
import com.learning.projectmanager.models.BoardModel
import com.learning.projectmanager.models.CardModel
import com.learning.projectmanager.models.TaskModel
import com.learning.projectmanager.models.UserModel
import com.learning.projectmanager.utils.Constants
import com.learning.projemanag.R
import com.learning.projemanag.databinding.ActivityCardDetailsBinding

class CardDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityCardDetailsBinding
    private lateinit var db: FirestoreClass

    private lateinit var mBoardDetails: BoardModel
    private lateinit var mMembersDetails: ArrayList<UserModel>
    private var mTaskPosition = -1
    private var mCardPosition = -1
    private var mSelectedColour = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        db = FirestoreClass()
        setContentView(binding.root)
        getIntentData()
        setupActionBar()

        binding.editCardNameTxt.setText(mBoardDetails.taskList[mTaskPosition].cardList[mCardPosition].title)
        binding.editCardNameTxt.setSelection(binding.editCardNameTxt.text!!.length)
        mSelectedColour = mBoardDetails.taskList[mTaskPosition].cardList[mCardPosition].cardColour
        if(mSelectedColour.isNotEmpty())
            binding.selectLabelColour.let {
                it.setBackgroundColor(Color.parseColor(mSelectedColour))
                it.text = ""
            }


        binding.selectLabelColour.setOnClickListener {
            colourLabelDialog()
        }

        binding.selectMembers.setOnClickListener {
            assignedMembersDialog()
        }

        binding.updateBtn.setOnClickListener {
            if(binding.editCardNameTxt.text.toString().isNotEmpty())
                updateCardDetails()
            else
                Toast.makeText(
                    this,
                    "Please Enter a Card Name",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_delete_card -> {
                Constants.dialogForDelete(
                    this@CardDetailsActivity,
                    mCardPosition,
                    supportActionBar?.title.toString()
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.cardDetailsToolbar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow_white)
            supportActionBar?.title = mBoardDetails.taskList[mTaskPosition].cardList[mCardPosition].title
        }
        binding.cardDetailsToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    @Suppress("Deprecation")
    private fun getIntentData() {
        if(intent.hasExtra(Constants.BOARD_DETAIL))
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        if(intent.hasExtra(Constants.TASK_POSITION))
            mTaskPosition = intent.getIntExtra(Constants.TASK_POSITION, -1)
        if(intent.hasExtra(Constants.CARD_POSITION))
            mCardPosition = intent.getIntExtra(Constants.CARD_POSITION, -1)
        if(intent.hasExtra(Constants.BOARD_MEMBERS))
            mMembersDetails = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS)!!
    }

    fun addUpdateCardListSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateCardDetails() {
        val card = CardModel(
            binding.editCardNameTxt.text.toString(),
            mBoardDetails.taskList[mTaskPosition].cardList[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskPosition].cardList[mCardPosition].assignedTo,
            mSelectedColour
        )

        mBoardDetails.taskList[mTaskPosition].cardList[mCardPosition] = card
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        showCustomProgressDialog()
        db.addUpdateList(this@CardDetailsActivity, mBoardDetails)
    }

    fun deleteCard() {
        val cardList: ArrayList<CardModel> = mBoardDetails.taskList[mTaskPosition].cardList
        cardList.removeAt(mCardPosition)

        val taskList: ArrayList<TaskModel> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        taskList[mTaskPosition].cardList = cardList
        showCustomProgressDialog()
        db.addUpdateList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun coloursList(): ArrayList<String> {
        val coloursList = ArrayList<String>()
        coloursList.add(resources.getString(R.string.Red))
        coloursList.add(resources.getString(R.string.Blue))
        coloursList.add(resources.getString(R.string.Green))
        coloursList.add(resources.getString(R.string.Dark_Grey))
        coloursList.add(resources.getString(R.string.Orange))
        coloursList.add(resources.getString(R.string.Dark_Red))
        coloursList.add(resources.getString(R.string.Dark_Blue))
        return coloursList
    }

    private fun setColour() {
        binding.selectLabelColour.text = ""
        binding.selectLabelColour.setBackgroundColor(Color.parseColor(mSelectedColour))
    }

    private fun colourLabelDialog() {
        val coloursList = coloursList()
        val listDialog = object: ColourLabelDialog(
            this,
            coloursList,
            mSelectedColour,
            resources.getString(R.string.select_colour_txt),
        ) {
            override fun onItemSelected(colour: String) {
                mSelectedColour = colour
                setColour()
            }
        }

        listDialog.show()
    }

    private fun assignedMembersDialog() {
        var cardAssignedMembers = mBoardDetails.taskList[mTaskPosition].cardList[mCardPosition].assignedTo
        if(cardAssignedMembers.size > 0) {
            mMembersDetails.indices.forEach { idx ->
                cardAssignedMembers.forEach { member ->
                    if(mMembersDetails[idx].id == member) {
                        mMembersDetails[idx].selected = true
                    }
                }
            }
        } else {
            mMembersDetails.indices.forEach { idx ->
                cardAssignedMembers.forEach { member ->
                    if(mMembersDetails[idx].id == member) {
                        mMembersDetails[idx].selected = false
                    }
                }
            }
        }

        val listDialog = object: AssignedMemberDialog(
            this,
            mMembersDetails,
            resources.getString(R.string.select_members_txt)
        ){
            override fun onItemSelected(member: UserModel, action: String) {
                //TODO
            }
        }

        listDialog.show()
    }
}