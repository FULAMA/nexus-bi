package com.example.nexusbi.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nexusbi.data.local.entities.GroupOrderEntity
import com.example.nexusbi.ui.language.AppLanguage
import com.example.nexusbi.ui.language.LocalizedStrings
import com.example.nexusbi.ui.theme.*
import com.example.nexusbi.ui.viewmodel.GroupOrderWithPledges

@Composable
fun GroupBuyingScreen(
    currentLanguage: AppLanguage,
    groupOrders: List<GroupOrderWithPledges>,
    totalEstimatedSavings: Double,
    onPledgeClick: (GroupOrderEntity) -> Unit,
    onCreateGroupOrderClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, LightBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBox,
                                contentDescription = "Coop",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = LocalizedStrings.get("coop_title", currentLanguage),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SlateDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = LocalizedStrings.get("coop_subtitle", currentLanguage),
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateGray
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = LocalizedStrings.get("group_savings_title", currentLanguage),
                                style = MaterialTheme.typography.labelSmall,
                                color = SlateGray
                            )
                            Text(
                                text = "+${String.format("%,.0f", totalEstimatedSavings)} FCFA",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                        }

                        Button(
                            onClick = onCreateGroupOrderClick,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("create_group_order_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create",
                                tint = PureWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = LocalizedStrings.get("create_order", currentLanguage),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "Commandes Groupées Actives",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SlateDark
            )
        }

        // Group Orders List
        items(
            items = groupOrders,
            key = { it.order.id }
        ) { item ->
            GroupOrderCard(
                order = item.order,
                currentLanguage = currentLanguage,
                onPledgeClick = { onPledgeClick(item.order) }
            )
        }
    }
}

@Composable
fun GroupOrderCard(
    order: GroupOrderEntity,
    currentLanguage: AppLanguage,
    onPledgeClick: () -> Unit
) {
    val progress = (order.currentQuantity.toFloat() / order.targetQuantity.toFloat()).coerceIn(0f, 1f)
    val unitSavings = (order.regularUnitPrice - order.groupUnitPrice).coerceAtLeast(0.0)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, LightBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("group_order_item_${order.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Supplier & Category Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldLight)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = order.category,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (order.status == "FUNDED") GreenPositive.copy(alpha = 0.15f) else TerracottaGold.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (order.status == "FUNDED") LocalizedStrings.get("status_funded", currentLanguage) else LocalizedStrings.get("status_open", currentLanguage),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (order.status == "FUNDED") GreenPositive else TerracottaGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Order Title
            Text(
                text = order.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SlateDark
            )

            Text(
                text = "Fournisseur: ${order.supplierName}",
                style = MaterialTheme.typography.bodySmall,
                color = SlateGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Prices Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SoftSand)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = LocalizedStrings.get("retail_price", currentLanguage),
                        style = MaterialTheme.typography.labelSmall,
                        color = SlateGray
                    )
                    Text(
                        text = "${String.format("%,.0f", order.regularUnitPrice)} FCFA",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SlateGray
                    )
                }

                Column {
                    Text(
                        text = LocalizedStrings.get("wholesale_price", currentLanguage),
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldPrimary
                    )
                    Text(
                        text = "${String.format("%,.0f", order.groupUnitPrice)} FCFA",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                }

                Column {
                    Text(
                        text = LocalizedStrings.get("save_per_unit", currentLanguage),
                        style = MaterialTheme.typography.labelSmall,
                        color = GreenPositive
                    )
                    Text(
                        text = "-${String.format("%,.0f", unitSavings)} FCFA",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = GreenPositive
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${LocalizedStrings.get("target_progress", currentLanguage)} ${order.currentQuantity} / ${order.targetQuantity} ${order.unitName}s",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = SlateDark
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = EmeraldPrimary,
                trackColor = EmeraldLight
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Participate Button
            Button(
                onClick = onPledgeClick,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pledge_button_${order.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Pledge",
                    tint = PureWhite
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = LocalizedStrings.get("participate", currentLanguage),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
