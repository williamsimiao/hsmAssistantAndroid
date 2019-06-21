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

data class ResponseBody7 (
    val version: Int,
    val type: Int,
    val attr: Int)

data class aclStruct(val rawValue: Int) {
    val obj_del: Int
        get() = rawValue shl 0
    val obj_read: Int
        get() = rawValue shl 1
    val obj_create: Int
        get() = rawValue shl 2
    val obj_update: Int
        get() = rawValue shl 3
    val usr_write: Int
        get() = obj_update
    val usr_create: Int
        get() = rawValue shl 4
    val usr_delete: Int
        get() = usr_create
    val usr_remote_log: Int
        get() = rawValue shl 5
    val usr_list: Int
        get() = rawValue shl 6
    val sys_operator: Int
        get() = rawValue shl 7
    val sys_backup: Int
        get() = rawValue shl 8
    val sys_restore: Int
        get() = sys_backup
    val sys_update_hsm: Int
        get() = rawValue shl 9
    val ns_authorization: Int
        get() = rawValue shl 10
    val virtual_x509_auth: Int
        get() = rawValue shl 28
    val virtual_otp_auth: Int
        get() = rawValue shl 29
    val change_pwd_next_time: Int
        get() = rawValue shl 30
}

//let rawValue: UInt32
//static let  =            aclStruct(rawValue: 1 << 0)
//static let  =           aclStruct(rawValue: 1 << 1)
//static let  =         aclStruct(rawValue: 1 << 2)
//static let  =         aclStruct(rawValue: 1 << 3)
//static let  =          obj_update
//static let  =         aclStruct(rawValue: 1 << 4)
//static let  =         usr_create
//static let  =     aclStruct(rawValue: 1 << 5)
//static let  =           aclStruct(rawValue: 1 << 6)
//static let  =       aclStruct(rawValue: 1 << 7)
//static let  =         aclStruct(rawValue: 1 << 8)
//static let  =        sys_backup
//static let  =     aclStruct(rawValue: 1 << 9)
//static let  =   aclStruct(rawValue: 1 << 10)
//static let  =  aclStruct(rawValue: 1 << 28)
//static let  =   aclStruct(rawValue: 1 << 29)
//static let  = aclStruct(rawValue: 1 << 30)