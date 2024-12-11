package com.example.routerush.data.response

import com.google.gson.annotations.SerializedName

data class LoginAndRegisterResponse(

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("error")
	val error: Error? = null
)

data class User(

	@field:SerializedName("uid")
	val uid: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)
