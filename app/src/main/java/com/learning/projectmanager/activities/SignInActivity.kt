package com.learning.projectmanager.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.learning.projemanag.R
import com.learning.projemanag.databinding.ActivitySignInBinding
import com.learning.projectmanager.firebase.FirestoreClass
import com.learning.projectmanager.models.UserModel

class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirestoreClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        db = FirestoreClass()
        setContentView(binding.root)
        setupActionBar()

        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        binding.signInBtn.setOnClickListener {
            signInUser()
        }

    }

    fun signInSuccess(user: UserModel) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.signInToolbar)
        if(supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            supportActionBar?.title = ""
        }
        binding.signInToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun signInUser() {
        val email: String = binding.editEmailTxt.text.toString().trim{ it <= ' '}
        val password: String = binding.editPasswordTxt.text.toString().trim{ it <= ' ' }
        if(validateForm(email, password)) {
            showCustomProgressDialog()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if(task.isSuccessful) {
                        Log.d("SignIn", "signInWithEmail:Success")
                        val user = auth.currentUser
                        db.loadUserData(this)
                        Toast.makeText(
                            this,
                            "Welcome",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Log.w("SignIn", "signInWithEmail:Failure", task.exception)
                        Toast.makeText(
                            this,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    private fun validateForm(email: String, password: String): Boolean =
        when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please Enter an Email Address")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please Enter a Password")
                false
            }
            else -> true
        }
}