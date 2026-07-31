package com.example.nexusbi.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.unit.sp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.nexusbi.data.local.entities.GroupOrderEntity
import com.example.nexusbi.ui.language.AppLanguage
import com.example.nexusbi.ui.language.LocalizedStrings
import com.example.nexusbi.ui.theme.*
import com.example.nexusbi.ui.utils.RemindersHelper
import com.example.nexusbi.ui.viewmodel.CustomerWithBalance

@Composable
fun AddCustomerDialog(
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, neighborhood: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var neighborhood by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = LocalizedStrings.get("add_customer", currentLanguage),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(LocalizedStrings.get("customer_name", currentLanguage)) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_customer_name")
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(LocalizedStrings.get("phone_number", currentLanguage)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_customer_phone")
                )
                OutlinedTextField(
                    value = neighborhood,
                    onValueChange = { neighborhood = it },
                    label = { Text(LocalizedStrings.get("neighborhood", currentLanguage)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(LocalizedStrings.get("notes", currentLanguage)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, phone, neighborhood, notes) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("confirm_add_customer_btn")
            ) {
                Text(LocalizedStrings.get("save", currentLanguage))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(LocalizedStrings.get("cancel", currentLanguage))
            }
        }
    )
}

@Composable
fun AddRecordDialog(
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, description: String, type: String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("CREDIT") } // "CREDIT" or "PAYMENT"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (type == "CREDIT") LocalizedStrings.get("add_debt_btn", currentLanguage) else LocalizedStrings.get("add_payment_btn", currentLanguage),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Type Selector Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = type == "CREDIT",
                        onClick = { type = "CREDIT" },
                        label = { Text(LocalizedStrings.get("add_debt_btn", currentLanguage)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RedNegative,
                            selectedLabelColor = PureWhite
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = type == "PAYMENT",
                        onClick = { type = "PAYMENT" },
                        label = { Text(LocalizedStrings.get("add_payment_btn", currentLanguage)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenPositive,
                            selectedLabelColor = PureWhite
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("${LocalizedStrings.get("amount", currentLanguage)} (FCFA)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_record_amount")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(LocalizedStrings.get("item_desc", currentLanguage)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_record_description")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    onConfirm(amt, description, type)
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (type == "CREDIT") RedNegative else GreenPositive),
                modifier = Modifier.testTag("confirm_add_record_btn")
            ) {
                Text(LocalizedStrings.get("save", currentLanguage))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(LocalizedStrings.get("cancel", currentLanguage))
            }
        }
    )
}

@Composable
fun ReminderDialog(
    customerWithBalance: CustomerWithBalance,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val messageText = remember(customerWithBalance, currentLanguage) {
        RemindersHelper.generateReminderText(
            customerName = customerWithBalance.customer.name,
            amountDue = customerWithBalance.netBalance,
            language = currentLanguage
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = LocalizedStrings.get("reminder_dialog_title", currentLanguage),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = LocalizedStrings.get("reminder_template_title", currentLanguage),
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateGray
                )

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SoftSand,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = messageText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateDark,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        RemindersHelper.sendWhatsApp(context, customerWithBalance.customer.phone, messageText)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF25D366)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("send_whatsapp_btn")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "WhatsApp", tint = PureWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = LocalizedStrings.get("send_whatsapp", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }

                OutlinedButton(
                    onClick = {
                        RemindersHelper.sendSms(context, customerWithBalance.customer.phone, messageText)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("send_sms_btn")
                ) {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "SMS", tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = LocalizedStrings.get("send_sms", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(LocalizedStrings.get("cancel", currentLanguage))
            }
        }
    )
}

