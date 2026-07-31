package com.example.nexusbi.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexusbi.ui.language.AppLanguage
import com.example.nexusbi.ui.language.LocalizedStrings
import com.example.nexusbi.ui.theme.*
import com.example.nexusbi.ui.viewmodel.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MonetizationDashboardScreen(
    currentLanguage: AppLanguage,
    currentCurrency: String,
    subscriptionPlan: SubscriptionPlan,
    subscriptionStatus: SubscriptionStatus,
    renewalDate: String,
    paymentMethod: String,
    totalCommissionsEarned: Double,
    availableCommissionBalance: Double,
    withdrawnCommissions: Double,
    groupOrders: List<GroupOrderWithPledges>,
    payoutHistory: List<CommissionPayoutRecord>,
    onUpgradeClick: () -> Unit,
    onRequestPayoutClick: () -> Unit,
    onCancelSubscriptionClick: () -> Unit
) {
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: Commissions Dashboard, 1: Premium Subscription

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, LightBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("monetization_hero_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
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
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Premium",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = LocalizedStrings.get("monetization_title", currentLanguage),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateDark
                                )
                                Text(
                                    text = "Rentabilité Achats Groupés & Abonnements",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateGray
                                )
                            }
                        }

                        // Active Plan Tag
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (subscriptionPlan != SubscriptionPlan.FREE) TerracottaGold.copy(alpha = 0.15f) else Color(0xFFF3F4F6)
                        ) {
                            Text(
                                text = if (subscriptionPlan != SubscriptionPlan.FREE) "★ PREMIUM" else "GRATUIT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (subscriptionPlan != SubscriptionPlan.FREE) TerracottaGold else SlateGray,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sub-tab Navigation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3F4F6))
                            .padding(4.dp)
                    ) {
                        Button(
                            onClick = { selectedSubTab = 0 },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedSubTab == 0) PureWhite else Color.Transparent,
                                contentColor = if (selectedSubTab == 0) EmeraldPrimary else SlateGray
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Commissions",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick = { selectedSubTab = 1 },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedSubTab == 1) PureWhite else Color.Transparent,
                                contentColor = if (selectedSubTab == 1) EmeraldPrimary else SlateGray
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Abonnement",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        if (selectedSubTab == 0) {
            // === TAB 0: COMMISSIONS DASHBOARD ===
            item {
                Text(
                    text = LocalizedStrings.get("commissions_title", currentLanguage),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Key Financial Metrics Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total Commissions Card
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = BorderStroke(1.dp, LightBorder),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("card_total_commissions")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = LocalizedStrings.get("total_commissions", currentLanguage),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SlateGray
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${String.format("%,.0f", totalCommissionsEarned)} $currentCurrency",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            }
                        }

                        // Available Balance for Payout
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = BorderStroke(1.dp, LightBorder),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("card_available_balance")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = TerracottaGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = LocalizedStrings.get("available_commissions", currentLanguage),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SlateGray
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "${String.format("%,.0f", availableCommissionBalance)} $currentCurrency",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TerracottaGold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Withdrawn Commissions
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = BorderStroke(1.dp, LightBorder),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = LocalizedStrings.get("withdrawn_commissions", currentLanguage),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SlateGray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${String.format("%,.0f", withdrawnCommissions)} $currentCurrency",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateDark
                                )
                            }
                        }

                        // Commission Boost Rate
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = BorderStroke(1.dp, LightBorder),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = LocalizedStrings.get("avg_commission_rate", currentLanguage),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SlateGray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "2.5% ${if (subscriptionPlan != SubscriptionPlan.FREE) "(+0.5% Premium)" else ""}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            }
                        }
                    }
                }
            }

            // Payout Request Action Button
            item {
                Button(
                    onClick = onRequestPayoutClick,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TerracottaGold,
                        contentColor = PureWhite
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_request_payout")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Payout",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = LocalizedStrings.get("request_payout", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Group Orders Commission Breakdown
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, LightBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Détail des Commissions par Achat Groupé",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SlateDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (groupOrders.isEmpty()) {
                            Text(
                                text = "Aucune commande groupée actuellement.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SlateGray,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            groupOrders.forEachIndexed { index, orderWithPledges ->
                                val order = orderWithPledges.order
                                val totalVolume = order.currentQuantity * order.groupUnitPrice
                                val ratePct = 2.5 + if (subscriptionPlan != SubscriptionPlan.FREE) 0.5 else 0.0
                                val commissionEarned = totalVolume * (ratePct / 100.0)

                                if (index > 0) HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    color = LightBorder
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = order.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = SlateDark
                                        )
                                        Text(
                                            text = "+${String.format("%,.0f", commissionEarned)} $currentCurrency",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldPrimary
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Grossiste : ${order.supplierName} • Vol: ${String.format("%,.0f", totalVolume)} FCFA",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SlateGray
                                        )
                                        Text(
                                            text = "Taux : $ratePct%",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SlateGray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Payout History List
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, LightBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = LocalizedStrings.get("payout_history", currentLanguage),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SlateDark
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (payoutHistory.isEmpty()) {
                            Text(
                                text = "Aucun retrait effectué pour le moment.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SlateGray,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            payoutHistory.forEachIndexed { index, record ->
                                if (index > 0) HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = LightBorder
                                )

                                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                val dateStr = sdf.format(Date(record.dateTimestamp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${record.provider} • ${record.phoneNumber}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = SlateDark
                                        )
                                        Text(
                                            text = "$dateStr • Réf: ${record.id}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SlateGray
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "-${String.format("%,.0f", record.amountFcfa)} FCFA",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = SlateDark
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = EmeraldLight
                                        ) {
                                            Text(
                                                text = if (record.status == "SUCCESS") "Transféré" else "En cours",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = EmeraldPrimary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } else {
            // === TAB 1: PREMIUM SUBSCRIPTION MANAGEMENT ===
            item {
                Text(
                    text = LocalizedStrings.get("subscription_management_title", currentLanguage),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Active Subscription Info Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, LightBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_active_subscription")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = LocalizedStrings.get("current_plan", currentLanguage),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SlateGray
                                )
                                Text(
                                    text = subscriptionPlan.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (subscriptionStatus == SubscriptionStatus.ACTIVE) EmeraldLight else Color(0xFFFEF2F2)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (subscriptionStatus == SubscriptionStatus.ACTIVE) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (subscriptionStatus == SubscriptionStatus.ACTIVE) EmeraldPrimary else RedNegative,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = if (subscriptionStatus == SubscriptionStatus.ACTIVE) "ACTIF" else "EXPIRÉ",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (subscriptionStatus == SubscriptionStatus.ACTIVE) EmeraldPrimary else RedNegative
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = LightBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Date de renouvellement :",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SlateGray
                                )
                                Text(
                                    text = renewalDate,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateDark
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Mode de paiement :",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SlateGray
                                )
                                Text(
                                    text = paymentMethod,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateDark
                                )
                            }
                        }
                    }
                }
            }

            // Subscription Plan Options (Free vs Monthly vs Annual)
            item {
                Text(
                    text = "Choisir ou modifier un Forfait",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Monthly Plan Card
            item {
                val isSelected = subscriptionPlan == SubscriptionPlan.MONTHLY_PREMIUM
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) EmeraldLight else PureWhite
                    ),
                    border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) EmeraldPrimary else LightBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUpgradeClick() }
                        .testTag("plan_card_monthly")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Premium Mensuel",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateDark
                                )
                                Text(
                                    text = "Flexibilité maximale sans engagement",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SlateGray
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "3 000 FCFA",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                                Text(
                                    text = "/ mois ($5)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SlateGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = LightBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        SubscriptionPlan.MONTHLY_PREMIUM.features.forEach { feature ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = feature,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateDark
                                )
                            }
                        }
                    }
                }
            }

            // Annual Plan Card (with Special Offer Badge)
            item {
                val isSelected = subscriptionPlan == SubscriptionPlan.ANNUAL_PREMIUM
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFFFFBEB) else PureWhite
                    ),
                    border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) TerracottaGold else LightBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUpgradeClick() }
                        .testTag("plan_card_annual")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "Premium Annuel",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = SlateDark
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = TerracottaGold
                                        ) {
                                            Text(
                                                text = "-16%",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = PureWhite,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "Économisez 6 000 FCFA par an",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SlateGray
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "30 000 FCFA",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TerracottaGold
                                )
                                Text(
                                    text = "/ an ($50)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SlateGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = LightBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        SubscriptionPlan.ANNUAL_PREMIUM.features.forEach { feature ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = TerracottaGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = feature,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateDark
                                )
                            }
                        }
                    }
                }
            }

            // Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onUpgradeClick,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_upgrade_premium")
                    ) {
                        Text(
                            text = LocalizedStrings.get("upgrade_plan", currentLanguage),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (subscriptionPlan != SubscriptionPlan.FREE) {
                        OutlinedButton(
                            onClick = onCancelSubscriptionClick,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, RedNegative),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedNegative),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) {
                            Text(
                                text = LocalizedStrings.get("cancel_plan", currentLanguage),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
