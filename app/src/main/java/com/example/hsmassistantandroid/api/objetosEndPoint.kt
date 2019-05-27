package com.example.hsmassistantandroid.api

import com.example.hsmassistantandroid.data.ResponseBody1
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface objetosEndPoint {
    @GET("list_objs")
    @Headers("Content-type:application/json", "Authorization:{token}")
    fun listObjs(@Header("token") token: String?): Call<ResponseBody1>
}