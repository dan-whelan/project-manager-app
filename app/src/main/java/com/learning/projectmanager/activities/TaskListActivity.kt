package com.learning.projectmanager.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.learning.projemanag.R
import com.learning.projectmanager.adapters.TaskItemsAdapter
import com.learning.projemanag.databinding.ActivityTaskListBinding
import com.learning.projectmanager.firebase.FirestoreClass
import com.learning.projectmanager.models.BoardModel
import com.learning.projectmanager.models.CardModel
import com.learning.projectmanager.models.TaskModel
import com.learning.projectmanager.models.UserModel
import com.learning.projectmanager.utils.Constants

class TaskListActivity : BaseActivity() {
    private lateinit var binding: ActivityTaskListBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirestoreClass

    private lateinit var boardDocumentId: String
    private lateinit var mBoardDetails: BoardModel
    private lateinit var mAssignedMemberList: ArrayList<UserModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        db = FirestoreClass()
        setContentView(binding.root)
        if(intent.hasExtra(Constants.DOCUMENT_ID)) {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
            Log.i(this.javaClass.simpleName, boardDocumentId)
        }
        showCustomProgressDialog()
        db.getBoardDetails(this, boardDocumentId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_members -> {
                val intent = Intent(this, BoardMembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                startMembersRequest.launch(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.taskToolbar)
        if(supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow_white)
            supportActionBar?.title = mBoardDetails.name
        }
        binding.taskToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun boardDetails(board: BoardModel) {
        mBoardDetails = board

        hideProgressDialog()
        setupActionBar()

        val addTaskList = TaskModel(resources.getString(R.string.add_list_txt))
        board.taskList.add(addTaskList)

        binding.taskList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.taskList.setHasFixedSize(true)

        val adapter = TaskItemsAdapter(this, board.taskList)
        binding.taskList.adapter = adapter

        showCustomProgressDialog()
        Log.i(this.javaClass.simpleName, mBoardDetails.assignedTo.toString())
        db.getAssignedMembersListDetails(this, mBoardDetails.assignedTo)
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()

        showCustomProgressDialog()

        db.getBoardDetails(this, boardDocumentId)
    }

    fun createTaskList(taskListName: String) {
        val task = TaskModel(taskListName, db.getCurrentUserId())
        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showCustomProgressDialog()
        db.addUpdateList(this, mBoardDetails)
    }

    fun updateTaskList(position: Int, listName: String, model: TaskModel) {
        val task = TaskModel(listName, model.createdBy)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        db.addUpdateList(this, mBoardDetails)
    }

    fun deleteTaskList(position: Int) {
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        db.addUpdateList(this, mBoardDetails)
    }

    fun createCardList(position: Int, cardName: String) {
        val assignedUsers: ArrayList<String> = ArrayList()
        assignedUsers.add(db.getCurrentUserId())
        val card = CardModel(cardName, db.getCurrentUserId(), assignedUsers)
        mBoardDetails.taskList[position].cardList.add(0, card)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        db.addUpdateList(this, mBoardDetails)
    }

    fun addCardToList(position: Int, cardName: String) {
        val assignedUsers: ArrayList<String> = ArrayList()
        assignedUsers.add(db.getCurrentUserId())
        val card = CardModel(cardName, db.getCurrentUserId(), assignedUsers)
        mBoardDetails.taskList[position].cardList.add(card)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        db.addUpdateList(this, mBoardDetails)
    }

    fun cardDetails(taskListPosition: Int, cardPosition: Int) {
        val intent = Intent(this, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
        intent.putExtra(Constants.TASK_POSITION, taskListPosition)
        intent.putExtra(Constants.CARD_POSITION, cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS, mAssignedMemberList)
        startCardDetailsRequest.launch(intent)
    }

    fun boardMembersDetails(list: ArrayList<UserModel>) {
        mAssignedMemberList = list
        hideProgressDialog()
    }

    private val startMembersRequest =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if(result.resultCode == Activity.RESULT_OK) {
                showCustomProgressDialog()
                db.getBoardDetails(this, boardDocumentId)
            } else {
                Log.i(this.javaClass.simpleName, "No Board Update Needed")
            }
        }

    private val startCardDetailsRequest =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if(result.resultCode == Activity.RESULT_OK) {
                showCustomProgressDialog()
                db.getBoardDetails(this, boardDocumentId)
            } else {
                Log.i(this.javaClass.simpleName, "No Card List Update Needed")
            }

        }
}