package com.harian.raweng_basketbaall_gameschedule.model

import kotlinx.serialization.Serializable

@Serializable
data class Team(
    val tid: String,
    val tn: String,
    val ta: String,
    val tc: String,
    val logo: String,
    val color: String
)