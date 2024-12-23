package com.example.routerush.ui.map

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.routerush.BuildConfig
import com.example.routerush.R
import com.example.routerush.data.LocationItem
import com.example.routerush.data.response.RouteResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.routerush.databinding.ActivityHomeBinding
import com.example.routerush.ui.ViewModelFactory
import com.example.routerush.ui.main.MainActivity
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request


class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var locationAdapter: LocationAdapter

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityHomeBinding
    private lateinit var optimizedAdapter: LocationAdapter

    private val markerLocations = mutableListOf<LatLng>()

    private lateinit var recentRouteAdapter: RecentRouteAdapter

    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationAdapter = LocationAdapter(mutableListOf()) { location ->
            moveToLocation(location)
        }
        binding.rvAddresses.layoutManager = LinearLayoutManager(this)
        binding.rvAddresses.adapter = locationAdapter

        

        binding.svSearchAddress.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchLocation(query)
                    binding.svSearchAddress.setQuery("", true)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        binding.fabMenu.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START) // Tutup drawer jika sudah terbuka
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START) // Buka drawer
            }
        }

        recentRouteAdapter = RecentRouteAdapter()
        binding.rvRoutes.layoutManager = LinearLayoutManager(this)
        binding.rvRoutes.adapter = recentRouteAdapter

        viewModel.addressHistory.observe(this) { route ->
            if (!route.isNullOrEmpty()) {
                recentRouteAdapter.updateRoutes(route)
            }
        }
        viewModel.fetchAddressHistory()


        binding.btnOptimizeRoute.setOnClickListener {
            val addresses = locationAdapter.getAddresses()
            viewModel.fetchAddressHistory()
            if (addresses.isNotEmpty()) {
                viewModel.optimizeRoute(addresses)
                binding.llSv.visibility= View.GONE
                binding.rvAddresses.visibility = View.GONE
                binding.btnOptimizeRoute.visibility = View.GONE
                binding.llTimeAndFuel.visibility = View.VISIBLE
                binding.rvOptimized.visibility = View.VISIBLE
                binding.btnNavigateRoute.visibility = View.VISIBLE

            } else {
                Toast.makeText(this, "No addresses to optimize!", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.optimizedRoute.observe(this) { route ->
            Log.d("OptimizedRoute", "Current route: $route")
            updateOptimizedRecyclerView(route)
            if (!route.isNullOrEmpty()) {

                mMap.clear()
                markerLocations.clear()


                val geocoder = Geocoder(this)
                val newLocations = mutableListOf<LatLng>()
                route.forEach { address ->
                    try {
                        val geocodeResult = geocoder.getFromLocationName(address, 1)
                        if (geocodeResult != null && geocodeResult.isNotEmpty()) {
                            val location = LatLng(geocodeResult[0].latitude, geocodeResult[0].longitude)
                            newLocations.add(location)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                markerLocations.addAll(newLocations)


                newLocations.forEachIndexed { index, location ->
                    val customMarker = createNumberedMarker(this, index + 1)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(location)
                            .title(route[index])
                            .icon(customMarker)
                    )
                }

                if (markerLocations.size > 1) {
                    drawOptimizedRoute(markerLocations)

                }

            }
        }

        binding.btnBack.setOnClickListener {
            binding.llSv.visibility= View.VISIBLE
            binding.rvAddresses.visibility = View.VISIBLE
            binding.btnOptimizeRoute.visibility = View.VISIBLE
            binding.llTimeAndFuel.visibility = View.GONE
            binding.rvOptimized.visibility = View.GONE
            binding.btnNavigateRoute.visibility = View.GONE
        }

        binding.btnErase.setOnClickListener{
            clearAllAddresses()
        }

        binding.btnNavigateRoute.setOnClickListener {
            if (markerLocations.isNotEmpty()) {
                navigateThroughLocations(markerLocations)
            } else {
                Toast.makeText(this, "No locations to navigate!", Toast.LENGTH_SHORT).show()
            }
        }

        val logoutButton = findViewById<Button>(R.id.btn_logout)
        val addRouteButton = findViewById<Button>(R.id.btn_add_route)

        addRouteButton.setOnClickListener{
            addRoutesToAddresses()
            Toast.makeText(this, "Route Added", Toast.LENGTH_SHORT).show()
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {

                // Arahkan ke MainActivity jika belum login
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                findViewById<TextView>(R.id.tv_user_name).text = user.name
                findViewById<TextView>(R.id.tv_user_email).text = user.email
            }
        }

        // Menangani klik tombol Logout
        logoutButton.setOnClickListener {
            viewModel.logout()
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()

            // Mengarahkan ke MainActivity setelah logout
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // Menyelesaikan HomeActivity setelah logout
        }



        BottomSheetBehavior.from(binding.sheet).apply {
            peekHeight = 100
            this.state = BottomSheetBehavior.STATE_HIDDEN
        }



        viewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.isLoading.observe(this)  { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.tvTime.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.tvDistance.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.ivTimeIcon.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.ivDistanceIcon.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.rvOptimized.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun addRoutesToAddresses() {
        val recentRoutes = recentRouteAdapter.getRoutes()
        recentRoutes.forEach { route ->
            // Convert route to LocationItem
            val geocoder = Geocoder(this)
            try {
                val geocodeResult = geocoder.getFromLocationName(route, 1)
                if (geocodeResult != null && geocodeResult.isNotEmpty()) {
                    val location = LatLng(geocodeResult[0].latitude, geocodeResult[0].longitude)
                    val locationItem = LocationItem(route, location.latitude, location.longitude)

                    // Add to rvAddresses
                    locationAdapter.addLocation(locationItem)
                    markerLocations.add(location)

                    // Add marker on the map
                    val customMarker = createNumberedMarker(this, locationAdapter.itemCount)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(location)
                            .title(route)
                            .icon(customMarker)
                    )
                    if (markerLocations.size > 1) {
                        drawRoute()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Update map view to include new markers
        if (markerLocations.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.Builder()
            markerLocations.forEach { boundsBuilder.include(it) }
            val bounds = boundsBuilder.build()
            val padding = 150
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
        }
    }

    private fun drawOptimizedRoute(locations: List<LatLng>) {
        lifecycleScope.launch {
            try {
                for (i in 0 until locations.size - 1) {
                    val origin = locations[i]
                    val destination = locations[i + 1]
                    val waypoints = locations.subList(1, locations.size - 1)
                    val url = getDirectionURL(origin, destination, waypoints)

                    val (path, info) = getDirection(url)
                    val (distance, duration) = info
                    drawPolyline(path)
                    binding.tvDistance.text = (distance)
                    binding.tvTime.text = (duration)
                }


                if (locations.isNotEmpty()) {
                    val boundsBuilder = LatLngBounds.Builder()
                    locations.forEach { boundsBuilder.include(it) }
                    val bounds = boundsBuilder.build()
                    val padding = 150
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@HomeActivity, "Failed to draw route: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun updateOptimizedRecyclerView(route: List<String>) {
        val optimizedLocations = route.map { address ->
            LocationItem(address, 0.0, 0.0)
        }
        optimizedAdapter = LocationAdapter(mutableListOf()) { location ->
            moveToLocation(location)
        }
        binding.rvOptimized.layoutManager = LinearLayoutManager(this)
        binding.rvOptimized.adapter = optimizedAdapter
        optimizedAdapter.updateData(optimizedLocations) // Buat fungsi di adapter
    }

    private fun navigateThroughLocations(locations: List<LatLng>) {
        if (locations.size < 2) {
            Toast.makeText(this, "At least two locations are required for navigation!", Toast.LENGTH_SHORT).show()
            return
        }

        val origin = locations.first() // First location as the start
        val destination = locations.last() // Last location as the destination
        val waypoints = locations.subList(1, locations.size - 1) // Intermediate stops (excluding origin and destination)

        // Construct the waypoints parameter
        val waypointString = waypoints.joinToString("|") { "${it.latitude},${it.longitude}" }

        val uri = Uri.parse(
            "https://www.google.com/maps/dir/?api=1" +
                    "&origin=${origin.latitude},${origin.longitude}" +
                    "&destination=${destination.latitude},${destination.longitude}" +
                    if (waypoints.isNotEmpty()) "&waypoints=$waypointString" else ""
        )
        Log.d("GoogleMapsURI", uri.toString())
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Google Maps is not installed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearAllAddresses() {
        locationAdapter.clearLocations()
        mMap.clear()
        markerLocations.clear()
        Toast.makeText(this, "All addresses cleared!", Toast.LENGTH_SHORT).show()
    }


    private fun moveToLocation(location: LocationItem) {
        // Move map to clicked location
        val latLng = LatLng(location.latitude, location.longitude)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f)) // Zoom into the location
    }


    private fun searchLocation(query: String) {
        val geocoder = Geocoder(this)
        try {
            val addresses = geocoder.getFromLocationName(query, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val location = LatLng(address.latitude, address.longitude)

                // Tambahkan lokasi ke RecyclerView
                val locationItem = LocationItem(query, address.latitude, address.longitude)
                locationAdapter.addLocation(locationItem)

                // Tambahkan marker dan pindahkan kamera
                markerLocations.add(location)
                val markerIndex = locationAdapter.itemCount
                val customMarker = createNumberedMarker(this, markerIndex)
                mMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title(query)
                        .icon(customMarker)
                )

                if (markerLocations.isNotEmpty()) {
                    val boundsBuilder = LatLngBounds.Builder()
                    markerLocations.forEach { boundsBuilder.include(it) }

                    val bounds = boundsBuilder.build()
                    val padding = 150 // Padding untuk batas kamera (dalam pixel)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
                }

                if (markerLocations.size > 1) {
                   drawRoute()
                }


            } else {
                Toast.makeText(this, "Location not found!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to search location: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun drawRoute() {
        if (markerLocations.size > 1) {
            val origin = markerLocations[0]
            val destination = markerLocations[markerLocations.size - 1]
            val waypoints = markerLocations.subList(1, markerLocations.size - 1)
            val url = getDirectionURL(origin, destination, waypoints)

            // Panggil coroutine untuk mendapatkan data rute
            lifecycleScope.launch {
                val (path, info) = getDirection(url)
                val (distance, duration) = info
                drawPolyline(path)
                binding.tvDistance.text = (distance)
                binding.tvTime.text = (duration)
            }
        }
    }
    private fun getDirectionURL(origin: LatLng, dest: LatLng, waypoints: List<LatLng>? = null): String {
        val waypointsParam = if (waypoints != null && waypoints.isNotEmpty()) {
            waypoints.joinToString("|") { "${it.latitude},${it.longitude}" }
        } else {
            ""
        }
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" + "&waypoints=$waypointsParam" +
                "&sensor=false&mode=driving&key=${BuildConfig.MAPS_API_KEY}"
    }

    private suspend fun getDirection(url: String): Pair<List<LatLng>, Pair<String, String>> {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body?.string() ?: ""
            val result = ArrayList<LatLng>()
            var distance = 0
            var duration = 0

            Log.d("API Response", data)
            try {
                val respObj = Gson().fromJson(data, RouteResponse::class.java)
                val legs = respObj.routes[0].legs

                // Ambil jarak dan waktu
                for (leg in legs) {
                    distance += leg.distance.value
                    duration += leg.duration.value
                    for (step in leg.steps) {
                        result.addAll(decodePolyline(step.polyline.points))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val distanceText = formatDistance(distance)
            val durationText = formatDuration(duration)
            Pair(result, Pair(distanceText, durationText))
        }
    }
    private fun formatDistance(distanceInMeters: Int): String {
        return if (distanceInMeters >= 1000) {
            String.format("%.1f km", distanceInMeters / 1000.0)
        } else {
            // Jarak dalam meter jika kurang dari 1000 meter
            "$distanceInMeters m"
        }
    }

    private fun formatDuration(durationInSeconds: Int): String {
        val hours = durationInSeconds / 3600
        val minutes = (durationInSeconds % 3600) / 60
        val seconds = durationInSeconds % 60

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
    private fun drawPolyline(path: List<LatLng>) {
        val color = ContextCompat.getColor(this, R.color.lightblue)
        val lineOption = PolylineOptions().apply {
            width(20f)
            color(color)
            geodesic(true)
        }

        lineOption.addAll(path)
        mMap.addPolyline(lineOption)
    }

    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(latLng)
        }

        return poly
    }

    private fun createNumberedMarker(context: Context, number: Int): BitmapDescriptor {
        val markerDrawable = ContextCompat.getDrawable(context, R.drawable.ic_default_marker)
        val width = markerDrawable?.intrinsicWidth ?: 0
        val height = markerDrawable?.intrinsicHeight ?: 0

        // Buat bitmap dengan ukuran marker
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Gambar marker ke dalam canvas
        markerDrawable?.setBounds(0, 0, width, height)
        markerDrawable?.draw(canvas)

        // Tambahkan nomor ke marker
        val paint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.darkblue)
            textSize = width /2f // Ukuran teks, sesuaikan dengan ukuran marker
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(ResourcesCompat.getFont(context, R.font.mulish_extra_bold), Typeface.BOLD)
        }

        // Hitung posisi teks agar berada di tengah marker
        val xPos = width / 2f
        val yPos = height / 2f - (paint.descent() + paint.ascent()) / 2f - height / 8f

        // Gambar nomor di atas marker
        canvas.drawText(number.toString(), xPos, yPos, paint)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val indonesiaBounds = LatLngBounds(
            LatLng(-11.0, 95.0),
            LatLng(6.0, 141.0)
        )

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(indonesiaBounds, 100))

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-5.0, 115.0), 5f))
    }

}