package com.example.sweethome.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sweethome.adapters.PlacesAdapter
import com.example.sweethome.database.DatabaseHandler
import com.example.sweethome.databinding.ActivityMainBinding
import com.example.sweethome.models.SweetHomeModel
import com.example.sweethome.utilz.SwipeToDeleteCallback
import com.example.sweethome.utilz.SwipeToEditCallback

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val getList = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            getPlacesListFromLocalDB()
        }else {
            Log.e("Activity", "Cancelled or Back Pressed")
        }
    }
    companion object{
        val EXTRA_PLACE_DETAILS = "extra_place_details"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.fabAddPlace.setOnClickListener{
            val intent = Intent(this, AddPlaceActivity::class.java)
            getList.launch(intent)
        }
        getPlacesListFromLocalDB()
    }
    private fun setupPlacesRecyclerView(placeList: ArrayList<SweetHomeModel>) {
        binding.rvPlacesList.layoutManager = LinearLayoutManager(this)
        binding.rvPlacesList.setHasFixedSize(true)
        val placesAdapter = PlacesAdapter(placeList)
        binding.rvPlacesList.adapter = placesAdapter
        placesAdapter.setOnClickListener(object: PlacesAdapter.OnClickListener{
            override fun onClick(position: Int, model: SweetHomeModel) {
                val intent = Intent(this@MainActivity,PlaceDetailActivity::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS,model)
                startActivity(intent)
            }

        })
        val editSwipeHandler = object : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, direction: Int) {
                val adapter = binding.rvPlacesList.adapter as PlacesAdapter
                adapter.notifyEditItem(this@MainActivity,viewHolder.adapterPosition)

            }
        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(binding.rvPlacesList)

    }
    private fun getPlacesListFromLocalDB() {
        val dbHandler = DatabaseHandler(this)
        val getPlaceList = dbHandler.getPlacesList()
        if (getPlaceList.size > 0) {
            binding.rvPlacesList.visibility = View.VISIBLE
            binding.tvNoPlacesFound.visibility = View.GONE
            setupPlacesRecyclerView(getPlaceList)
        } else {
            binding.rvPlacesList.visibility = View.GONE
            binding.tvNoPlacesFound.visibility = View.VISIBLE


        }
    }
}