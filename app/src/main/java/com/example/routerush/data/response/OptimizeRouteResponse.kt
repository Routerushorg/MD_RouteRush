package com.example.routerush.data.response

import com.google.gson.annotations.SerializedName

data class OptimizeRouteResponse(
    @SerializedName("optimizedRoute")
    val optimizedRoute: String
)