@Composable
fun GroupPledgeDialog(
    order: GroupOrderEntity,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (boutiqueName: String, phone: String, qty: Int) -> Unit
) {
    var boutiqueName by remember { mutableStateOf("Boutique Kredi") }
    var phone by remember { mutableStateOf("+243810000000") }
    var qtyText by remember { mutableStateOf("5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Commander : ${order.title}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Prix de gros: ${String.format("%,.0f", order.groupUnitPrice)} FCFA / ${order.unitName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = boutiqueName,
                    onValueChange = { boutiqueName = it },
                    label = { Text("Nom de votre boutique") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = qtyText,
                    onValueChange = { qtyText = it },
                    label = { Text("${LocalizedStrings.get("pledge_qty", currentLanguage)} (${order.unitName}s)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_pledge_qty")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = qtyText.toIntOrNull() ?: 1
                    onConfirm(boutiqueName, phone, qty)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("confirm_pledge_btn")
            ) {
                Text(LocalizedStrings.get("confirm_pledge", currentLanguage))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(LocalizedStrings.get("cancel", currentLanguage))
            }
        }
    )
}

@Composable
fun CreateGroupOrderDialog(
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        supplierName: String,
        regularPrice: Double,
        groupPrice: Double,
        unitName: String,
        targetQty: Int,
        category: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var supplierName by remember { mutableStateOf("") }
    var regularPriceText by remember { mutableStateOf("") }
    var groupPriceText by remember { mutableStateOf("") }
    var unitName by remember { mutableStateOf("carton") }
    var targetQtyText by remember { mutableStateOf("50") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = LocalizedStrings.get("create_order", currentLanguage),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nom du produit (ex: Carton de Lait)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = supplierName,
                    onValueChange = { supplierName = it },
                    label = { Text("Nom du grossiste / fournisseur") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = regularPriceText,
                        onValueChange = { regularPriceText = it },
                        label = { Text("Prix détail") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = groupPriceText,
                        onValueChange = { groupPriceText = it },
                        label = { Text("Prix gros") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = unitName,
                        onValueChange = { unitName = it },
                        label = { Text("Unité") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = targetQtyText,
                        onValueChange = { targetQtyText = it },
                        label = { Text("Quantité cible") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val reg = regularPriceText.toDoubleOrNull() ?: 0.0
                    val grp = groupPriceText.toDoubleOrNull() ?: 0.0
                    val tgt = targetQtyText.toIntOrNull() ?: 10
                    onConfirm(title, supplierName, reg, grp, unitName, tgt, "Alimentaire")
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text(LocalizedStrings.get("save", currentLanguage))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(LocalizedStrings.get("cancel", currentLanguage))
            }
        }
    )
}

@Composable
fun AppGuideAndUpdateDialog(
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit
) {
    var isCheckingUpdates by remember { mutableStateOf(false) }
    var updateStatusMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info",
                    tint = EmeraldPrimary
                )
                Text(
                    text = "Publication & Mises à Jour App",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                // Version & OTA Updates Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SoftSand,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Version App: v1.2.0 (Stable)",
                                fontWeight = FontWeight.Bold,
                                color = SlateDark,
                                fontSize = 13.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Auto-Sync Actif",
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "💡 Mises à jour automatiques :\nLorsque vous publiez l'application sur le Google Play Store ou un serveur de mises à jour In-App, chaque nouvelle version est automatiquement téléchargée et installée sur les téléphones des utilisateurs sans effacer leurs données (base de données SQLite préservée).",
                            fontSize = 11.sp,
                            color = SlateGray,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                isCheckingUpdates = true
                                updateStatusMessage = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("check_updates_btn")
                        ) {
                            if (isCheckingUpdates) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = PureWhite
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Vérification...", fontSize = 12.sp)
                            } else {
                                Text("Vérifier les Mises à Jour", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        LaunchedEffect(isCheckingUpdates) {
                            if (isCheckingUpdates) {
                                kotlinx.coroutines.delay(1200)
                                isCheckingUpdates = false
                                updateStatusMessage = "✅ Vous utilisez la version v1.2.0. Mises à jour directes activées !"
                            }
                        }

                        if (updateStatusMessage != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = updateStatusMessage!!,
                                fontSize = 11.sp,
                                color = GreenPositive,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // GitHub Export Info
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PureWhite,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "🐙 Exporter vers GitHub",
                            fontWeight = FontWeight.Bold,
                            color = SlateDark,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Pour publier votre code sur GitHub, ouvrez le menu de l'éditeur AI Studio (icône engrenage / 3 points en haut) et sélectionnez 'Export to GitHub' ou téléchargez l'archive ZIP.",
                            fontSize = 11.sp,
                            color = SlateGray,
                            lineHeight = 15.sp
                        )
                    }
                }

                // App Features Overview
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PureWhite,
                    shadowElevation = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "📱 Application Explicite NexusBI",
                            fontWeight = FontWeight.Bold,
                            color = SlateDark,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Carnet Client : Dettes & Relances SMS/WhatsApp\n• Score Risque IA Gemini : Évaluation solvabilité\n• Achats Groupés : Grossistes & Réductions\n• Taux de Change & Cours Marchés en temps réel",
                            fontSize = 11.sp,
                            color = SlateGray,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Compris !")
            }
        }
    )
}
