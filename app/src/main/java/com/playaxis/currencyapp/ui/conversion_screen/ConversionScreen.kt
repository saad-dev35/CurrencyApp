package com.playaxis.currencyapp.ui.conversion_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playaxis.currencyapp.ui.main.CurrencyDropdown
import com.playaxis.currencyapp.ui.main.MainVm
import com.playaxis.currencyapp.utils.NetworkResult

@Composable
fun ConversionScreen(
    viewModel: ConversionVm,
    sharedVm: MainVm,
    onNavigateBack: () -> Unit
) {
    // Remembering user input for amount and selected currencies
    var amountInput by remember { mutableStateOf("") }
    var sourceCurrency by remember { mutableStateOf("USD") }
    var targetCurrency by remember { mutableStateOf("EUR") }

    //LiveData that observes converted amount
    val convertedAmount by viewModel.conversionRate.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text("Currency Conversion", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Amount Input Field
        OutlinedTextField(
            value = amountInput,
            onValueChange = {
                amountInput = it
                if (it.isNotEmpty())
                    viewModel.getConversionRate(
                        amount = amountInput,
                        base = sourceCurrency,
                        target = targetCurrency
                    )
            },
            label = { Text("Enter Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Source Currency Dropdown
        CurrencyDropdown(
            viewModel = sharedVm,
            label = "Source Currency",
            selectedCurrency = sourceCurrency,
            onCurrencySelected = {
                sourceCurrency = it
                if (amountInput.isNotEmpty())
                    viewModel.getConversionRate(amountInput, sourceCurrency, targetCurrency)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Target Currency Dropdown
        CurrencyDropdown(
            viewModel = sharedVm,
            label = "Target Currency",
            selectedCurrency = targetCurrency,
            onCurrencySelected = {
                targetCurrency = it
                if (amountInput.isNotEmpty())
                    viewModel.getConversionRate(amountInput, sourceCurrency, targetCurrency)
            }
        )

        // Display the conversion result
        when (convertedAmount) {
            is NetworkResult.Loading -> {
                CircularProgressIndicator()
            }

            is NetworkResult.Success -> {
                val amount = convertedAmount?.data?.conversionResult ?: "0.00"
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Conversion Rate: ${convertedAmount?.data?.conversionRate ?: "0.00"}",
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Converted Amount: $amount",
                    fontSize = 18.sp
                )
            }

            is NetworkResult.Error -> {
                Text(
                    text = "Error: ${convertedAmount?.message}",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> {
                Text(text = "Enter an amount to convert", fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}