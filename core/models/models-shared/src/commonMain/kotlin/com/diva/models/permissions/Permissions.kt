package com.diva.models.permissions

sealed class Permissions(val name: String) {
    sealed class Write(name: String) : Permissions(name) {
        data object CreateUser : Write("create_user")
        data object UpdateUser : Write("update_user")
        data object DeleteUser : Write("delete_user")
    }
}
