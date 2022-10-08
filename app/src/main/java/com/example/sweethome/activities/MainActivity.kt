package com.example.sweethome.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.sweethome.database.DatabaseHandler
import com.example.sweethome.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.fabAddPlace.setOnClickListener{
            val intent = Intent(this, AddPlaceActivity::class.java)
            startActivity(intent)
        }
        getPlacesListFromLocalDB()
    }
    private fun getPlacesListFromLocalDB() {
        val dbHandler = DatabaseHandler(this)
        val getPlaceList = dbHandler.getPlacesList()
        if (getPlaceList.size > 0) {
           for(i in getPlaceList) {
               val place = "ID: " + i.id + " Title: " + i.title + " Description: " + i.description + " Image: " + i.image + " Date: " + i.date + " Location: " + i.location + " Latitude: " + i.latitude + " Longitude: " + i.longitude
               Log.e("Place List", place)
           }
        }
    }
}