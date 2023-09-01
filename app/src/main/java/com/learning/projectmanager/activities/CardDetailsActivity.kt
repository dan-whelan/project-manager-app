package com.learning.projectmanager.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.learning.projectmanager.adapters.AssignedMembersAdapter
import com.learning.projectmanager.dialogs.AssignedMemberDialog
import com.learning.projectmanager.dialogs.ColourLabelDialog
import com.learning.projectmanager.firebase.FirestoreClass
import com.learning.projectmanager.models.BoardModel
import com.learning.projectmanager.models.CardModel
import com.learning.projectmanager.models.SelectedMemberModel
import com.learning.projectmanager.models.TaskModel
import com.learning.projectmanager.models.UserModel
import com.learning.projectmanager.utils.Constants
import com.learning.projemanag.R
import com.learning.projemanag.databinding.ActivityCardDetailsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CardDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityCardDetailsBinding
    private lateinit var db: FirestoreClass

    private lateinit var mBoardDetails: BoardModel
    private lateinit var mMembersDetails: ArrayList<UserModel>
    private var mTaskPosition = -1
    private var mCardPosition = -1
    private var mSelectedColour = ""
    private var mDueDateInMS: Long = 0

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
        mDueDateInMS = mBoardDetails.taskList[mTaskPosition].cardList[mCardPosition].dueDate
        if(mDueDateInMS > 0) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val date = sdf.format(Date(mDueDateInMS))
            binding.selectDueDate.text = date
        }

        populateAssignedMembersToUI()
        binding.selectLabelColour.setOnClickListener {
            colourLabelDialog()
        }

        binding.selectMembers.setOnClickListener {
            assignedMembersDialog()
        }

        binding.selectDueDate.setOnClickListener {
            clickDatePicker()
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

    /*
        Updates the local board model with card changes in order to update the board card mnodel in the DB
     */
    private fun updateCardDetails() {
        val card = CardModel(
            binding.editCardNameTxt.text.toString(),
            mBoardDetails.taskList[mTaskPosition].cardList[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskPosition].cardList[mCardPosition].assignedTo,
            mSelectedColour,
            mDueDateInMS
        )

        mBoardDetails.taskList[mTaskPosition].cardList[mCardPosition] = card
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        showCustomProgressDialog()
        db.addUpdateList(this@CardDetailsActivity, mBoardDetails)
    }

    fun addUpdateCardListSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    /*
        Deletes a card from the local board model that has been deleted by user and calls for DB update
     */
    fun deleteCard() {
        val cardList: ArrayList<CardModel> = mBoardDetails.taskList[mTaskPosition].cardList
        cardList.removeAt(mCardPosition)

        val taskList: ArrayList<TaskModel> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)

        taskList[mTaskPosition].cardList = cardList
        showCustomProgressDialog()
        db.addUpdateList(this@CardDetailsActivity, mBoardDetails)
    }

    /*
        List of card label colours that can be selected by the user
     */
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

    /*
        Sets the colour of the label_colour property in the UI
     */
    private fun setColour() {
        binding.selectLabelColour.text = ""
        binding.selectLabelColour.setBackgroundColor(Color.parseColor(mSelectedColour))
    }

    /*
        Dialog Displayed when user is selecting a label colour
     */
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

    /*
        Dialog Displayed when the user is assigning members to the board
     */
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
                if(action == Constants.SELECT) {
                    if(!mBoardDetails
                        .taskList[mTaskPosition]
                        .cardList[mCardPosition]
                        .assignedTo
                        .contains(member.id)) {
                        mBoardDetails
                            .taskList[mTaskPosition]
                            .cardList[mCardPosition]
                            .assignedTo.add(member.id)
                    }
                } else {
                    mBoardDetails
                        .taskList[mTaskPosition]
                        .cardList[mCardPosition]
                        .assignedTo.remove(member.id)

                    mMembersDetails.indices.forEach { idx ->
                        if(mMembersDetails[idx].id == member.id) {
                            mMembersDetails[idx].selected = false
                        }
                    }
                }

                populateAssignedMembersToUI()
            }
        }
        listDialog.show()
    }

    /*
        Populates the assigned card members to the UI
     */
    private fun populateAssignedMembersToUI() {
        val assignedMembersList = mBoardDetails.taskList[mTaskPosition].cardList[mCardPosition].assignedTo

        val selectedMembersList: ArrayList<SelectedMemberModel> = ArrayList()
        mMembersDetails.indices.forEach { idx ->
            assignedMembersList.forEach { member ->
                if(mMembersDetails[idx].id == member) {
                    val selectedMember = SelectedMemberModel(
                        mMembersDetails[idx].id,
                        mMembersDetails[idx].image
                    )
                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if(selectedMembersList.size > 0) {
            selectedMembersList.add(SelectedMemberModel("", ""))
            binding.selectMembers.visibility = View.GONE
            binding.assignedMembers.let {
                it.visibility = View.VISIBLE
                it.layoutManager = GridLayoutManager(this, 6)

                val adapter = AssignedMembersAdapter(this, selectedMembersList, true)
                it.adapter = adapter
                adapter.onClickListener =
                    object : AssignedMembersAdapter.OnClickListener {
                        override fun onClick() {
                            assignedMembersDialog()
                        }
                    }
            }
        } else {
            binding.selectMembers.visibility = View.VISIBLE
            binding.assignedMembers.visibility = View.GONE
        }
    }

    /*
        DatePickerDialog displayed when user is choosing the cards due date
     */
    private fun clickDatePicker() {
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)
        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = "$selectedDayOfMonth/${selectedMonth+1}/$selectedYear"
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                val theDate = sdf.parse(selectedDate)!!
                mDueDateInMS = theDate.time
                binding.selectDueDate.text = sdf.format(theDate)
            },
            year,
            month,
            day
        )

        datePicker.show()
    }
}