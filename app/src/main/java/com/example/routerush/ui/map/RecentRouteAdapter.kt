package com.example.routerush.ui.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.routerush.R

class RecentRouteAdapter :
    RecyclerView.Adapter<RecentRouteAdapter.RouteViewHolder>() {

    private val differ = AsyncListDiffer(this, DiffCallback())

    // ViewHolder untuk item route
    class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val routeTextView: TextView = itemView.findViewById(R.id.tv_route)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_route, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.routeTextView.text = differ.currentList[position]
    }

    override fun getItemCount(): Int = differ.currentList.size

    // Fungsi untuk memperbarui data
    fun updateRoutes(newRoutes: List<String>) {
        differ.submitList(newRoutes)
    }

    // DiffUtil untuk perbandingan data
    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem // Perbandingan unik per alamat
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem // Perbandingan isi alamat
        }
    }

    // Fungsi untuk mendapatkan daftar saat ini
    fun getRoutes(): List<String> {
        return differ.currentList
    }
}