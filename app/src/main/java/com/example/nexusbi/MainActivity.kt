package com.example.nexusbi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nexusbi.ui.components.*
import com.example.nexusbi.ui.language.LocalizedStrings
import com.example.nexusbi.ui.screens.CarnetScreen
import com.example.nexusbi.ui.screens.GroupBuyingScreen
import com.example.nexusbi.ui.screens.InsightsShieldScreen
import com.example.nexusbi.ui.screens.MonetizationDashboardScreen
import com.example.nexusbi.ui.theme.NexusBITheme
import com.example.nexusbi.ui.theme.EmeraldPrimary
import com.example.nexusbi.ui.theme.PureWhite
import com.example.nexusbi.ui.viewmodel.CarnetViewModel
import com.example.nexusbi.ui.viewmodel.CarnetViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: CarnetViewModel by viewModels {
        CarnetViewModelFactory((application as CarnetApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NexusBITheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                var selectedTabIndex by remember { mutableIntStateOf(0) }

                Scaffold(
                    topBar = {
                        TopHeaderBar(
                            currentLanguage = uiState.language,
                            onLanguageSelected = { viewModel.setLanguage(it) },
                            currentCurrency = uiState.currentCurrency,
                            onCurrencySelected = { viewModel.setCurrency(it) },
                            totalCreditDue = uiState.totalCreditDueAll,
                            totalSavings = uiState.totalEstimatedSavings,
                            onOpenGuideAndUpdate = { viewModel.setShowAppGuideDialog(true) }
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = PureWhite,
                            tonalElevation = 8.dp,
                            modifier = Modifier
                                .navigationBarsPadding()
                                .testTag("main_bottom_navigation")
                        ) {
                            NavigationBarItem(
                                selected = selectedTabIndex == 0,
                                onClick = { selectedTabIndex = 0 },
                                icon = { Icon(imageVector = Icons.Default.List, contentDescription = "Carnet") },
                                label = {
                                    Text(
                                        text = LocalizedStrings.get("tab_carnet", uiState.language),
                                        fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = EmeraldPrimary,
                                    selectedTextColor = EmeraldPrimary,
                                    indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("tab_carnet_item")
                            )

                            NavigationBarItem(
                                selected = selectedTabIndex == 1,
                                onClick = { selectedTabIndex = 1 },
                                icon = { Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Coop") },
                                label = {
                                    Text(
                                        text = LocalizedStrings.get("tab_coop", uiState.language),
                                        fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = EmeraldPrimary,
                                    selectedTextColor = EmeraldPrimary,
                                    indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("tab_coop_item")
                            )

                            NavigationBarItem(
                                selected = selectedTabIndex == 2,
                                onClick = { selectedTabIndex = 2 },
                                icon = { Icon(imageVector = Icons.Default.Info, contentDescription = "Insights") },
                                label = {
                                    Text(
                                        text = LocalizedStrings.get("tab_insights", uiState.language),
                                        fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = EmeraldPrimary,
                                    selectedTextColor = EmeraldPrimary,
                                    indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("tab_insights_item")
                            )

                            NavigationBarItem(
                                selected = selectedTabIndex == 3,
                                onClick = { selectedTabIndex = 3 },
                                icon = { Icon(imageVector = Icons.Default.Star, contentDescription = "Monétisation") },
                                label = {
                                    Text(
                                        text = LocalizedStrings.get("tab_monetization", uiState.language),
                                        fontWeight = if (selectedTabIndex == 3) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = EmeraldPrimary,
                                    selectedTextColor = EmeraldPrimary,
                                    indicatorColor = EmeraldPrimary.copy(alpha = 0.15f)
                                ),
                                modifier = Modifier.testTag("tab_monetization_item")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTabIndex) {
                            0 -> CarnetScreen(
                                currentLanguage = uiState.language,
                                searchQuery = uiState.searchQuery,
                                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                filterType = uiState.filterType,
                                sortType = uiState.sortType,
                                onFilterTypeChange = { viewModel.setFilterType(it) },
                                onSortTypeChange = { viewModel.setSortType(it) },
                                customersWithBalance = uiState.customersWithBalance,
                                selectedCustomer = uiState.selectedCustomer,
                                selectedCustomerRecords = uiState.selectedCustomerRecords,
                                aiRiskEvaluation = uiState.selectedCustomer?.let { uiState.aiRiskEvaluationMap[it.customer.id] },
                                isAiLoading = uiState.isAiLoading,
                                onSelectCustomer = { viewModel.selectCustomer(it) },
                                onAddCustomerClick = { viewModel.setShowAddCustomerDialog(true) },
                                onAddRecordClick = { viewModel.setShowAddRecordDialog(true) },
                                onSendReminderClick = { viewModel.setShowReminderDialog(true) },
                                onEvaluateAiRiskClick = { customer -> viewModel.evaluateCustomerRiskWithAi(customer) },
                                onDeleteCustomerClick = { viewModel.deleteCustomer(it) }
                            )

                            1 -> GroupBuyingScreen(
                                currentLanguage = uiState.language,
                                groupOrders = uiState.groupOrdersWithPledges,
                                totalEstimatedSavings = uiState.totalEstimatedSavings,
                                onPledgeClick = { viewModel.openPledgeDialog(it) },
                                onCreateGroupOrderClick = { viewModel.setShowCreateGroupOrderDialog(true) }
                            )

                            2 -> InsightsShieldScreen(
                                currentLanguage = uiState.language,
                                currentCurrency = uiState.currentCurrency,
                                exchangeRates = uiState.exchangeRates,
                                marketCommodities = uiState.marketCommodities,
                                aiAdviceText = uiState.aiAdviceText,
                                isAiLoading = uiState.isAiLoading,
                                isInsuranceActive = uiState.isInsuranceShieldActive,
                                customersWithBalance = uiState.customersWithBalance,
                                onToggleInsurance = { viewModel.toggleInsuranceShield() },
                                onAskAi = { query -> viewModel.askAiAdvisor(query) },
                                onRefreshMarketPrices = { viewModel.refreshMarketPrices() },
                                onRefreshExchangeRates = { viewModel.refreshExchangeRates() }
                            )

                            3 -> MonetizationDashboardScreen(
                                currentLanguage = uiState.language,
                                currentCurrency = uiState.currentCurrency,
                                subscriptionPlan = uiState.subscriptionPlan,
                                subscriptionStatus = uiState.subscriptionStatus,
                                renewalDate = uiState.subscriptionRenewalDate,
                                paymentMethod = uiState.subscriptionPaymentMethod,
                                totalCommissionsEarned = uiState.totalCommissionsEarned,
                                availableCommissionBalance = uiState.availableCommissionBalance,
                                withdrawnCommissions = uiState.withdrawnCommissions,
                                groupOrders = uiState.groupOrdersWithPledges,
                                payoutHistory = uiState.payoutHistory,
                                onUpgradeClick = { viewModel.setShowUpgradePremiumDialog(true) },
                                onRequestPayoutClick = { viewModel.setShowPayoutRequestDialog(true) },
                                onCancelSubscriptionClick = { viewModel.cancelSubscription() }
                            )
                        }

                        // Dialogs
                        if (uiState.showAddCustomerDialog) {
                            AddCustomerDialog(
                                currentLanguage = uiState.language,
                                onDismiss = { viewModel.setShowAddCustomerDialog(false) },
                                onConfirm = { name, phone, neighborhood, notes ->
                                    viewModel.addCustomer(name, phone, neighborhood, notes)
                                }
                            )
                        }

                        if (uiState.showAddRecordDialog && uiState.selectedCustomer != null) {
                            AddRecordDialog(
                                currentLanguage = uiState.language,
                                onDismiss = { viewModel.setShowAddRecordDialog(false) },
                                onConfirm = { amount, desc, type ->
                                    viewModel.addDebtRecord(
                                        customerId = uiState.selectedCustomer!!.customer.id,
                                        amount = amount,
                                        itemDescription = desc,
                                        type = type
                                    )
                                }
                            )
                        }

                        if (uiState.showReminderDialog && uiState.selectedCustomer != null) {
                            ReminderDialog(
                                customerWithBalance = uiState.selectedCustomer!!,
                                currentLanguage = uiState.language,
                                onDismiss = { viewModel.setShowReminderDialog(false) }
                            )
                        }

                        if (uiState.showGroupPledgeDialog && uiState.selectedGroupOrderForPledge != null) {
                            GroupPledgeDialog(
                                order = uiState.selectedGroupOrderForPledge!!,
                                currentLanguage = uiState.language,
                                onDismiss = { viewModel.closePledgeDialog() },
                                onConfirm = { boutiqueName, phone, qty ->
                                    viewModel.pledgeToOrder(
                                        order = uiState.selectedGroupOrderForPledge!!,
                                        boutiqueName = boutiqueName,
                                        phone = phone,
                                        qty = qty
                                    )
                                }
                            )
                        }

                        if (uiState.showCreateGroupOrderDialog) {
                            CreateGroupOrderDialog(
                                currentLanguage = uiState.language,
                                onDismiss = { viewModel.setShowCreateGroupOrderDialog(false) },
                                onConfirm = { title, supplier, regPrice, grpPrice, unit, targetQty, cat ->
                                    viewModel.createGroupOrder(
                                        title = title,
                                        supplierName = supplier,
                                        regularPrice = regPrice,
                                        groupPrice = grpPrice,
                                        unitName = unit,
                                        targetQty = targetQty,
                                        category = cat
                                    )
                                }
                            )
                        }

                        if (uiState.showAppGuideDialog) {
                            AppGuideAndUpdateDialog(
                                currentLanguage = uiState.language,
                                onDismiss = { viewModel.setShowAppGuideDialog(false) }
                            )
                        }

                        if (uiState.showUpgradePremiumDialog) {
                            UpgradePremiumDialog(
                                currentLanguage = uiState.language,
                                onDismiss = { viewModel.setShowUpgradePremiumDialog(false) },
                                onConfirm = { plan, payMethod ->
                                    viewModel.upgradeSubscription(plan, payMethod)
                                }
                            )
                        }

                        if (uiState.showPayoutRequestDialog) {
                            PayoutRequestDialog(
                                availableBalance = uiState.availableCommissionBalance,
                                currentCurrency = uiState.currentCurrency,
                                currentLanguage = uiState.language,
                                onDismiss = { viewModel.setShowPayoutRequestDialog(false) },
                                onConfirm = { amount, provider, phone ->
                                    viewModel.requestCommissionPayout(amount, provider, phone)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
