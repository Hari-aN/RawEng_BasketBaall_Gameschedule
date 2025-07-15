package com.harian.raweng_basketbaall_gameschedule.util

import android.content.Context
import com.harian.raweng_basketbaall_gameschedule.model.Game
import com.harian.raweng_basketbaall_gameschedule.model.Team
import kotlinx.serialization.json.*

object JsonUtils {
    private val jsonParser = Json { ignoreUnknownKeys = true }

    fun loadGamesFromAssets(context: Context, filename: String): List<Game> {
        return try {
            val jsonString = context.assets.open(filename).bufferedReader().use { it.readText() }
            val scheduleArray =
                Json.parseToJsonElement(jsonString).jsonObject["data"]?.jsonObject?.get("schedules")
                    ?: return emptyList()
            jsonParser.decodeFromJsonElement(scheduleArray)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun loadTeamsFromAssets(context: Context, filename: String): List<Team> {
        return try {
            val jsonString = context.assets.open(filename).bufferedReader().use { it.readText() }
            val teamsArray = Json.parseToJsonElement(jsonString)
                .jsonObject["data"]?.jsonObject?.get("teams") ?: return emptyList()
            jsonParser.decodeFromJsonElement(teamsArray)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}