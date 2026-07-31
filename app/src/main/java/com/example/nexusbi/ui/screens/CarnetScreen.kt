package com.example.nexusbi.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.nexusbi.data.api.AiRiskEvaluation
import com.example.nexusbi.data.local.entities.CustomerEntity
import com.example.nexusbi.data.local.entities.DebtRecordEntity
import com.example.nexusbi.ui.language.AppLanguage
import com.example.nexusbi.ui.language.LocalizedStrings
import com.example.nexusbi.ui.theme.*
import com.example.nexusbi.ui.viewmodel.CustomerFilter
import com.example.nexusbi.ui.viewmodel.CustomerSort
import com.example.nexusbi.ui.viewmodel.CustomerWithBalance
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarnetScreen(
    currentLanguage: AppLanguage,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterType: CustomerFilter,
    sortType: CustomerSort,
    onFilterTypeChange: (CustomerFilter) -> Unit,
    onSortTypeChange: (CustomerSort) -> Unit,
    customersWithBalance: List<CustomerWithBalance>,
    selectedCustomer: CustomerWithBalance?,
    selectedCustomerRecords: List<DebtRecordEntity>,
    aiRiskEvaluation: AiRiskEvaluation? = null,
    isAiLoading: Boolean = false,
    onSelectCustomer: (CustomerWithBalance?) -> Unit,
    onAddCustomerClick: () -> Unit,
    onAddRecordClick: () -> Unit,
    onSendReminderClick: () -> Unit,
    onEvaluateAiRiskClick: (CustomerWithBalance) -> Unit = {},
    onDeleteCustomerClick: (CustomerEntity) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.FRENCH) }
    var showSortMenu by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar & Add Customer Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            text = LocalizedStrings.get("search_customer", currentLanguage),
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = SlateGray
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = SlateGray
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = LightBorder
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_customer_input")
                )

                Button(
                    onClick = onAddCustomerClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp),
                    modifier = Modifier.testTag("add_customer_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Customer",
                        tint = PureWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips and Sort Menu Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        FilterChip(
                            selected = filterType == CustomerFilter.ALL,
                            onClick = { onFilterTypeChange(CustomerFilter.ALL) },
                            label = { Text(LocalizedStrings.get("filter_all", currentLanguage), fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = PureWhite
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = filterType == CustomerFilter.WITH_DEBT,
                            onClick = { onFilterTypeChange(CustomerFilter.WITH_DEBT) },
                            label = { Text(LocalizedStrings.get("filter_due", currentLanguage), fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RedNegative,
                                selectedLabelColor = PureWhite
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = filterType == CustomerFilter.PAID,
                            onClick = { onFilterTypeChange(CustomerFilter.PAID) },
                            label = { Text(LocalizedStrings.get("filter_paid", currentLanguage), fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GreenPositive,
                                selectedLabelColor = PureWhite
                            )
                        )
                    }
                }

                Box {
                    IconButton(
                        onClick = { showSortMenu = true },
                        modifier = Modifier.testTag("sort_menu_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Sort",
                            tint = SlateDark
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(LocalizedStrings.get("sort_highest", currentLanguage)) },
                            leadingIcon = { Icon(Icons.Default.KeyboardArrowUp, contentDescription = null) },
                            onClick = {
                                onSortTypeChange(CustomerSort.BALANCE_DESC)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(LocalizedStrings.get("sort_name", currentLanguage)) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            onClick = {
                                onSortTypeChange(CustomerSort.NAME_ASC)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(LocalizedStrings.get("sort_recent", currentLanguage)) },
                            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                            onClick = {
                                onSortTypeChange(CustomerSort.RECENT)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LocalizedStrings.get("debtor_list", currentLanguage),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )
                Text(
                    text = "${customersWithBalance.size} clients",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateGray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Customer List
            if (customersWithBalance.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Empty",
                            modifier = Modifier.size(56.dp),
                            tint = SlateGray.copy(alpha = 0.5f)
                        )
                        Text(
                            text = LocalizedStrings.get("no_debt", currentLanguage),
                            style = MaterialTheme.typography.bodyLarge,
                            color = SlateGray
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        items = customersWithBalance,
                        key = { it.customer.id }
                    ) { item ->
                        CustomerCard(
                            customerWithBalance = item,
                            currentLanguage = currentLanguage,
                            onClick = { onSelectCustomer(item) }
                        )
                    }
                }
            }
        }

        // Customer Detail BottomSheet
        if (selectedCustomer != null) {
            CustomerDetailSheet(
                customerWithBalance = selectedCustomer,
                records = selectedCustomerRecords,
                aiRiskEvaluation = aiRiskEvaluation,
                isAiLoading = isAiLoading,
                currentLanguage = currentLanguage,
                dateFormat = dateFormat,
                onDismiss = { onSelectCustomer(null) },
                onAddRecordClick = onAddRecordClick,
                onSendReminderClick = onSendReminderClick,
                onEvaluateAiRiskClick = { onEvaluateAiRiskClick(selectedCustomer) },
                onDeleteCustomerClick = { onDeleteCustomerClick(selectedCustomer.customer) }
            )
        }
    }
}

@Composable
fun CustomerCard(
    customerWithBalance: CustomerWithBalance,
    currentLanguage: AppLanguage,
    onClick: () -> Unit
) {
    val net = customerWithBalance.netBalance
    val badgeText = when {
        net > 10000 -> LocalizedStrings.get("badge_urgent", currentLanguage)
        net > 0 -> LocalizedStrings.get("badge_active", currentLanguage)
        else -> LocalizedStrings.get("badge_paid", currentLanguage)
    }
    val badgeBg = when {
        net > 10000 -> RedNegative.copy(alpha = 0.15f)
        net > 0 -> TerracottaGold.copy(alpha = 0.15f)
        else -> GreenPositive.copy(alpha = 0.15f)
    }
    val badgeColor = when {
        net > 10000 -> RedNegative
        net > 0 -> TerracottaGold
        else -> GreenPositive
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("customer_item_${customerWithBalance.customer.id}")
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Avatar circle
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (net > 0) RedNegative.copy(alpha = 0.15f)
                            else GreenPositive.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = customerWithBalance.customer.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (net > 0) RedNegative else GreenPositive
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = customerWithBalance.customer.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = SlateDark
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(badgeBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone",
                            tint = SlateGray,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = customerWithBalance.customer.phone.ifBlank { "Pas de numéro" },
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateGray
                        )
                    }
                    if (customerWithBalance.customer.neighborhood.isNotBlank()) {
                        Text(
                            text = customerWithBalance.customer.neighborhood,
                            style = MaterialTheme.typography.labelSmall,
                            color = SlateGray.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = LocalizedStrings.get("net_balance", currentLanguage),
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateGray
                )
                Text(
                    text = "${String.format("%,.0f", customerWithBalance.netBalance)} FCFA",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (customerWithBalance.netBalance > 0) RedNegative else GreenPositive
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailSheet(
    customerWithBalance: CustomerWithBalance,
    records: List<DebtRecordEntity>,
    aiRiskEvaluation: AiRiskEvaluation? = null,
    isAiLoading: Boolean = false,
    currentLanguage: AppLanguage,
    dateFormat: SimpleDateFormat,
    onDismiss: () -> Unit,
    onAddRecordClick: () -> Unit,
    onSendReminderClick: () -> Unit,
    onEvaluateAiRiskClick: () -> Unit = {},
    onDeleteCustomerClick: () -> Unit
) {
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SoftSand
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            // Header Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = customerWithBalance.customer.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = SlateDark
                    )
                    Text(
                        text = "${customerWithBalance.customer.phone} • ${customerWithBalance.customer.neighborhood}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateGray
                    )
                }

                IconButton(
                    onClick = onDeleteCustomerClick,
                    modifier = Modifier.testTag("delete_customer_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = RedNegative
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // AI Risk Evaluation Card (Gemini API)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PureWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = "AI Risk", tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                            Text(
                                text = "Évaluation Risque Client (IA)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SlateDark
                            )
                        }

                        if (aiRiskEvaluation == null) {
                            OutlinedButton(
                                onClick = onEvaluateAiRiskClick,
                                shape = RoundedCornerShape(10.dp),
                                enabled = !isAiLoading,
                                modifier = Modifier.testTag("evaluate_ai_risk_btn")
                            ) {
                                if (isAiLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = EmeraldPrimary)
                                } else {
                                    Text("Évaluer", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (aiRiskEvaluation != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val riskColor = when (aiRiskEvaluation.level.uppercase()) {
                                "FIABLE", "FAIBLE" -> GreenPositive
                                "PRUDENCE", "MOYEN" -> TerracottaGold
                                else -> RedNegative
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = riskColor.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Risque: ${aiRiskEvaluation.level} (${aiRiskEvaluation.score}/100)",
                                    color = riskColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "Plafond max: ${String.format("%,.0f", aiRiskEvaluation.maxRecommendedCreditFcfa)} FCFA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SlateDark
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = aiRiskEvaluation.recommendation,
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Total Balance Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PureWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = LocalizedStrings.get("total_credit_due", currentLanguage),
                                style = MaterialTheme.typography.labelMedium,
                                color = SlateGray
                            )
                            Text(
                                text = "${String.format("%,.0f", customerWithBalance.netBalance)} FCFA",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (customerWithBalance.netBalance > 0) RedNegative else GreenPositive
                            )
                        }

                        if (customerWithBalance.netBalance > 0) {
                            Button(
                                onClick = onSendReminderClick,
                                colors = ButtonDefaults.buttonColors(containerColor = TerracottaGold),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("send_reminder_sheet_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = PureWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = LocalizedStrings.get("send_reminder", currentLanguage),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Share Statement Button
                    OutlinedButton(
                        onClick = {
                            val historyText = records.joinToString("\n") { r ->
                                "- ${dateFormat.format(Date(r.date))}: ${r.itemDescription} (${if (r.type == "CREDIT") "+" else "-"}${String.format("%,.0f", r.amount)} FCFA)"
                            }
                            val statementText = """
                                📄 *RELEVÉ DE COMPTE - CARNET KREDI*
                                Client: ${customerWithBalance.customer.name}
                                Tél: ${customerWithBalance.customer.phone}
                                Quartier: ${customerWithBalance.customer.neighborhood}
                                ----------------------------------
                                Total Solde Dû: ${String.format("%,.0f", customerWithBalance.netBalance)} FCFA
                                Total Crédits: ${String.format("%,.0f", customerWithBalance.totalDebt)} FCFA
                                Total Payé: ${String.format("%,.0f", customerWithBalance.totalPaid)} FCFA
                                ----------------------------------
                                *Historique des opérations :*
                                $historyText
                                
                                Merci de votre confiance !
                            """.trimIndent()

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Relevé de compte - ${customerWithBalance.customer.name}")
                                putExtra(Intent.EXTRA_TEXT, statementText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Partager le relevé de compte via"))
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("share_statement_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = LocalizedStrings.get("share_statement", currentLanguage),
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onAddRecordClick,
                    colors = ButtonDefaults.buttonColors(containerColor = RedNegative),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_credit_entry_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Credit")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = LocalizedStrings.get("add_debt_btn", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onAddRecordClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPositive),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_payment_entry_btn")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Payment")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = LocalizedStrings.get("add_payment_btn", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = LocalizedStrings.get("history", currentLanguage),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SlateDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Transactions History List
            if (records.isEmpty()) {
                Text(
                    text = "Aucune transaction enregistrée.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateGray,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                ) {
                    items(records) { record ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PureWhite,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (record.type == "CREDIT") Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = record.type,
                                        tint = if (record.type == "CREDIT") RedNegative else GreenPositive,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = record.itemDescription,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SlateDark
                                        )
                                        Text(
                                            text = dateFormat.format(Date(record.date)),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SlateGray
                                        )
                                    }
                                }

                                Text(
                                    text = "${if (record.type == "CREDIT") "+" else "-"}${String.format("%,.0f", record.amount)} FCFA",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (record.type == "CREDIT") RedNegative else GreenPositive
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

