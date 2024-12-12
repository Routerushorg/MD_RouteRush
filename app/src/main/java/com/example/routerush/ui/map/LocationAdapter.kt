package com.example.routerush.ui.map

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.routerush.R
import com.example.routerush.data.LocationItem

class LocationAdapter(private val locations: MutableList<LocationItem>,
                      private val onLocationClickListener: (LocationItem) -> Unit) :
    RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val numberTextView: TextView = itemView.findViewById(R.id.tv_number)
        val nameTextView: TextView = itemView.findViewById(R.id.tv_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_address, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.numberTextView.text =  holder.itemView.context.getString(R.string.location_number, position + 1)
        holder.nameTextView.text = location.name
        holder.itemView.setOnClickListener {
            onLocationClickListener(location)
        }
    }

    override fun getItemCount(): Int = locations.size

    fun addLocation(location: LocationItem) {
        // Cek apakah lokasi sudah ada
        if (!locations.contains(location)) {
            locations.add(location)
            notifyItemInserted(locations.size - 1)
        } else {
            // Location already exists, no need to add again
            Log.d("LocationAdapter", "Location already exists: ${location.name}")
        }
    }

    fun clearLocations() {
        val size = locations.size
        locations.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun getAddresses(): List<String> {
        return locations.map { it.name }
    }
    fun getLocations(): List<LocationItem> {
        return locations
    }
    fun updateData(newLocations: List<LocationItem>) {
        val size = locations.size
        locations.clear()
        locations.addAll(newLocations)
        notifyItemRangeRemoved(0,size)
    }
}