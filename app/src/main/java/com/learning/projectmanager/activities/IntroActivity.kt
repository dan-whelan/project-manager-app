package com.learning.projectmanager.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.learning.projemanag.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
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
        binding.appNameTxt.typeface = typeface

        binding.signUpBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.signInBtn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }
}