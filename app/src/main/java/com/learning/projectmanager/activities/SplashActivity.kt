package com.learning.projectmanager.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.learning.projemanag.databinding.ActivitySplashBinding
import com.learning.projectmanager.firebase.FirestoreClass

class SplashActivity : com.learning.projectmanager.activities.BaseActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val typeface: Typeface = Typeface.createFromAsset(assets, "BebasNeue-Regular.ttf")
        binding.splashTitleTxt.typeface = typeface

        Handler(Looper.getMainLooper()).postDelayed({
            var currentUserId = FirestoreClass().getCurrentUserId()
            if(currentUserId.isNotEmpty()) {
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(
                    this,
                    "Welcome",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            }
        }, 2500)

    }
}