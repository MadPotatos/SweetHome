package com.example.sweethome.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sweethome.databinding.ActivityMainBinding
import com.example.sweethome.databinding.ActivityPlaceDetailBinding
import com.example.sweethome.models.SweetHomeModel

class PlaceDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaceDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        var sweetHomeModel :SweetHomeModel? = null
         if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
             sweetHomeModel = intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as SweetHomeModel
         }
        setSupportActionBar(binding.toolbarPlaceDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = sweetHomeModel?.title
        binding.toolbarPlaceDetail.setNavigationOnClickListener{
            onBackPressed()
        }
        binding.tvDescription.text = sweetHomeModel?.description
        binding.tvLocation.text = sweetHomeModel?.location
        binding.ivPlaceImage.setImageURI(Uri.parse(sweetHomeModel?.image))
    }
}