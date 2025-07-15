package com.harian.raweng_basketbaall_gameschedule

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.harian.raweng_basketbaall_gameschedule.model.Game
import com.harian.raweng_basketbaall_gameschedule.model.Team
import com.harian.raweng_basketbaall_gameschedule.util.JsonUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.collections.filter

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val _schedules = MutableStateFlow<List<Game>>(emptyList())
    val schedules: StateFlow<List<Game>> = _schedules.asStateFlow()

    private val _filteredSchedules = MutableStateFlow<List<Game>>(emptyList())
    val filteredSchedules: StateFlow<List<Game>> = _filteredSchedules.asStateFlow()

    private val _teams = MutableStateFlow<Map<String, Team>>(emptyMap())
    val teams: StateFlow<Map<String, Team>> = _teams.asStateFlow()

    var searchQuery by mutableStateOf("")

    init {
        loadSchedulesAndTeams()
    }

    private fun loadSchedulesAndTeams() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val gameList = JsonUtils.loadGamesFromAssets(context, "Schedule.json")
            val teamList = JsonUtils.loadTeamsFromAssets(context, "teams.json")
            _schedules.value = gameList
            _filteredSchedules.value = gameList
            _teams.value = teamList.associateBy { it.tid }
        }
    }

    fun filterSchedules() {
        val query = searchQuery.trim().lowercase()
        _filteredSchedules.value = if (query.isEmpty()) {
            _schedules.value
        } else {
            _schedules.value.filter { game ->
                val arena = game.arenaName.lowercase()
                val city = game.arenaCity.lowercase()
                val home = game.homeTeam.tc.lowercase() + game.homeTeam.tn.lowercase()
                val visitor = game.visitorTeam.tc.lowercase() + game.visitorTeam.tn.lowercase()
                arena.contains(query) || city.contains(query) || home.contains(query) || visitor.contains(query)
            }
        }
    }
}