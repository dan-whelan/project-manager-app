package com.learning.projectmanager.activities

import android.Manifest
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.learning.projemanag.R
import com.learning.projemanag.databinding.ActivityCreateBoardBinding
import com.learning.projectmanager.firebase.FirestoreClass
import com.learning.projectmanager.models.BoardModel
import com.learning.projectmanager.utils.Constants

class CreateBoardActivity : com.learning.projectmanager.activities.BaseActivity() {
    private lateinit var binding: ActivityCreateBoardBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirestoreClass

    private lateinit var mUsername: String
    private var mSelectedImage: Uri? = null
    private var mBoardImageUrl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        db = FirestoreClass()
        setContentView(binding.root)

        setupActionBar()
        if(intent.hasExtra(Constants.NAME))
            mUsername = intent.getStringExtra(Constants.NAME)!!

        binding.editBoardImg.setOnClickListener {
            chooseImageFromGallery()
        }
        binding.createBtn.setOnClickListener {
            if(mSelectedImage != null) uploadBoardImage()
            else {
                showCustomProgressDialog()
                createBoard()
            }
        }
    }

    fun boardCreatedSuccessfully() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        Toast.makeText(
            this,
            "Board Created Successfully",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.signUpToolbar)
        if(supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow_white)
            supportActionBar?.title = ""
        }
        binding.signUpToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun createBoard() {
        val assignedUsersArrayList: ArrayList<String> = ArrayList()
        assignedUsersArrayList.add(getCurrentUserID())

        val board = BoardModel(
            binding.editBoardNameTxt.text.toString(),
            mBoardImageUrl,
            mUsername,
            assignedUsersArrayList
        )

        db.registerBoard(this, board)
    }

    private fun uploadBoardImage() {
        showCustomProgressDialog()
        if(mSelectedImage != null) {
            val sRef: StorageReference =
                FirebaseStorage.getInstance().reference.child(
                    "BOARD_IMAGE${System.currentTimeMillis()}.${Constants.getFileExtension(this, mSelectedImage!!)}"
                )
            sRef.putFile(mSelectedImage!!).addOnSuccessListener { taskSnapshot ->
                Log.i("SREF", taskSnapshot.metadata?.reference?.downloadUrl.toString())
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    mBoardImageUrl = uri.toString()
                    createBoard()
                }
            }.addOnFailureListener {ex ->
                Log.e("ERROR", ex.message.toString())
                hideProgressDialog()
            }
        }
    }

    /*
        Allows for image to be selected from gallery using Dexter for permissions
    */
    private fun chooseImageFromGallery() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report != null) {
                        if (report.areAllPermissionsGranted()) {
                            galleryImageSelection()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    Constants.showRationaleDialogForPermissions(this@CreateBoardActivity)
                }
            }).onSameThread().check()
    }

    private fun galleryImageSelection() =
        getGalleryResult.launch("image/*")

    private val getGalleryResult =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            if(uri != null) {
                mSelectedImage = uri
                Glide
                    .with(this@CreateBoardActivity)
                    .load(mSelectedImage)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.editBoardImg)
            } else {
                Log.e("ERROR", "Cannot Access Gallery at this time")
            }
        }
}