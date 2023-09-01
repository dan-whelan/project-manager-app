package com.learning.projectmanager.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.learning.projemanag.R

open class BaseActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false
    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    /*
        Displays a spinning progress dialog while processes occur in the background
     */
    fun showCustomProgressDialog() {
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_custom_progress)
        mProgressDialog.show()
    }

    /*
        Hides above progress dialog
     */
    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    /*
        Returns the UUID of the current user logged into the app
     */
    fun getCurrentUserID(): String =
        FirebaseAuth.getInstance().currentUser!!.uid

    /*
        Checks if the back button has been pressed twice in order to exit
     */
    fun doubleBackToExit() {
        if(doubleBackToExitPressedOnce) {
            super.getOnBackPressedDispatcher().onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(
            this,
            "Press Back again to exit",
            Toast.LENGTH_SHORT
        ).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    /*
        Displays a snackbar containing information about why a process has failed (user caused)
     */
    fun showErrorSnackBar(message: String) {
        val snackBar = Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG
        )
        snackBar.setBackgroundTint(ContextCompat.getColor(
            this,
            R.color.snackbar_error_colour)
        )
        snackBar.show()
    }
}