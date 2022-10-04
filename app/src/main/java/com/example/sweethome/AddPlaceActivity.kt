package com.example.sweethome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sweethome.databinding.ActivityAddPlaceBinding
import com.example.sweethome.databinding.ActivityMainBinding

class AddPlaceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPlaceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlaceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAddPlace.setNavigationOnClickListener{
            onBackPressed()
        }
    }
}