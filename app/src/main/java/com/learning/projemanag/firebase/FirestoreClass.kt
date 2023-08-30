package com.learning.projemanag.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.learning.projemanag.activities.BoardMembersActivity
import com.learning.projemanag.activities.CreateBoardActivity
import com.learning.projemanag.activities.EditProfileActivity
import com.learning.projemanag.activities.MainActivity
import com.learning.projemanag.activities.SignInActivity
import com.learning.projemanag.activities.SignUpActivity
import com.learning.projemanag.activities.TaskListActivity
import com.learning.projemanag.models.BoardModel
import com.learning.projemanag.models.UserModel
import com.learning.projemanag.utils.Constants

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: UserModel) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "${e.message}")
            }
    }

    fun registerBoard(activity: CreateBoardActivity, boardInfo: BoardModel) {
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(boardInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "${e.message}")
            }
    }

    fun getBoardsList(activity: MainActivity) {
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val boardList: ArrayList<BoardModel> = ArrayList()
                document.documents.forEach {
                    var board = it.toObject(BoardModel::class.java)
                    board?.documentId = it.id
                    boardList.add(board!!)
                }
                activity.populateBoardsListToUI(boardList)
            }.addOnFailureListener { ex ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "ERROR in fetching Boards")
            }
    }

    fun updateUserProfileData(activity: EditProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i("DB", "Profile Data Updated Successfully")
                Toast.makeText(
                    activity,
                    "Profile Data Updated Successfully",
                    Toast.LENGTH_LONG
                ).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener { ex ->
                activity.hideProgressDialog()
                Log.e("ERROR", ex.message.toString())
                Toast.makeText(
                    activity,
                    "Error Updating Files, Please Try Again Later",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    fun getBoardDetails(activity: TaskListActivity, documentId: String) {
        Log.i(this.javaClass.simpleName, documentId)
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                val board = document.toObject(BoardModel::class.java)!!
                board.documentId = documentId
                activity.boardDetails(board)
            }.addOnFailureListener { ex ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "ERROR in fetching Boards")
            }
    }

    fun loadUserData(activity: Activity, readBoardsList: Boolean = false) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(UserModel::class.java)
                if (loggedInUser != null) {
                    when (activity) {
                        is SignInActivity -> {
                            activity.signInSuccess(loggedInUser)
                        }
                        is MainActivity -> {
                            activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                        }
                        is EditProfileActivity -> {
                            activity.setUserDataInUI(loggedInUser)
                        }
                    }
                }
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "${e.message}")
            }
    }

    fun getCurrentUserId(): String {
        val currentUser = auth.currentUser
        var currentUserId = ""
        if(currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    fun addUpdateTaskList(activity: TaskListActivity, board: BoardModel) {
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "TaskList updated Successfully")
                activity.addUpdateTaskListSuccess()
            }.addOnFailureListener { ex ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error updating TaskList: ${ex.message}")
            }
    }

    fun getAssignedMembersListDetails(activity: BoardMembersActivity, assignedTo: ArrayList<String>) {
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val membersList: ArrayList<UserModel> = ArrayList()
                document.documents.forEach {
                    val member = it.toObject(UserModel::class.java)!!
                    membersList.add(member)
                }
                activity.populateMembersListToUI(membersList)
            }.addOnFailureListener { ex ->
                activity.hideProgressDialog()
                Log.i(activity.javaClass.simpleName, ex.message.toString())
            }
    }
}