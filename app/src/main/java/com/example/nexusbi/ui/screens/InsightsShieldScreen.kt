package com.example.nexusbi.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexusbi.data.api.CommodityItem
import com.example.nexusbi.data.api.CurrencyService
import com.example.nexusbi.ui.language.AppLanguage
import com.example.nexusbi.ui.language.LocalizedStrings
import com.example.nexusbi.ui.theme.*
import com.example.nexusbi.ui.viewmodel.CustomerWithBalance

@Composable
fun InsightsShieldScreen(
    currentLanguage: AppLanguage,
    currentCurrency: String = "FCFA",
    exchangeRates: Map<String, Double> = mapOf("USD" to 1.0, "FCFA" to 600.0, "CDF" to 2850.0, "EUR" to 0.92),
    marketCommodities: List<CommodityItem> = emptyList(),
    aiAdviceText: String = "",
    isAiLoading: Boolean = false,
    isInsuranceActive: Boolean,
    customersWithBalance: List<CustomerWithBalance>,
    onToggleInsurance: () -> Unit,
    onAskAi: (String) -> Unit = {},
    onRefreshMarketPrices: () -> Unit = {},
    onRefreshExchangeRates: () -> Unit = {}
) {
    val context = LocalContext.current
    var customAiPrompt by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Title
        item {
            Text(
                text = LocalizedStrings.get("insights_title", currentLanguage),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SlateDark
            )
        }

        // 🤖 API 1: Gemini AI Commerçant Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "AI",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = LocalizedStrings.get("ai_advisor_title", currentLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SlateDark
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldLight
                        ) {
                            Text(
                                text = "Gemini 3.5 Flash",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Posez une question à votre assistant commercial IA pour optimiser la trésorerie et la gestion des créances.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateGray
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick AI Prompts
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { onAskAi("Comment réduire les impayés de crédit dans ma boutique ?") },
                                label = { Text("Réduire impayés", fontSize = 11.sp) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { onAskAi("Conseils pour négocier de meilleurs prix avec les grossistes") },
                                label = { Text("Négociation grossistes", fontSize = 11.sp) }
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { onAskAi("Quelle stratégie de relance adopter sans énerver les clients ?") },
                                label = { Text("Relance courtoise", fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Input & Send Row
                    OutlinedTextField(
                        value = customAiPrompt,
                        onValueChange = { customAiPrompt = it },
                        placeholder = { Text("Ex: Comment mieux gérer mon stock ce mois-ci ?", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (customAiPrompt.isNotBlank()) {
                                        onAskAi(customAiPrompt)
                                        customAiPrompt = ""
                                    }
                                },
                                enabled = !isAiLoading && customAiPrompt.isNotBlank()
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send", tint = EmeraldPrimary)
                            }
                        }
                    )

                    if (isAiLoading) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = EmeraldPrimary, strokeWidth = 2.dp)
                            Text("Gemini analyse vos données...", fontSize = 12.sp, color = SlateGray)
                        }
                    } else if (aiAdviceText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(EmeraldLight)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = aiAdviceText,
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateDark,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        // 📈 API 4: Market Commodity Prices Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = LocalizedStrings.get("market_prices_title", currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SlateDark
                        )

                        IconButton(
                            onClick = { onRefreshMarketPrices() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = EmeraldPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    marketCommodities.forEach { commodity ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = commodity.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateDark
                                )
                                Text(
                                    text = commodity.unit,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SlateGray
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = CurrencyService.formatAmount(commodity.currentPriceFcfa, currentCurrency, exchangeRates),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateDark
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = if (commodity.isUp) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Trend",
                                        tint = if (commodity.isUp) RedNegative else GreenPositive,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = "${if (commodity.isUp) "+" else ""}${commodity.changePercent}%",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (commodity.isUp) RedNegative else GreenPositive
                                    )
                                }
                            }
                        }
                        HorizontalDivider(color = LightBorder)
                    }
                }
            }
        }

        // 💱 API 3: Live Exchange Rates & Converter Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = LocalizedStrings.get("currency_converter_title", currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SlateDark
                        )

                        IconButton(
                            onClick = { onRefreshExchangeRates() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = EmeraldPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Taux officiels en direct (Base USD / FCFA / CDF / EUR) :",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateGray
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val fcfaRate = exchangeRates["FCFA"] ?: 600.0
                    val cdfRate = exchangeRates["CDF"] ?: 2850.0
                    val eurRate = exchangeRates["EUR"] ?: 0.92

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldLight,
                            modifier = Modifier.weight(1f).padding(end = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("1 USD", fontSize = 10.sp, color = SlateGray)
                                Text("${String.format("%,.0f", fcfaRate)} FCFA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SoftSand,
                            modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("1 USD", fontSize = 10.sp, color = SlateGray)
                                Text("${String.format("%,.0f", cdfRate)} CDF", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SlateDark)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = TerracottaGold.copy(alpha = 0.15f),
                            modifier = Modifier.weight(1f).padding(start = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("1 EUR", fontSize = 10.sp, color = SlateGray)
                                Text("${String.format("%,.0f", fcfaRate / eurRate)} FCFA", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TerracottaGold)
                            }
                        }
                    }
                }
            }
        }

        // Export Carnet CSV Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sauvegarde & Exportation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Téléchargez ou partagez la liste complète de vos crédits et débiteurs au format CSV.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val csvHeader = "ID,Nom,Téléphone,Quartier,Total_Crédit_FCFA,Total_Payé_FCFA,Solde_Dû_FCFA\n"
                            val csvRows = customersWithBalance.joinToString("\n") { c ->
                                "${c.customer.id},\"${c.customer.name}\",\"${c.customer.phone}\",\"${c.customer.neighborhood}\",${c.totalDebt},${c.totalPaid},${c.netBalance}"
                            }
                            val fullCsv = csvHeader + csvRows

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/csv"
                                putExtra(Intent.EXTRA_SUBJECT, "Exportation Carnet Kredi - CSV")
                                putExtra(Intent.EXTRA_TEXT, fullCsv)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Exporter le fichier CSV via"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("export_csv_carnet_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Export")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = LocalizedStrings.get("export_csv_btn", currentLanguage),
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }
                }
            }
        }

        // Micro-Insurance Shield Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = if (isInsuranceActive) EmeraldPrimary else PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Shield",
                                tint = if (isInsuranceActive) TerracottaGold else EmeraldPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = "Crédit Shield ($1/mois)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isInsuranceActive) PureWhite else SlateDark
                            )
                        }

                        Switch(
                            checked = isInsuranceActive,
                            onCheckedChange = { onToggleInsurance() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = TerracottaGold
                            ),
                            modifier = Modifier.testTag("insurance_toggle_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = LocalizedStrings.get("insurance_desc", currentLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isInsuranceActive) Color(0xFFA7F3D0) else SlateGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isInsuranceActive) Color(0xFF00382C)
                                else EmeraldLight
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (isInsuranceActive) LocalizedStrings.get("insurance_active", currentLanguage) else LocalizedStrings.get("insurance_inactive", currentLanguage),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isInsuranceActive) Color(0xFFA7F3D0) else EmeraldPrimary
                        )
                    }
                }
            }
        }
    }
}

