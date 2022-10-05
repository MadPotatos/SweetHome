package com.example.sweethome

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.sweethome.databinding.ActivityAddPlaceBinding
import com.example.sweethome.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class AddPlaceActivity : AppCompatActivity(),View.OnClickListener {
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
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
        dateSetListener = DatePickerDialog.OnDateSetListener{
            view,year,month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            updateDateInView()
        }
        binding.etDate.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            binding.etDate.id ->{
                DatePickerDialog(this@AddPlaceActivity, dateSetListener, cal.get(Calendar.YEAR)
                ,cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()

            }

        }
    }
    private fun updateDateInView(){
        val format = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(format,Locale.getDefault())
        binding.etDate.setText(sdf.format(cal.time).toString())
    }
}