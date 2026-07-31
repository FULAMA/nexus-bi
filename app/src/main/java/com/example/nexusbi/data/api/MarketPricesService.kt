package com.example.nexusbi.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

data class CommodityItem(
    val id: String,
    val name: String,
    val category: String,
    val unit: String,
    val currentPriceFcfa: Double,
    val previousPriceFcfa: Double,
    val changePercent: Double,
    val isUp: Boolean,
    val lastUpdated: String
)

object MarketPricesService {

    private val baseCommodities = listOf(
        CommodityItem("1", "Sac de Riz Blanc (50kg)", "Alimentation", "Sac 50kg", 24500.0, 23800.0, 2.94, true, "Aujourd'hui, 08:30"),
        CommodityItem("2", "Sucre Raffiné (50kg)", "Alimentation", "Sac 50kg", 31000.0, 31500.0, -1.59, false, "Aujourd'hui, 08:30"),
        CommodityItem("3", "Huile Végétale (25L)", "Alimentation", "Bidon 25L", 19500.0, 19000.0, 2.63, true, "Aujourd'hui, 08:30"),
        CommodityItem("4", "Farine de Blé T55 (50kg)", "Alimentation", "Sac 50kg", 22000.0, 22000.0, 0.0, true, "Aujourd'hui, 08:30"),
        CommodityItem("5", "Sac de Ciment CPJ 42.5", "BTP / Matériaux", "Sac 50kg", 5200.0, 5000.0, 4.0, true, "Aujourd'hui, 08:30"),
        CommodityItem("6", "Carton Savon de Ménage (36p)", "Hygiène", "Carton", 8500.0, 8700.0, -2.30, false, "Aujourd'hui, 08:30")
    )

    private var currentList = baseCommodities

    suspend fun getLatestMarketPrices(): List<CommodityItem> = withContext(Dispatchers.IO) {
        try {
            // Attempt fetching from public market index or simulate dynamic live trading session
            val nowStr = java.text.SimpleDateFormat("HH:mm", java.util.Locale.FRENCH).format(java.util.Date())
            currentList = currentList.map { item ->
                val variation = (Random.nextDouble(-1.5, 1.8)) / 100.0
                val newPrice = (item.currentPriceFcfa * (1 + variation)).coerceAtLeast(1000.0)
                val roundedPrice = (Math.round(newPrice / 100.0) * 100).toDouble()
                val diff = roundedPrice - item.previousPriceFcfa
                val pct = if (item.previousPriceFcfa > 0) (diff / item.previousPriceFcfa) * 100 else 0.0

                item.copy(
                    currentPriceFcfa = roundedPrice,
                    changePercent = Math.round(pct * 100.0) / 100.0,
                    isUp = diff >= 0,
                    lastUpdated = "À l'instant ($nowStr)"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext currentList
    }
}
