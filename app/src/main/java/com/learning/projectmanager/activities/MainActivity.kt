package com.learning.projectmanager.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.learning.projemanag.R
import com.learning.projectmanager.adapters.AssignedBoardsAdapter
import com.learning.projemanag.databinding.ActivityMainBinding
import com.learning.projectmanager.firebase.FirestoreClass
import com.learning.projectmanager.models.BoardModel
import com.learning.projectmanager.models.UserModel
import com.learning.projectmanager.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirestoreClass

    private lateinit var mUsername: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        db = FirestoreClass()
        setContentView(binding.root)

        setupActionBar()

        binding.navigationView.setNavigationItemSelectedListener(this)

        findViewById<FloatingActionButton>(R.id.add_board_btn).setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUsername)
            startCreateBoardActivity.launch(intent)
        }

        db.getUserData(this, true)
    }
    /*
        Populates the UI with all boards associated to current user
     */
    fun populateBoardsListToUI(boardsList: ArrayList<BoardModel>) {
        hideProgressDialog()
        if(boardsList.size > 0) {
            val adapter = AssignedBoardsAdapter(this, boardsList)
            findViewById<RecyclerView>(R.id.board_list).let {
                it.visibility = View.VISIBLE
                it.layoutManager = LinearLayoutManager(this@MainActivity)
                it.setHasFixedSize(true)
                it.adapter = adapter
            }
            findViewById<TextView>(R.id.no_boards_txt).visibility = View.GONE

            adapter.setOnClickListener(object: AssignedBoardsAdapter.OnClickListener{
                override fun onClick(position: Int, model: BoardModel) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })
        } else {
            findViewById<RecyclerView>(R.id.board_list).visibility = View.GONE
            findViewById<TextView>(R.id.no_boards_txt).visibility = View.VISIBLE
        }
    }


    private fun setupActionBar() {
        setSupportActionBar(binding.mainAppBar.mainToolbar)
        binding.mainAppBar.mainToolbar.setNavigationIcon(R.drawable.ic_action_nav_menu)
        binding.mainAppBar.mainToolbar.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }
    /*
        Closes drawer if drawer is open, else closes app
     */
    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    /*
        populates user details to drawer on main screen
     */
    fun populateUserDetailsToDrawerUI(user: UserModel, readBoardsList: Boolean = false) {
        mUsername = user.name
        Glide
            .with(this@MainActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(findViewById(R.id.profile_img))

        findViewById<TextView>(R.id.username).text = user.name

        if(readBoardsList) {
            showCustomProgressDialog()
            db.getBoardsList(this)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_my_profile -> {
                Log.i("Main", "Gone to EditProfile")
                val intent = Intent(this, EditProfileActivity::class.java)
                startEditProfile.launch(intent)
            }
            R.id.nav_sign_out -> {
                Log.i("Main", "User SignOut")
                auth.signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private val startEditProfile =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){ result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK) {
                db.getUserData(this@MainActivity)
            } else {
                Log.e("Error", "Update Profile failed")
            }
        }

    private val startCreateBoardActivity =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK) {
                db.getBoardsList(this)
            } else {
                Log.e("Error", "Update Boards Failed")
            }
        }
}