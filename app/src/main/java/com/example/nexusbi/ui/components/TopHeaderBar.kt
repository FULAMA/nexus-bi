package com.example.nexusbi.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexusbi.ui.language.AppLanguage
import com.example.nexusbi.ui.language.LocalizedStrings
import com.example.nexusbi.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopHeaderBar(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    currentCurrency: String = "FCFA",
    onCurrencySelected: (String) -> Unit = {},
    totalCreditDue: Double,
    totalSavings: Double,
    onOpenGuideAndUpdate: () -> Unit = {}
) {
    var languageMenuExpanded by remember { mutableStateOf(false) }
    var currencyMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        color = PureWhite,
        contentColor = SlateDark,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, LightBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = LocalizedStrings.get("app_title", currentLanguage),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SlateDark
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(EmeraldLight)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Offline Mode",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Hors-ligne",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldPrimary
                                )
                            }
                        }
                    }
                    Text(
                        text = LocalizedStrings.get("tagline", currentLanguage),
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateGray
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Currency Selector Pill
                    Box {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF3F4F6),
                            border = BorderStroke(1.dp, LightBorder),
                            modifier = Modifier
                                .testTag("currency_selector_btn")
                                .clickable { currencyMenuExpanded = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = currentCurrency,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SlateDark,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Currency",
                                    tint = SlateGray
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = currencyMenuExpanded,
                            onDismissRequest = { currencyMenuExpanded = false }
                        ) {
                            listOf("FCFA", "USD", "CDF", "EUR").forEach { curr ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = curr,
                                            fontWeight = if (curr == currentCurrency) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        onCurrencySelected(curr)
                                        currencyMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Language Selector Pill
                    Box {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF3F4F6),
                            border = BorderStroke(1.dp, LightBorder),
                            modifier = Modifier
                                .testTag("language_selector_btn")
                                .clickable { languageMenuExpanded = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = currentLanguage.flag, fontSize = 16.sp)
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = SlateGray
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = languageMenuExpanded,
                            onDismissRequest = { languageMenuExpanded = false }
                        ) {
                            AppLanguage.values().forEach { lang ->
                                DropdownMenuItem(
                                    text = {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text(text = lang.flag)
                                            Text(
                                                text = lang.displayName,
                                                fontWeight = if (lang == currentLanguage) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                    },
                                    onClick = {
                                        onLanguageSelected(lang)
                                        languageMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Guide & Update Button Pill
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF3F4F6),
                        border = BorderStroke(1.dp, LightBorder),
                        modifier = Modifier
                            .testTag("app_guide_update_btn")
                            .clickable { onOpenGuideAndUpdate() }
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Guide & Updates",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Total Credit Badge
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, RedNegative.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = LocalizedStrings.get("total_credit_due", currentLanguage),
                            style = MaterialTheme.typography.labelSmall,
                            color = RedNegative
                        )
                        Text(
                            text = "${String.format("%,.0f", totalCreditDue)} FCFA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = RedNegative
                        )
                    }
                }

                // Total Savings Badge
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = EmeraldLight,
                    border = BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = LocalizedStrings.get("group_savings_title", currentLanguage),
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldPrimary
                        )
                        Text(
                            text = "+${String.format("%,.0f", totalSavings)} FCFA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                }
            }
        }
    }
}
