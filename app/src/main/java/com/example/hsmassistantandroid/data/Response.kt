package com.example.hsmassistantandroid.data

data class ResponseBody1 (
    val token: String,
    val cid: String,
    val pwd_expired: String)

data class ResponseBody2 (
    val objs: List<String>)