package com.playaxis.currencyapp.ui.history_screen

import android.util.Log
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import com.playaxis.currencyapp.ui.main.CurrencyDropdown
import com.playaxis.currencyapp.ui.main.MainVm
import com.playaxis.currencyapp.utils.NetworkResult
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.Line

@Composable
fun HistoryScreen(
    viewModel: HistoryVm,
    sharedVm: MainVm
) {

    var selectedCurrency by remember { mutableStateOf("USD") }
    val historyRates by viewModel.historyRates.observeAsState()

    LaunchedEffect(selectedCurrency) {
        viewModel.getHistoryRates(selectedCurrency)
    }

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
                viewModel.getHistoryRates(newCurrency)
            }
        )
        Spacer(modifier = Modifier.height(50.dp))
        when (historyRates) {
            is NetworkResult.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            is NetworkResult.Success -> {
                val rates = (historyRates as NetworkResult.Success<List<Double>>).data
                Log.e("History Screen", "HistoryScreen: ${rates?.size}" )
                LineChart(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(0.4f)
                        .padding(horizontal = 22.dp),
                    data = listOf(
                        Line(
                            label = selectedCurrency,
                            values = rates!!,
                            color = SolidColor(Color(0xFF23af92)),
                            firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                            secondGradientFillColor = Color.Transparent,
                            strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                            gradientAnimationDelay = 1000,
                            drawStyle = DrawStyle.Stroke(width = 2.dp),
                        )
                    ),
                    animationMode = AnimationMode.Together(delayBuilder = {
                        it * 500L
                    }),
                )
            }

            is NetworkResult.Error -> {
                Text(
                    text = "Error: ${(historyRates as NetworkResult.Error).message}",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            null -> {}
        }
    }
}