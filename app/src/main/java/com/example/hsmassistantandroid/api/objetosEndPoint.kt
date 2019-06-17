package com.example.hsmassistantandroid.api

import com.example.hsmassistantandroid.data.ResponseBody2
import com.example.hsmassistantandroid.data.ResponseBody5
import com.example.hsmassistantandroid.data.ResponseBody7
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface objetosEndPoint {
    @GET("list_objs")
    fun listObjs(@Header("Authorization") token: String?): Call<ResponseBody2>

    @POST("get_obj_info")
    @Headers("Content-type:application/json")
    fun getObjInfo(@Body request: RequestBody, @Header("Authorization") token: String?): Call<ResponseBody7>

    @POST("obj_exp")
    @Headers("Content-type:application/json")
    fun objExp(@Body request: RequestBody, @Header("Authorization") token: String?): Call<ResponseBody>

}