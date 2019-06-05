package com.example.hsmassistantandroid.extensions

fun handleNetworkResponse(responseCode: Int?): String {
    if(responseCode == null) {
        return "failed null"
    }
    when(responseCode) {
        in 200..299 -> return "sucess"
        in 401..500 -> return "authenticationError"
        in 501..599 -> return "badRequest"
        600 -> return "outdated"
        else -> return "failed"
    }
}
