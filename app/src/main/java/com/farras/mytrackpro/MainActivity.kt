package com.farras.mytrackpro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.farras.mytrackpro.databinding.ActivityMainBinding

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }
}