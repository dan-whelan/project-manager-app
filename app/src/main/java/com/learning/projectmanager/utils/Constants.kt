package com.learning.projectmanager.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.webkit.MimeTypeMap
import com.learning.projectmanager.activities.CardDetailsActivity
import com.learning.projectmanager.activities.TaskListActivity

object Constants {
    const val USERS = "users"
    const val BOARDS = "boards"

    const val IMAGE = "image"
    const val NAME = "name"
    const val MOBILE = "mobile"

    const val ASSIGNED_TO = "assignedTo"
    const val DOCUMENT_ID = "documentId"
    const val TASK_LIST = "taskList"
    const val BOARD_DETAIL = "board_detail"

    const val ID = "id"
    const val EMAIL = "email"

    const val TASK_POSITION = "task_position"
    const val CARD_POSITION = "card_position"
    const val BOARD_MEMBERS = "board_members"

    const val SELECT = "select"
    const val UNSELECT = "unselect"

    /*
    Rationale for Using Permissions, sends user to settings for access if previously denied
     */
    fun showRationaleDialogForPermissions(activity: Activity): AlertDialog =
        AlertDialog.Builder(activity)
            .setMessage("Please Turn on Permissions from Application Settings")
            .setPositiveButton("GO TO SETTINGS"){
                    _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", activity.packageName, null)
                    intent.data = uri
                    activity.startActivity(intent)
                } catch(e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel"){
                    dialog, _ ->
                dialog.dismiss()
            }.show()

    fun getFileExtension(activity: Activity, uri: Uri): String? =
        MimeTypeMap
            .getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri))

    fun dialogForDelete(context: Context, position: Int, title: String) {
        val alertDialog: AlertDialog = AlertDialog.Builder(context)
            .setTitle("Alert")
            .setMessage("Are you sure you want to delete $title.\nThis Cannot be Undone.")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("Yes") { dialogInterface, _ ->
                dialogInterface.dismiss()
                if(context is TaskListActivity) {
                    context.deleteTaskList(position)
                } else if(context is CardDetailsActivity) {
                    context.deleteCard()
                }
            }.setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}