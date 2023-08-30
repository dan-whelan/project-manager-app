package com.learning.projemanag.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.webkit.MimeTypeMap

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

}