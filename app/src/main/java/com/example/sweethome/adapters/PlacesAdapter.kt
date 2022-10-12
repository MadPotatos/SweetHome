package com.example.sweethome.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sweethome.activities.AddPlaceActivity
import com.example.sweethome.activities.MainActivity
import com.example.sweethome.databinding.ItemPlaceBinding
import com.example.sweethome.models.SweetHomeModel

class PlacesAdapter(var list : ArrayList<SweetHomeModel>):
    RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    class ViewHolder(binding: ItemPlaceBinding): RecyclerView.ViewHolder(binding.root) {
        val placeImage = binding.ivPlaceImage
        val tvTitle = binding.tvTitle
        val tvDescription = binding.tvDescription
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return PlacesAdapter.ViewHolder(
            ItemPlaceBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        holder.placeImage.setImageURI(Uri.parse(model.image))
        holder.tvTitle.text = model.title
        holder.tvDescription.text = model.description
        holder.itemView.setOnClickListener{
            if(onClickListener != null){
                    onClickListener!!.onClick(position,model)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun notifyEditItem(activity:Activity, position: Int){
       val intent = Intent(activity, AddPlaceActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, list[position])
        activity.startActivity(intent)
        notifyItemChanged(position)
    }
    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }
    interface OnClickListener {
        fun onClick(position: Int,model: SweetHomeModel)
    }
}