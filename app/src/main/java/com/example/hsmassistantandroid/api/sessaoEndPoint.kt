package com.example.hsmassistantandroid.api

import com.example.hsmassistantandroid.data.ResponseBody0
import com.example.hsmassistantandroid.data.ResponseBody1
import com.example.hsmassistantandroid.data.ResponseBody3
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface sessaoEndPoint {

    @POST("close")
    @Headers("Content-type:application/json")
    fun close(@Header("Authorization") token: String?): Call<ResponseBody0>

    @POST("auth")
    @Headers("Content-type:application/json")
    fun auth(@Body request: RequestBody): Call<ResponseBody1>

    @GET("probe")
    fun probe(@Header("Authorization") token: String?): Call<ResponseBody3>
}