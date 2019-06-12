package com.example.hsmassistantandroid.api

import com.example.hsmassistantandroid.data.ResponseBody0
import com.example.hsmassistantandroid.data.ResponseBody2
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface usuarioEndPoint {
    @POST("change_pwd")
    @Headers("Content-type:application/json")
    fun changePwd(@Body request: RequestBody, @Header("Authorization") token: String?): Call<ResponseBody0>

    @POST("create_usr")
    @Headers("Content-type:application/json")
    fun createUsr(@Header("Authorization") token: String?): Call<ResponseBody2>

    @GET("list_usrs")
    @Headers("Content-type:application/json")
    fun listUsrs(@Header("Authorization") token: String?): Call<ResponseBody2>

    @POST("list_usr_trust")
    @Headers("Content-type:application/json")
    fun listUsersTrust(@Header("Authorization") token: String?): Call<ResponseBody2>

    @POST("update_acl")
    @Headers("Content-type:application/json")
    fun updateAcl(@Header("Authorization") token: String?): Call<ResponseBody2>

    @POST("get_acl")
    @Headers("Content-type:application/json")
    fun getAcl(@Header("Authorization") token: String?): Call<ResponseBody2>
}

//switch self {
//    case .changePwd:
//    return "change_pwd"
//    case .createUsr:
//    return "create_usr"
//    case .listUsrs:
//    return "list_usrs"
//    case .listUsrTrust:
//    return "list_usr_trust"
//    case .updateAcl:
//    return "update_acl"
//    case .getAcl:
//    return "get_acl"
//}