package com.learning.projemanag.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.learning.projemanag.R
import com.learning.projemanag.databinding.ActivitySignUpBinding
import com.learning.projemanag.firebase.FirestoreClass
import com.learning.projemanag.models.UserModel

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirestoreClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
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

        binding.signUpBtn.setOnClickListener {
            registerUser()
        }

    }

    private fun setupActionBar() {
        setSupportActionBar(binding.signUpToolbar)
        if(supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
            supportActionBar?.title = ""
        }
        binding.signUpToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun registerUser() {
        val name: String = binding.editNameTxt.text.toString().trim{ it <= ' ' }
        val email: String = binding.editEmailTxt.text.toString().trim{ it <= ' ' }
        val password: String = binding.editPasswordTxt.text.toString().trim{ it <= ' ' }

        if(validateForm(name, email, password)) {
            showCustomProgressDialog()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser =
                            task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = UserModel(
                            firebaseUser.uid,
                            name,
                            email
                        )
                        db.registerUser(this, user)
                    } else {
                        Toast.makeText(
                            this,
                            task.exception!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun validateForm(name: String, email: String, password: String): Boolean =
        when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please Enter a Name")
                false
            }
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

    fun userRegisteredSuccess() {
        Toast.makeText(
            this,
            "You have successfully registered",
            Toast.LENGTH_SHORT
        ).show()

        auth.signOut()
        finish()
    }
}