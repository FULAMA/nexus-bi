package com.example.nexusbi.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class CurrencyRate(
    val code: String,
    val name: String,
    val symbol: String,
    val rateToUsd: Double // 1 USD = X Currency
)

object CurrencyService {
    // Default fallback rates relative to USD
    // 1 USD = 600 FCFA / XAF
    // 1 USD = 2850 CDF
    // 1 USD = 0.92 EUR
    // 1 USD = 1.0 USD
    private val defaultRates = mapOf(
        "USD" to 1.0,
        "FCFA" to 600.0,
        "CDF" to 2850.0,
        "EUR" to 0.92
    )

    private var cachedRates: Map<String, Double> = defaultRates
    private var lastFetchTime: Long = 0

    suspend fun getExchangeRates(): Map<String, Double> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        // Refresh cache every 1 hour
        if (now - lastFetchTime < 3600_000 && cachedRates != defaultRates) {
            return@withContext cachedRates
        }

        try {
            val url = URL("https://open.er-api.com/v6/latest/USD")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == 200) {
                val jsonString = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObj = JSONObject(jsonString)
                val ratesObj = jsonObj.getJSONObject("rates")

                val usdToXaf = ratesObj.optDouble("XAF", 600.0)
                val usdToCdf = ratesObj.optDouble("CDF", 2850.0)
                val usdToEur = ratesObj.optDouble("EUR", 0.92)

                val newRates = mapOf(
                    "USD" to 1.0,
                    "FCFA" to if (usdToXaf > 0) usdToXaf else 600.0,
                    "CDF" to if (usdToCdf > 0) usdToCdf else 2850.0,
                    "EUR" to if (usdToEur > 0) usdToEur else 0.92
                )
                cachedRates = newRates
                lastFetchTime = now
                return@withContext newRates
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext cachedRates
    }

    /**
     * Converts an amount in FCFA to the target currency.
     */
    fun convertFromFcfa(
        amountFcfa: Double,
        targetCurrency: String,
        rates: Map<String, Double> = cachedRates
    ): Double {
        if (targetCurrency == "FCFA") return amountFcfa
        val fcfaPerUsd = rates["FCFA"] ?: 600.0
        val targetPerUsd = rates[targetCurrency] ?: 1.0

        val amountUsd = amountFcfa / fcfaPerUsd
        return amountUsd * targetPerUsd
    }

    fun formatAmount(
        amountFcfa: Double,
        currencyCode: String,
        rates: Map<String, Double> = cachedRates
    ): String {
        val converted = convertFromFcfa(amountFcfa, currencyCode, rates)
        return when (currencyCode) {
            "FCFA" -> "${String.format("%,.0f", converted)} FCFA"
            "USD" -> "${String.format("%,.2f", converted)} $"
            "CDF" -> "${String.format("%,.0f", converted)} FC"
            "EUR" -> "${String.format("%,.2f", converted)} €"
            else -> "${String.format("%,.2f", converted)} $currencyCode"
        }
    }
}
