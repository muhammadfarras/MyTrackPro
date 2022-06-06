package com.farras.mytrackpro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.farras.mytrackpro.databinding.ActivityOpeningBinding

private lateinit var binding: ActivityOpeningBinding

class OpeningActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpeningBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}