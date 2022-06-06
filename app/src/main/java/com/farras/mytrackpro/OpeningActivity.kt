package com.farras.mytrackpro

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.farras.mytrackpro.databinding.ActivityOpeningBinding

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityOpeningBinding

class OpeningActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpeningBinding.inflate(layoutInflater)
        val view = binding.root
        var context = application.applicationContext
        setContentView(view)

        binding.openingAnimationView.repeatCount = 1
        binding.openingAnimationView.addAnimatorListener(object : Animator.AnimatorListener{

            override fun onAnimationEnd(animation: Animator?) {
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}

        })
    }
}