package com.example.sweethome.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.sweethome.models.SweetHomeModel

class DatabaseHandler(context :Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "SweetHomeDB"
        private const val TABLE_NAME = "SweetHomeTable"
        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_IMAGE = "image"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = ("CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT" + ")")
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    fun updatePlace(place: SweetHomeModel) : Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, place.title)
        contentValues.put(KEY_DESCRIPTION, place.description)
        contentValues.put(KEY_IMAGE, place.image)
        contentValues.put(KEY_DATE, place.date)
        contentValues.put(KEY_LOCATION, place.location)
        contentValues.put(KEY_LATITUDE, place.latitude)
        contentValues.put(KEY_LONGITUDE, place.longitude)
        val success = db.update(TABLE_NAME, contentValues, KEY_ID +"=" + place.id, null)
        db.close()
        return success
    }
    fun addPlace(place: SweetHomeModel) : Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, place.title)
        contentValues.put(KEY_DESCRIPTION, place.description)
        contentValues.put(KEY_IMAGE, place.image)
        contentValues.put(KEY_DATE, place.date)
        contentValues.put(KEY_LOCATION, place.location)
        contentValues.put(KEY_LATITUDE, place.latitude)
        contentValues.put(KEY_LONGITUDE, place.longitude)
        val success = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return success
    }

    fun getPlacesList(): ArrayList<SweetHomeModel> {
        val placeList: ArrayList<SweetHomeModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION))
                val image = cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE))
                val location = cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOCATION))
                val latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LATITUDE))
                val longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LONGITUDE))
                val place = SweetHomeModel(id, title, description, image, date, location, latitude, longitude)
                placeList.add(place)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return placeList
    }
}
