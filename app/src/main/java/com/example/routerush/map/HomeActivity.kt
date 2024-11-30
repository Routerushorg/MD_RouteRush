package com.example.routerush.map

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.routerush.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.routerush.databinding.ActivityHomeBinding
import com.example.routerush.login.LoginActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.fabMenu.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START) // Tutup drawer jika sudah terbuka
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START) // Buka drawer
            }
        }

        val drawerLayout = binding.drawerLayout
        val navigationView = binding.navigationView

        // Menangani item click pada navigation view
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Mengatur item yang dipilih
            menuItem.isChecked = true

            // Menutup drawer setelah memilih item
            drawerLayout.closeDrawer(GravityCompat.START)

            // Menangani klik berdasarkan ID item
            when (menuItem.itemId) {
                R.id.menu_add_route -> {
                    // Tindakan untuk item Add Route
                    Toast.makeText(this, "Add Route clicked", Toast.LENGTH_SHORT).show()
                    // Lakukan aksi yang sesuai, misalnya membuka Activity baru untuk menambah route
                }
                else -> {
                    // Tindakan default
                }
            }
            true
        }

        // Handle Navigation Drawer

        val headerView = navigationView.getHeaderView(0)
        val logoutButton = headerView.findViewById<Button>(R.id.btn_logout)

        // Menangani klik tombol Logout
        logoutButton.setOnClickListener {
            // Tindakan logout, misalnya mengarahkan ke LoginActivity
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()

            // Mengarahkan ke LoginActivity setelah logout
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()  // Menyelesaikan MainActivity setelah logout
        }



        BottomSheetBehavior.from(binding.sheet).apply {
            peekHeight = 100
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }


}