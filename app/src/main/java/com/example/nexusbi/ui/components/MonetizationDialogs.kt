package com.example.nexusbi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.nexusbi.ui.language.AppLanguage
import com.example.nexusbi.ui.language.LocalizedStrings
import com.example.nexusbi.ui.theme.*
import com.example.nexusbi.ui.viewmodel.SubscriptionPlan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpgradePremiumDialog(
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (SubscriptionPlan, String) -> Unit
) {
    var selectedPlan by remember { mutableStateOf(SubscriptionPlan.MONTHLY_PREMIUM) }
    var selectedProvider by remember { mutableStateOf("Orange Money") }
    var phoneNumber by remember { mutableStateOf("+243 89 123 4567") }

    val providers = listOf("Orange Money", "Wave", "M-Pesa", "MTN MoMo", "Carte Bancaire")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PureWhite,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("dialog_upgrade_premium")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Premium",
                        tint = TerracottaGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Souscrire à l'Abonnement Premium",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark
                    )
                }

                Text(
                    text = "Bénéficiez de toutes les fonctionnalités avancées & du boost de commission sur vos achats groupés.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateGray
                )

                HorizontalDivider(color = SoftSand)

                // Plan Selection Radio / Cards
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "1. Choisissez votre Forfait",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark
                    )

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (selectedPlan == SubscriptionPlan.MONTHLY_PREMIUM) EmeraldLight else SoftSand,
                        border = if (selectedPlan == SubscriptionPlan.MONTHLY_PREMIUM) androidx.compose.foundation.BorderStroke(2.dp, EmeraldPrimary) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPlan = SubscriptionPlan.MONTHLY_PREMIUM }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Premium Mensuel",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateDark
                                )
                                Text(
                                    text = "3 000 FCFA / mois ($5)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldPrimary
                                )
                            }
                            RadioButton(
                                selected = selectedPlan == SubscriptionPlan.MONTHLY_PREMIUM,
                                onClick = { selectedPlan = SubscriptionPlan.MONTHLY_PREMIUM }
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (selectedPlan == SubscriptionPlan.ANNUAL_PREMIUM) Color(0xFFFFFBEB) else SoftSand,
                        border = if (selectedPlan == SubscriptionPlan.ANNUAL_PREMIUM) androidx.compose.foundation.BorderStroke(2.dp, TerracottaGold) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPlan = SubscriptionPlan.ANNUAL_PREMIUM }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Premium Annuel",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateDark
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = TerracottaGold
                                    ) {
                                        Text(
                                            text = "2 mois offerts",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = SlateDark,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "30 000 FCFA / an ($50)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TerracottaGold
                                )
                            }
                            RadioButton(
                                selected = selectedPlan == SubscriptionPlan.ANNUAL_PREMIUM,
                                onClick = { selectedPlan = SubscriptionPlan.ANNUAL_PREMIUM }
                            )
                        }
                    }
                }

                // Mobile Money Operator Selection
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "2. Opérateur de Paiement Mobile Money",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        providers.take(4).forEach { provider ->
                            val isSelected = selectedProvider == provider
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) EmeraldPrimary else SoftSand,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedProvider = provider }
                            ) {
                                Text(
                                    text = provider,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) PureWhite else SlateDark,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Numéro Mobile Money / Téléphone") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = LocalizedStrings.get("cancel", currentLanguage))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onConfirm(selectedPlan, "$selectedProvider ($phoneNumber)")
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text(text = "Valider l'Abonnement")
                    }
                }
            }
        }
    }
}

@Composable
fun PayoutRequestDialog(
    availableBalance: Double,
    currentCurrency: String,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (Double, String, String) -> Unit
) {
    var amountText by remember { mutableStateOf(availableBalance.toInt().toString()) }
    var selectedProvider by remember { mutableStateOf("Orange Money") }
    var phoneNumber by remember { mutableStateOf("+243 89 123 4567") }

    val providers = listOf("Orange Money", "Wave", "M-Pesa", "MTN MoMo")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PureWhite,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("dialog_payout_request")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Retirer mes Commissions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Solde disponible :",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateGray
                        )
                        Text(
                            text = "${String.format("%,.0f", availableBalance)} $currentCurrency",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Montant à retirer (FCFA)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Choisir l'opérateur Mobile Money pour la réception :",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateGray
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    providers.forEach { provider ->
                        val isSelected = selectedProvider == provider
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) EmeraldPrimary else SoftSand,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedProvider = provider }
                        ) {
                            Text(
                                text = provider,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) PureWhite else SlateDark,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Numéro de téléphone récepteur") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = LocalizedStrings.get("cancel", currentLanguage))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                onConfirm(amt, selectedProvider, phoneNumber)
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaGold, contentColor = SlateDark)
                    ) {
                        Text(text = "Effectuer le Retrait", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
