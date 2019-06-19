package com.example.hsmassistantandroid.api

import com.example.hsmassistantandroid.data.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface usuarioEndPoint {
    @POST("change_pwd")
    @Headers("Content-type:application/json")
    fun changePwd(@Body request: RequestBody, @Header("Authorization") token: String?): Call<ResponseBody0>

    @POST("create_usr")
    @Headers("Content-type:application/json")
    fun createUsr(@Body request: RequestBody, @Header("Authorization") token: String?): Call<ResponseBody>

    @GET("list_usrs")
    fun listUsrs(@Header("Authorization") token: String?): Call<ResponseBody4>

    @POST("list_usr_trust")
    @Headers("Content-type:application/json")
    fun listUsersTrust(@Body request: RequestBody, @Header("Authorization") token: String?): Call<ResponseBody5>

    @POST("update_acl")
    @Headers("Content-type:application/json")
    fun updateAcl(@Body request: RequestBody, @Header("Authorization") token: String?): Call<ResponseBody0>

    @POST("get_acl")
    @Headers("Content-type:application/json")
    fun getAcl(@Body request: RequestBody, @Header("Authorization") token: String?): Call<ResponseBody6>
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