package com.example.hsmassistantandroid.api

import com.example.hsmassistantandroid.data.ResponseBody2
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface objetosEndPoint {
    @GET("list_objs")
    fun listObjs(@Header("Authorization") token: String?): Call<ResponseBody2>
}