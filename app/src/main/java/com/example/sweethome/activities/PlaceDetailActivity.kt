package com.example.sweethome.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sweethome.databinding.ActivityMainBinding
import com.example.sweethome.databinding.ActivityPlaceDetailBinding

class PlaceDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaceDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}