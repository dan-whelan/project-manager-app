package com.learning.projectmanager.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
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
import com.learning.projemanag.databinding.ActivityEditProfileBinding
import com.learning.projectmanager.firebase.FirestoreClass
import com.learning.projectmanager.models.UserModel
import com.learning.projectmanager.utils.Constants
import kotlinx.coroutines.launch
import java.io.File
import java.util.HashMap

class EditProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirestoreClass

    private var mSelectedImage: Uri? = null
    private var mLatestTmpUri: Uri? = null
    private var mProfileImgUrl: String = ""
    private lateinit var mUserDetails: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        db = FirestoreClass()
        setContentView(binding.root)

        setupActionBar()

        db.getUserData(this)

        binding.editProfileImg.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems = arrayOf(
                "Select Picture From Gallery",
                "Capture Picture From Camera"
            )
            pictureDialog.setItems(pictureDialogItems){
                    _, idx ->
                when(idx) {
                    0 -> chooseImageFromGallery()
                    1 -> takePictureWithCamera()
                }
            }
            pictureDialog.show()
        }

        binding.updateBtn.setOnClickListener {
            if(mSelectedImage != null) uploadUserImage()
            else {
                showCustomProgressDialog()
                updateUserProfileData()
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
            ).withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if(report != null) {
                        if (report.areAllPermissionsGranted()) {
                            galleryImageSelection()
                        }
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    Constants.showRationaleDialogForPermissions(this@EditProfileActivity)
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
                    .with(this@EditProfileActivity)
                    .load(mSelectedImage)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.editProfileImg)
            } else {
                Log.e("ERROR", "Cannot Access Gallery at this time")
            }
        }

    /*
        Same as above just for camera
     */

    private fun takePictureWithCamera() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if(report != null) {
                        if (report.areAllPermissionsGranted()) {
                            Log.i("EDIT", "Have been selected")
                            takeImage()
                        }
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    Constants.showRationaleDialogForPermissions(this@EditProfileActivity)
                }
            }).onSameThread().check()
    }

    private fun takeImage() {
        lifecycleScope.launch {
            getTmpFileUri().let { uri ->
                mLatestTmpUri = uri
                getPictureFromCamera.launch(uri)
            }
        }
    }


    private val getPictureFromCamera =
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ){ isSuccess ->
            if(isSuccess) {
                mLatestTmpUri?.let { uri ->
                    mSelectedImage = uri
                    Glide
                        .with(this@EditProfileActivity)
                        .load(mSelectedImage)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(binding.editProfileImg)
                }
            }
        }

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".jpeg", cacheDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(applicationContext, "com.learning.projemanag.provider", tmpFile)
    }

    /*
        Setup Action Bar
     */

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

    /*
        Populate UI with user info from Firestore
     */

    fun setUserDataInUI(user: UserModel) {
        mUserDetails = user
        Glide
            .with(this@EditProfileActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(binding.editProfileImg)
        binding.editNameTxt.setText(user.name)
        binding.editEmailTxt.setText(user.email)
        if(user.mobile != 0L) binding.editMobileTxt.setText(user.mobile.toString())
    }

    /*
        Upload User selected image from device to firebase storage
     */
    private fun uploadUserImage() {
        showCustomProgressDialog()
        if(mSelectedImage != null) {
            val sRef: StorageReference =
                FirebaseStorage.getInstance().reference.child(
                    "USER_IMAGE${System.currentTimeMillis()}.${Constants.getFileExtension(this, mSelectedImage!!)}"
                )
            sRef.putFile(mSelectedImage!!).addOnSuccessListener { taskSnapshot ->
                Log.i("SREF", taskSnapshot.metadata?.reference?.downloadUrl.toString())
                taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    mProfileImgUrl = uri.toString()
                    updateUserProfileData()
                }
            }.addOnFailureListener {ex ->
                Log.e("ERROR", ex.message.toString())
                hideProgressDialog()
            }
        }
    }


    /*
        Updating user details in Firestore
     */
    private fun updateUserProfileData() {
        val userHashMap = HashMap<String, Any>()
        Log.i("here", "")
        if(mProfileImgUrl.isNotEmpty() && mProfileImgUrl != mUserDetails.image)
            userHashMap[Constants.IMAGE] = mProfileImgUrl
        if(binding.editNameTxt.text.toString() != mUserDetails.name)
            userHashMap[Constants.NAME] = binding.editNameTxt.text.toString()
        if(binding.editMobileTxt.text.toString() != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = binding.editMobileTxt.text.toString().toLong()
        }

        db.updateUserProfileData(this@EditProfileActivity, userHashMap)
    }

    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

}