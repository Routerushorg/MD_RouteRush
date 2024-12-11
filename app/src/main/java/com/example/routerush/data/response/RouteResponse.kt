package com.example.routerush.data.response

import com.google.gson.annotations.SerializedName


data class RouteResponse(
    @SerializedName("routes") val routes: ArrayList<Routes> = ArrayList()
)

data class Routes(
    @SerializedName("legs") val legs: ArrayList<Legs> = ArrayList()
)

data class Legs(
    @SerializedName("distance") val distance: Distance = Distance(),
    @SerializedName("duration") val duration: Duration = Duration(),
    @SerializedName("end_address") val endAddress: String = "",
    @SerializedName("start_address") val startAddress: String = "",
    @SerializedName("end_location") val endLocation: Location = Location(),
    @SerializedName("start_location") val startLocation: Location = Location(),
    @SerializedName("steps") val steps: ArrayList<Steps> = ArrayList()
)

data class Steps(
    @SerializedName("distance") val distance: Distance = Distance(),
    @SerializedName("duration") val duration: Duration = Duration(),
    @SerializedName("end_address") val endAddress: String = "",
    @SerializedName("start_address") val startAddress: String = "",
    @SerializedName("end_location") val endLocation: Location = Location(),
    @SerializedName("start_location") val startLocation: Location = Location(),
    @SerializedName("polyline") val polyline: PolyLine = PolyLine(),
    @SerializedName("travel_mode") val travelMode: String = "",
    @SerializedName("maneuver") val maneuver: String = ""
)

data class Duration(
    @SerializedName("text") val text: String = "",
    @SerializedName("value") val value: Int = 0
)

data class Distance(
    @SerializedName("text") val text: String = "",
    @SerializedName("value") val value: Int = 0
)

data class PolyLine(
    @SerializedName("points") val points: String = ""
)

data class Location(
    @SerializedName("lat") val lat: String = "",
    @SerializedName("lng") val lng: String = ""
)