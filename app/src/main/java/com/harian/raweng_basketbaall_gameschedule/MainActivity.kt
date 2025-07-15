package com.harian.raweng_basketbaall_gameschedule
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harian.raweng_basketbaall_gameschedule.ui.ScheduleScreen
import com.harian.raweng_basketbaall_gameschedule.ui.theme.RawEng_BasketBaall_GamescheduleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RawEng_BasketBaall_GamescheduleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val scheduleViewModel: ScheduleViewModel = viewModel()
                    Column(modifier = Modifier.fillMaxSize()) {
                        SearchBar(
                            query = scheduleViewModel.searchQuery,
                            onQueryChange = { scheduleViewModel.searchQuery = it },
                            onSearch = { scheduleViewModel.filterSchedules() }
                        )
                        ScheduleScreen(viewModel = scheduleViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, onSearch: () -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = {
            onQueryChange(it)
            onSearch()
        },
        label = { Text("Search arena, city, or team") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}