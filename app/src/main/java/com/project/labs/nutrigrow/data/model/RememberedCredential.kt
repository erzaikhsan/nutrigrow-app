package com.project.labs.nutrigrow.data.model

data class RememberedCredential(
    val email: String,
    val password: String,
    val remember: Boolean,
)
