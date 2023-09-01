package com.learning.projectmanager.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.learning.projectmanager.activities.BaseActivity
import com.learning.projectmanager.activities.BoardMembersActivity
import com.learning.projectmanager.activities.CardDetailsActivity
import com.learning.projectmanager.activities.CreateBoardActivity
import com.learning.projectmanager.activities.EditProfileActivity
import com.learning.projectmanager.activities.MainActivity
import com.learning.projectmanager.activities.SignInActivity
import com.learning.projectmanager.activities.SignUpActivity
import com.learning.projectmanager.activities.TaskListActivity
import com.learning.projectmanager.models.BoardModel
import com.learning.projectmanager.models.UserModel
import com.learning.projectmanager.utils.Constants

/*
    Firestore class for implementing functions that call the Firestore
    Separate in case need for change of DB is required
 */
class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /*
        Stores new user details in Firestore
     */
    fun registerUser(activity: SignUpActivity, userInfo: UserModel) =
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "${e.message}")
            }


    /*
        Stores new board details in Firestore
     */
    fun registerBoard(activity: CreateBoardActivity, boardInfo: BoardModel) =
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(boardInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "${e.message}")
            }


    /*
        Returns a list of boards that have been assigned to a user
     */
    fun getBoardsList(activity: MainActivity) =
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

    /*
        Updates any user profile data that has been changed in the Edit Profile Screen
     */
    fun updateUserProfileData(activity: EditProfileActivity, userHashMap: HashMap<String, Any>) =
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

    /*
        Returns the details on a specific board
     */
    fun getBoardDetails(activity: TaskListActivity, documentId: String) =
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener { document ->
                val board = document.toObject(BoardModel::class.java)!!
                board.documentId = documentId
                activity.boardDetails(board)
            }.addOnFailureListener { ex ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "ERROR in fetching Boards")
            }

    /*
        Returns the user data of logged in user
     */
    fun getUserData(activity: Activity, readBoardsList: Boolean = false) =
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
                            activity.populateUserDetailsToDrawerUI(loggedInUser, readBoardsList)
                        }
                        is EditProfileActivity -> {
                            activity.setUserDataInUI(loggedInUser)
                        }
                    }
                }
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "${e.message}")
            }


    /*
        Returns the user id of the user currently logged in
     */
    fun getCurrentUserId(): String {
        val currentUser = auth.currentUser
        var currentUserId = ""
        if(currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }
    /*
        Updates List Data in DB (Both TaskList and CardList)
     */
    fun addUpdateList(activity: Activity, board: BoardModel) {
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "TaskList updated Successfully")
                if(activity is TaskListActivity) activity.addUpdateTaskListSuccess()
                else if(activity is CardDetailsActivity) activity.addUpdateCardListSuccess()
            }.addOnFailureListener { ex ->
                if(activity is BaseActivity) activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error updating TaskList: ${ex.message}")
            }
    }

    /*
        Returns the details of the users that are assigned to a Board/Card
     */
    fun getAssignedMembersListDetails(activity: Activity, assignedTo: ArrayList<String>) =
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
                if(activity is BoardMembersActivity) activity.populateMembersListToUI(membersList)
                else if(activity is TaskListActivity) activity.boardMembersDetails(membersList)
            }.addOnFailureListener { ex ->
                if(activity is BaseActivity) activity.hideProgressDialog()
                Log.i(activity.javaClass.simpleName, ex.message.toString())
            }

    /*
        Returns the user details of a user that has been searched (Based on Email)
     */
    fun getMemberDetails(activity: BoardMembersActivity, email: String) =
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener { document ->
                if(document.documents.size > 0) {
                    val user = document.documents[0].toObject(UserModel::class.java)!!
                    activity.memberDetails(user)
                } else {
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No such member found")
                }
            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error Finding User, Please Try Again Later.")
            }

    /*
        Assigns a specified user to a board
     */
    fun assignMemberToBoard(
        activity: BoardMembersActivity,
        board: BoardModel,
        member: UserModel
    ) {
       val assignedToHashMap = HashMap<String, Any>()
       assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo

       mFireStore.collection(Constants.BOARDS)
           .document(board.documentId)
           .update(assignedToHashMap)
           .addOnSuccessListener {
               activity.memberAssignSuccessCall(member)
           }.addOnFailureListener {
               Log.e(activity.javaClass.simpleName, "Error while adding member, please try again later")
           }
    }
}