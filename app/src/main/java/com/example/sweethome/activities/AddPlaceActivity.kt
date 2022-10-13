package com.example.sweethome.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.sweethome.database.DatabaseHandler
import com.example.sweethome.databinding.ActivityAddPlaceBinding
import com.example.sweethome.models.SweetHomeModel

import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


class AddPlaceActivity : AppCompatActivity(),View.OnClickListener {
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var binding: ActivityAddPlaceBinding
    private var mPlaceDetails: SweetHomeModel? = null
    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    private val getPicture =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
                if(it.data != null){
                    val selectedImageBitmap: Bitmap
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        selectedImageBitmap = ImageDecoder.decodeBitmap( ImageDecoder.createSource(this.contentResolver, it.data?.data!!))
                    } else {
                        selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, it.data?.data)
                        Log.e("Saved Image: ","Path :: $saveImageToInternalStorage")
                    }
                    saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                    binding.ivPlaceImage.setImageURI(saveImageToInternalStorage)
                }
            }
        }
    private val takePicture =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
                if(it.data != null){
                    val thumbnail = it.data?.extras?.get("data") as Bitmap
                    saveImageToInternalStorage = saveImageToInternalStorage(thumbnail)
                    Log.e("Saved Image : ","Path :: $saveImageToInternalStorage")
                    binding.ivPlaceImage.setImageURI(saveImageToInternalStorage)

                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlaceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAddPlace.setNavigationOnClickListener {
            onBackPressed()
        }
        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)) {
            mPlaceDetails =
                intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as SweetHomeModel
        }
        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        updateDateInView()
        if (mPlaceDetails != null) {
            supportActionBar?.title = "Edit Place"
            binding.etTitle.setText(mPlaceDetails!!.title)
            binding.etDescription.setText(mPlaceDetails!!.description)
            binding.etDate.setText(mPlaceDetails!!.date)
            binding.etLocation.setText(mPlaceDetails!!.location)
            mLatitude = mPlaceDetails!!.latitude
            mLongitude = mPlaceDetails!!.longitude
            saveImageToInternalStorage = Uri.parse(mPlaceDetails!!.image)
            binding.ivPlaceImage.setImageURI(saveImageToInternalStorage)
            binding.btnSave.text = "UPDATE"
        }
        binding.etDate.setOnClickListener(this)
        binding.tvAddImage.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)

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
                        1 -> takePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }
            binding.btnSave.id ->{
                when {
                    binding.etTitle.text.isNullOrEmpty() -> {
                        Toast.makeText(this,"Please enter title",Toast.LENGTH_SHORT).show()
                    }
                    binding.etDescription.text.isNullOrEmpty() -> {
                        Toast.makeText(this,"Please enter description",Toast.LENGTH_SHORT).show()
                    }
                    binding.etLocation.text.isNullOrEmpty() -> {
                        Toast.makeText(this,"Please enter location",Toast.LENGTH_SHORT).show()
                    }
                       saveImageToInternalStorage == null -> {
                            Toast.makeText(this,"Please select image",Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            val sweetHomeModel = SweetHomeModel(
                                if (mPlaceDetails == null) 0 else mPlaceDetails!!.id,
                                binding.etTitle.text.toString(),
                                binding.etDescription.text.toString(),
                                saveImageToInternalStorage.toString(),
                                binding.etDate.text.toString(),
                                binding.etLocation.text.toString(),
                                mLatitude,
                                mLongitude
                            )
                            val dbHandler = DatabaseHandler(this)
                            if (mPlaceDetails == null) {
                                val addSweetHomeModel = dbHandler.addPlace(sweetHomeModel)
                                if (addSweetHomeModel > 0) {
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                }
                            } else {
                                val updateSweetHomeModel = dbHandler.updatePlace(sweetHomeModel)
                                if (updateSweetHomeModel > 0) {
                                    setResult(Activity.RESULT_OK)
                                    finish()
                                }
                            }

                        }
                }
            }
        }
    }
    private fun takePhotoFromCamera(){
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).withListener(object : MultiplePermissionsListener{
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if(report.areAllPermissionsGranted()){
                    val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    takePicture.launch(captureIntent)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
                showRationalDialogForPermissions()
            }

        }).onSameThread().check()
    }
    private fun choosePhotoFromGallery() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE).
        withListener(object: MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport)
            {if(report.areAllPermissionsGranted()){
                val galleryIntent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                getPicture.launch(galleryIntent)

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
    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("SweetHomeImages", Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")
        try{
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }
}