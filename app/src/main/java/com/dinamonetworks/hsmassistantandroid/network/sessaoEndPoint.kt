package com.dinamonetworks.hsmassistantandroid.network

import com.dinamonetworks.hsmassistantandroid.data.ResponseBody0
import com.dinamonetworks.hsmassistantandroid.data.ResponseBody1
import com.dinamonetworks.hsmassistantandroid.data.ResponseBody3
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface sessaoEndPoint {

    @POST("close")
    @Headers("Content-type:application/json")
    fun close(@Body request: RequestBody, @Header("Authorization") token: String?): Call<ResponseBody0>

    @POST("auth")
    @Headers("Content-type:application/json")
    fun auth(@Body request: RequestBody): Call<ResponseBody1>

    @GET("probe")
    fun probe(@Header("Authorization") token: String?): Call<ResponseBody3>

}