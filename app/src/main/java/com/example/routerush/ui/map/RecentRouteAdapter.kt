package com.example.routerush.ui.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.routerush.R

class RecentRouteAdapter(private val routes: MutableList<String>) :
    RecyclerView.Adapter<RecentRouteAdapter.RouteViewHolder>() {

    class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val routeTextView: TextView = itemView.findViewById(R.id.tv_route)
    }
    private val differ = AsyncListDiffer(this, DiffCallback())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_route, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.routeTextView.text = routes[position]
    }

    override fun getItemCount(): Int = routes.size

    fun updateRoutes(newRoutes: List<String>) {
        routes.clear()
        routes.addAll(newRoutes)
        differ.submitList(newRoutes)
    }

    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
    fun getRoutes(): List<String> {
        return routes
    }
}