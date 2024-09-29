package com.playaxis.currencyapp.ui.home_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.playaxis.currencyapp.ui.main.CurrencyDropdown
import com.playaxis.currencyapp.ui.main.MainVm
import com.playaxis.currencyapp.utils.NetworkResult

@Composable
fun HomeScreen(
    viewModel: HomeVm,
    sharedVm: MainVm,
    onNavigateToConversion: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    var selectedCurrency by remember { mutableStateOf("USD") }

    // Trigger data fetch for the selected currency
    LaunchedEffect(selectedCurrency) {
        viewModel.getExchangeRates(selectedCurrency)
    }

    val exchangeRatesState by viewModel.exchangeRates.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(20.dp))
        // Currency Dropdown
        CurrencyDropdown(
            viewModel = sharedVm,
            label = "Select Currency",
            selectedCurrency = selectedCurrency,
            onCurrencySelected = { newCurrency ->
                selectedCurrency = newCurrency
                viewModel.getExchangeRates(newCurrency)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // LazyColumn to display exchange rates
        when (exchangeRatesState) {
            is NetworkResult.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            is NetworkResult.Success -> {
                val rates = exchangeRatesState?.data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(0.8f)
                ) {
                    rates?.let {
                        items(it.size) { index ->
                            val currency = rates[index].currencyCode
                            val rate = rates[index].rate
                            Text(
                                text = "$currency: $rate",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            is NetworkResult.Error -> {
                Text(
                    text = "Error: ${(exchangeRatesState as NetworkResult.Error).message}",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            null -> {

            }
        }

        Button(
            onClick = {
                onNavigateToConversion()
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Go to conversion screen")
        }

        Button(
            onClick = {
                onNavigateToHistory()
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Go to history screen")
        }
    }
}
