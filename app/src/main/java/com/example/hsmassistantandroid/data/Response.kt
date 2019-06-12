package com.example.hsmassistantandroid.data

//Usado quando a resposta é vazia
data class ResponseBody0 (
    val nothing: String
)

data class ResponseBody1 (
    val token: String,
    val cid: String,
    val pwd_expired: String)

data class ResponseBody2 (
    val obj: List<String>)

data class ResponseBody3 (
    val probe_str: String)

data class ResponseBody4 (
    val usr: List<String>)

data class item (
    val usr: String,
    val acl: Int)

data class ResponseBody5 (
    val trust: List<item>)

data class ResponseBody6 (
    val acl: Int)