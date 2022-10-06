package com.example.sweethome

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.sweethome.databinding.ActivityAddPlaceBinding

import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.util.*


class AddPlaceActivity : AppCompatActivity(),View.OnClickListener {
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var binding: ActivityAddPlaceBinding
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
                if(it.data != null){
                    val selectedImageUri: Uri? = it.data?.data
                    if(selectedImageUri != null){
                        binding.ivPlaceImage.setImageURI(selectedImageUri)
                    }else{
                        Toast.makeText(this,"Image not found",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    companion object{
        private const val GALLERY = 1
        private const val CAMERA = 2
    }

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
        binding.tvAddImage.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            binding.etDate.id ->{
                DatePickerDialog(this@AddPlaceActivity, dateSetListener, cal.get(Calendar.YEAR)
                ,cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()

            }
            binding.tvAddImage.id ->{
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pickerDialogItems = arrayOf("Select photo from Gallery","Take photo from camera")
                pictureDialog.setItems(pickerDialogItems){
                    _,which ->
                    when(which){
                        0 -> choosePhotoFromGallery()
                        1 -> Toast.makeText(this@AddPlaceActivity,"Coming soon...",Toast.LENGTH_SHORT).show()
                    }
                }
                pictureDialog.show()
            }
        }
    }

    private fun choosePhotoFromGallery() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE).
        withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport)
            {if(report.areAllPermissionsGranted()){
                val galleryIntent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                getResult.launch(galleryIntent)

            }}
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken)
            {
                showRationalDialogForPermissions()
            }
        }).onSameThread().check()

    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this).setMessage(
            "You have turned off permission required for this feature" +
                    "It can be enabled under the" + " Applications Settings"
        ).setPositiveButton("GO TO SETTINGS") { _, _ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }.setNegativeButton("Cancel"){
            dialog, which ->
            dialog.dismiss()
        }.show()
    }


    private fun updateDateInView(){
        val format = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(format,Locale.getDefault())
        binding.etDate.setText(sdf.format(cal.time).toString())

    }
}