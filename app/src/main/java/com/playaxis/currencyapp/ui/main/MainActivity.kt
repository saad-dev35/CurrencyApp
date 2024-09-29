package com.playaxis.currencyapp.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.playaxis.currencyapp.ui.conversion_screen.ConversionScreen
import com.playaxis.currencyapp.ui.conversion_screen.ConversionVm
import com.playaxis.currencyapp.ui.history_screen.HistoryScreen
import com.playaxis.currencyapp.ui.history_screen.HistoryVm
import com.playaxis.currencyapp.ui.home_screen.HomeScreen
import com.playaxis.currencyapp.ui.home_screen.HomeVm
import com.playaxis.currencyapp.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //shared viewmodel
    private val mainViewModel: MainVm by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = Screen.Home.route) {
                // Home Screen
                composable(Screen.Home.route) {
                    val homeViewModel: HomeVm = hiltViewModel()
                    HomeScreen(
                        viewModel = homeViewModel,
                        sharedVm = mainViewModel,
                        onNavigateToConversion = { navController.navigate(Screen.Conversion.route) },
                        onNavigateToHistory = { navController.navigate(Screen.History.route) }
                    )
                }

                // Conversion Screen
                composable(Screen.Conversion.route) {
                    val conversionViewModel: ConversionVm = hiltViewModel()
                    ConversionScreen(
                        viewModel = conversionViewModel,
                        sharedVm = mainViewModel,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }

                // History Screen
                composable(Screen.History.route) {
                    val historyViewModel: HistoryVm = hiltViewModel()
                    HistoryScreen(
                        viewModel = historyViewModel,
                        sharedVm = mainViewModel
                    )
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Conversion : Screen("conversion")
    data object History : Screen("history")
}

@Composable
fun CurrencyDropdown(
    viewModel: MainVm,
    label: String,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit
) {
    // Observe the LiveData
    val supportedCodesState by viewModel.supportedCodes.observeAsState()
    val currencies = when (supportedCodesState) {
        is NetworkResult.Success -> supportedCodesState?.data?.supportedCodes?.map { it }
        is NetworkResult.Error -> listOf()
        else -> listOf()
    }

    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedCurrency,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Currency Dropdown")
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currencies?.forEach { currency ->
                DropdownMenuItem(
                    text = { currency?.get(0)?.let { Text("$it (${currency[1]})") } },
                    onClick = {
                        currency?.get(0)?.let { onCurrencySelected(it) }
                        expanded = false
                    }
                )
            }
        }
    }
}