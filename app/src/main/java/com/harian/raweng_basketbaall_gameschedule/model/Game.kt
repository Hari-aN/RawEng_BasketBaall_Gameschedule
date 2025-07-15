package com.harian.raweng_basketbaall_gameschedule.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeamInfo(
    val tid: String,
    val ta: String,
    val tn: String,
    val tc: String
)

@Serializable
data class Game(
    val gid: String,
    val gametime: String,
    @SerialName("arena_name") val arenaName: String,
    @SerialName("arena_city") val arenaCity: String,
    @SerialName("st") val status: Int,
    val h: TeamInfo,
    val v: TeamInfo
) {
    val homeTeam: TeamInfo get() = h
    val visitorTeam: TeamInfo get() = v
}