package com.example.hsmassistantandroid.api

import com.example.hsmassistantandroid.data.ResponseBody1
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface sessaoEndPoint {

    @POST("close")
    @Headers("Content-type:application/json")
    fun close(@Body request: RequestBody, @Header("Authorization") token: String?): Call<ResponseBody1>

    @POST("auth")
    @Headers("Content-type:application/json")
    fun auth(@Body request: RequestBody): Call<ResponseBody1>
}