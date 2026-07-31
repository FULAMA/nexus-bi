package com.example.nexusbi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.nexusbi.data.local.entities.CustomerEntity
import com.example.nexusbi.data.local.entities.DebtRecordEntity
import com.example.nexusbi.data.local.entities.GroupOrderEntity
import com.example.nexusbi.data.local.entities.GroupOrderPledgeEntity
import com.example.nexusbi.data.repository.CarnetRepository
import com.example.nexusbi.ui.language.AppLanguage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import com.example.nexusbi.data.api.CommodityItem
import com.example.nexusbi.data.api.CurrencyService
import com.example.nexusbi.data.api.GeminiAiService
import com.example.nexusbi.data.api.MarketPricesService
import com.example.nexusbi.data.api.AiRiskEvaluation

private data class Tuple3<A, B, C>(val a: A, val b: B, val c: C)
private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

private data class UserPrefs(
    val lang: AppLanguage,
    val query: String,
    val filter: CustomerFilter,
    val sort: CustomerSort,
    val currency: String
)

private data class ApiData(
    val rates: Map<String, Double>,
    val commodities: List<CommodityItem>,
    val riskMap: Map<Long, AiRiskEvaluation>,
    val advice: String,
    val loading: Boolean
)

private data class DialogData(
    val selectedCustId: Long?,
    val insActive: Boolean,
    val addCust: Boolean,
    val addRec: Boolean,
    val rem: Boolean,
    val pledge: Boolean,
    val pledgeOrder: GroupOrderEntity?,
    val createOrder: Boolean,
    val currencyDlg: Boolean,
    val aiDlg: Boolean,
    val guideDlg: Boolean
)

private data class BaseStateGroup(
    val prefs: UserPrefs,
    val api: ApiData,
    val dialogs: DialogData
)

data class CustomerWithBalance(
    val customer: CustomerEntity,
    val totalDebt: Double,
    val totalPaid: Double,
    val netBalance: Double,
    val lastTransactionDate: Long?
)

data class GroupOrderWithPledges(
    val order: GroupOrderEntity,
    val pledges: List<GroupOrderPledgeEntity>
)

enum class SubscriptionPlan(
    val title: String,
    val priceFcfa: Double,
    val priceUsd: Double,
    val commissionBoost: Double,
    val features: List<String>
) {
    FREE(
        title = "Gratuit (Standard)",
        priceFcfa = 0.0,
        priceUsd = 0.0,
        commissionBoost = 0.0,
        features = listOf("Gestion jusqu'à 20 clients", "Carnet de crédit local", "Participation aux achats groupés")
    ),
    MONTHLY_PREMIUM(
        title = "Premium Mensuel",
        priceFcfa = 3000.0,
        priceUsd = 5.0,
        commissionBoost = 0.5,
        features = listOf(
            "Clients & Transactions illimités",
            "Exportation illimitée PDF & CSV",
            "Rappels WhatsApp & SMS automatiques",
            "Conseiller IA & Évaluation du risque crédit illimités",
            "Micro-Assurance Crédit Shield incluse",
            "Boost de Commission (+0.5% sur achats groupés)"
        )
    ),
    ANNUAL_PREMIUM(
        title = "Premium Annuel (2 mois offerts)",
        priceFcfa = 30000.0,
        priceUsd = 50.0,
        commissionBoost = 0.8,
        features = listOf(
            "Tous les avantages du Premium Mensuel",
            "Économisez 6 000 FCFA / an (2 mois offerts)",
            "Badge 'Boutique Vérifiée' sur le réseau coopératif",
            "Priorité sur les livraisons de commande groupée",
            "Support prioritaire 24/7"
        )
    )
}

enum class SubscriptionStatus {
    ACTIVE,
    TRIAL,
    INACTIVE
}

data class CommissionPayoutRecord(
    val id: String,
    val amountFcfa: Double,
    val provider: String,
    val phoneNumber: String,
    val dateTimestamp: Long,
    val status: String
)

private data class MonetizationData(
    val plan: SubscriptionPlan,
    val status: SubscriptionStatus,
    val payMethod: String,
    val renewDate: String,
    val payoutHistory: List<CommissionPayoutRecord>,
    val showUpgradeDlg: Boolean,
    val showPayoutDlg: Boolean
)

enum class CustomerFilter {
    ALL, WITH_DEBT, PAID
}

enum class CustomerSort {
    BALANCE_DESC, NAME_ASC, RECENT
}

data class CarnetUiState(
    val language: AppLanguage = AppLanguage.FRENCH,
    val searchQuery: String = "",
    val filterType: CustomerFilter = CustomerFilter.ALL,
    val sortType: CustomerSort = CustomerSort.BALANCE_DESC,
    val currentCurrency: String = "FCFA",
    val exchangeRates: Map<String, Double> = mapOf("USD" to 1.0, "FCFA" to 600.0, "CDF" to 2850.0, "EUR" to 0.92),
    val marketCommodities: List<CommodityItem> = emptyList(),
    val aiRiskEvaluationMap: Map<Long, AiRiskEvaluation> = emptyMap(),
    val aiAdviceText: String = "",
    val isAiLoading: Boolean = false,
    val customersWithBalance: List<CustomerWithBalance> = emptyList(),
    val totalCreditDueAll: Double = 0.0,
    val totalCollectedThisMonth: Double = 0.0,
    val selectedCustomer: CustomerWithBalance? = null,
    val selectedCustomerRecords: List<DebtRecordEntity> = emptyList(),
    val groupOrdersWithPledges: List<GroupOrderWithPledges> = emptyList(),
    val totalEstimatedSavings: Double = 0.0,
    val isInsuranceShieldActive: Boolean = false,
    val showAddCustomerDialog: Boolean = false,
    val showAddRecordDialog: Boolean = false,
    val showReminderDialog: Boolean = false,
    val showGroupPledgeDialog: Boolean = false,
    val selectedGroupOrderForPledge: GroupOrderEntity? = null,
    val showCreateGroupOrderDialog: Boolean = false,
    val showCurrencyConverterDialog: Boolean = false,
    val showAiAdvisorDialog: Boolean = false,
    val showAppGuideDialog: Boolean = false,
    val subscriptionPlan: SubscriptionPlan = SubscriptionPlan.MONTHLY_PREMIUM,
    val subscriptionStatus: SubscriptionStatus = SubscriptionStatus.ACTIVE,
    val subscriptionRenewalDate: String = "15 Août 2026",
    val subscriptionPaymentMethod: String = "Orange Money (••• 8921)",
    val totalCommissionsEarned: Double = 42500.0,
    val availableCommissionBalance: Double = 22000.0,
    val withdrawnCommissions: Double = 20500.0,
    val payoutHistory: List<CommissionPayoutRecord> = emptyList(),
    val showUpgradePremiumDialog: Boolean = false,
    val showPayoutRequestDialog: Boolean = false
)

class CarnetViewModel(private val repository: CarnetRepository) : ViewModel() {

    private val _language = MutableStateFlow(AppLanguage.FRENCH)
    private val _searchQuery = MutableStateFlow("")
    private val _filterType = MutableStateFlow(CustomerFilter.ALL)
    private val _sortType = MutableStateFlow(CustomerSort.BALANCE_DESC)
    private val _currentCurrency = MutableStateFlow("FCFA")
    private val _exchangeRates = MutableStateFlow<Map<String, Double>>(mapOf("USD" to 1.0, "FCFA" to 600.0, "CDF" to 2850.0, "EUR" to 0.92))
    private val _marketCommodities = MutableStateFlow<List<CommodityItem>>(emptyList())
    private val _aiRiskEvaluationMap = MutableStateFlow<Map<Long, AiRiskEvaluation>>(emptyMap())
    private val _aiAdviceText = MutableStateFlow("")
    private val _isAiLoading = MutableStateFlow(false)

    private val _selectedCustomerId = MutableStateFlow<Long?>(null)
    private val _isInsuranceActive = MutableStateFlow(false)

    // Dialog states
    private val _showAddCustomerDialog = MutableStateFlow(false)
    private val _showAddRecordDialog = MutableStateFlow(false)
    private val _showReminderDialog = MutableStateFlow(false)
    private val _showGroupPledgeDialog = MutableStateFlow(false)
    private val _selectedGroupOrderForPledge = MutableStateFlow<GroupOrderEntity?>(null)
    private val _showCreateGroupOrderDialog = MutableStateFlow(false)
    private val _showCurrencyConverterDialog = MutableStateFlow(false)
    private val _showAiAdvisorDialog = MutableStateFlow(false)
    private val _showAppGuideDialog = MutableStateFlow(false)

    // Monetization & Subscription states
    private val _subscriptionPlan = MutableStateFlow(SubscriptionPlan.MONTHLY_PREMIUM)
    private val _subscriptionStatus = MutableStateFlow(SubscriptionStatus.ACTIVE)
    private val _subscriptionPaymentMethod = MutableStateFlow("Orange Money (••• 8921)")
    private val _subscriptionRenewalDate = MutableStateFlow("15 Août 2026")
    private val _payoutHistory = MutableStateFlow<List<CommissionPayoutRecord>>(
        listOf(
            CommissionPayoutRecord("PO-1092", 12500.0, "Orange Money", "+243 89 123 4567", System.currentTimeMillis() - 86400000L * 3, "SUCCESS"),
            CommissionPayoutRecord("PO-1088", 8000.0, "Wave", "+221 77 987 6543", System.currentTimeMillis() - 86400000L * 10, "SUCCESS")
        )
    )
    private val _showUpgradePremiumDialog = MutableStateFlow(false)
    private val _showPayoutRequestDialog = MutableStateFlow(false)

    val customersFlow = repository.allCustomers
    val recordsFlow = repository.allRecords
    val groupOrdersFlow = repository.allGroupOrders

    init {
        refreshExchangeRates()
        refreshMarketPrices()
    }

    fun refreshExchangeRates() {
        viewModelScope.launch {
            val rates = CurrencyService.getExchangeRates()
            _exchangeRates.value = rates
        }
    }

    fun refreshMarketPrices() {
        viewModelScope.launch {
            val commodities = MarketPricesService.getLatestMarketPrices()
            _marketCommodities.value = commodities
        }
    }

    fun setCurrency(currencyCode: String) {
        _currentCurrency.value = currencyCode
    }

    fun setShowCurrencyConverterDialog(show: Boolean) {
        _showCurrencyConverterDialog.value = show
    }

    fun setShowAiAdvisorDialog(show: Boolean) {
        _showAiAdvisorDialog.value = show
    }

    fun evaluateCustomerRiskWithAi(customer: CustomerWithBalance) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val evaluation = GeminiAiService.evaluateCustomerRisk(
                customerName = customer.customer.name,
                netBalanceFcfa = customer.netBalance,
                totalPaidFcfa = customer.totalPaid,
                recordCount = 5
            )
            val updated = _aiRiskEvaluationMap.value.toMutableMap()
            updated[customer.customer.id] = evaluation
            _aiRiskEvaluationMap.value = updated
            _isAiLoading.value = false
        }
    }

    fun askAiAdvisor(query: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            val advice = GeminiAiService.generateContent("Tu es le Conseiller IA Carnet Kredi. $query")
            _aiAdviceText.value = advice
            _isAiLoading.value = false
        }
    }

    private val prefsFlow = combine(_language, _searchQuery, _filterType, _sortType, _currentCurrency) { l, q, f, s, c ->
        UserPrefs(l, q, f, s, c)
    }

    private val apiDataFlow = combine(_exchangeRates, _marketCommodities, _aiRiskEvaluationMap, _aiAdviceText, _isAiLoading) { er, mc, ar, aa, il ->
        ApiData(er, mc, ar, aa, il)
    }

    private val dialogGroup1 = combine(
        _selectedCustomerId, _isInsuranceActive, _showAddCustomerDialog, _showAddRecordDialog
    ) { sc, ia, ac, ar ->
        Tuple4(sc, ia, ac, ar)
    }

    private val dialogGroup2 = combine(
        _showReminderDialog, _showGroupPledgeDialog, _selectedGroupOrderForPledge, _showCreateGroupOrderDialog
    ) { rm, gp, so, co ->
        Tuple4(rm, gp, so, co)
    }

    private val dialogGroup3 = combine(
        _showCurrencyConverterDialog, _showAiAdvisorDialog, _showAppGuideDialog
    ) { cc, ai, gd ->
        Tuple3(cc, ai, gd)
    }

    private val dialogDataFlow = combine(dialogGroup1, dialogGroup2, dialogGroup3) { d1, d2, d3 ->
        DialogData(
            selectedCustId = d1.a,
            insActive = d1.b,
            addCust = d1.c,
            addRec = d1.d,
            rem = d2.a,
            pledge = d2.b,
            pledgeOrder = d2.c,
            createOrder = d2.d,
            currencyDlg = d3.a,
            aiDlg = d3.b,
            guideDlg = d3.c
        )
    }

    private val dbDataFlow = combine(customersFlow, recordsFlow, groupOrdersFlow) { c, r, g ->
        Tuple3(c, r, g)
    }

    private val monetGroup1 = combine(
        _subscriptionPlan, _subscriptionStatus, _subscriptionPaymentMethod, _subscriptionRenewalDate
    ) { plan, status, payMethod, renewDate ->
        Tuple4(plan, status, payMethod, renewDate)
    }

    private val monetGroup2 = combine(
        _payoutHistory, _showUpgradePremiumDialog, _showPayoutRequestDialog
    ) { history, upgradeDlg, payoutDlg ->
        Tuple3(history, upgradeDlg, payoutDlg)
    }

    private val monetizationFlow = combine(monetGroup1, monetGroup2) { g1, g2 ->
        MonetizationData(
            plan = g1.a,
            status = g1.b,
            payMethod = g1.c,
            renewDate = g1.d,
            payoutHistory = g2.a,
            showUpgradeDlg = g2.b,
            showPayoutDlg = g2.c
        )
    }

    val uiState: StateFlow<CarnetUiState> = combine(
        prefsFlow,
        apiDataFlow,
        dialogDataFlow,
        dbDataFlow,
        monetizationFlow
    ) { prefs, api, dialogs, dbData, monet ->
        val customers = dbData.a
        val records = dbData.b
        val groupOrders = dbData.c

        // Calculate balances
        val customerBalances = customers.map { customer ->
            val custRecords = records.filter { it.customerId == customer.id }
            val totalDebt = custRecords.filter { it.type == "CREDIT" }.sumOf { it.amount }
            val totalPaid = custRecords.filter { it.type == "PAYMENT" }.sumOf { it.amount } +
                    custRecords.filter { it.type == "CREDIT" && it.isPaid }.sumOf { it.amountPaid }
            val net = (totalDebt - totalPaid).coerceAtLeast(0.0)
            val lastDate = custRecords.maxOfOrNull { it.date } ?: customer.createdAt

            CustomerWithBalance(
                customer = customer,
                totalDebt = totalDebt,
                totalPaid = totalPaid,
                netBalance = net,
                lastTransactionDate = lastDate
            )
        }

        // Filter by search query & filterType
        var filteredCustomers = customerBalances.filter {
            it.customer.name.contains(prefs.query, ignoreCase = true) ||
                    it.customer.phone.contains(prefs.query, ignoreCase = true) ||
                    it.customer.neighborhood.contains(prefs.query, ignoreCase = true)
        }.filter {
            when (prefs.filter) {
                CustomerFilter.ALL -> true
                CustomerFilter.WITH_DEBT -> it.netBalance > 0
                CustomerFilter.PAID -> it.netBalance <= 0
            }
        }

        // Sort customers
        filteredCustomers = when (prefs.sort) {
            CustomerSort.BALANCE_DESC -> filteredCustomers.sortedByDescending { it.netBalance }
            CustomerSort.NAME_ASC -> filteredCustomers.sortedBy { it.customer.name.lowercase() }
            CustomerSort.RECENT -> filteredCustomers.sortedByDescending { it.lastTransactionDate ?: 0L }
        }

        val totalCreditDueAll = customerBalances.sumOf { it.netBalance }
        val totalCollectedThisMonth = customerBalances.sumOf { it.totalPaid }

        val selectedCustWithBal = customerBalances.find { it.customer.id == dialogs.selectedCustId }
        val selectedRecords = if (dialogs.selectedCustId != null) {
            records.filter { it.customerId == dialogs.selectedCustId }.sortedByDescending { it.date }
        } else emptyList()

        // Group Orders
        val ordersWithPledges = groupOrders.map { order ->
            GroupOrderWithPledges(order = order, pledges = emptyList())
        }

        val totalEstimatedSavings = groupOrders.sumOf { order ->
            val savingsPerUnit = (order.regularUnitPrice - order.groupUnitPrice).coerceAtLeast(0.0)
            savingsPerUnit * order.currentQuantity
        }

        // Monetization calculations
        val totalVolumeAllGroupOrders = groupOrders.sumOf { it.currentQuantity * it.groupUnitPrice }
        val baseCommissionRatePct = 2.5 + monet.plan.commissionBoost
        val totalCommissionsEarned = (totalVolumeAllGroupOrders * (baseCommissionRatePct / 100.0)) + 12500.0
        val withdrawnCommissions = monet.payoutHistory.sumOf { it.amountFcfa }
        val availableCommissionBalance = (totalCommissionsEarned - withdrawnCommissions).coerceAtLeast(0.0)

        CarnetUiState(
            language = prefs.lang,
            searchQuery = prefs.query,
            filterType = prefs.filter,
            sortType = prefs.sort,
            currentCurrency = prefs.currency,
            exchangeRates = api.rates,
            marketCommodities = api.commodities,
            aiRiskEvaluationMap = api.riskMap,
            aiAdviceText = api.advice,
            isAiLoading = api.loading,
            customersWithBalance = filteredCustomers,
            totalCreditDueAll = totalCreditDueAll,
            totalCollectedThisMonth = totalCollectedThisMonth,
            selectedCustomer = selectedCustWithBal,
            selectedCustomerRecords = selectedRecords,
            groupOrdersWithPledges = ordersWithPledges,
            totalEstimatedSavings = totalEstimatedSavings,
            isInsuranceShieldActive = dialogs.insActive,
            showAddCustomerDialog = dialogs.addCust,
            showAddRecordDialog = dialogs.addRec,
            showReminderDialog = dialogs.rem,
            showGroupPledgeDialog = dialogs.pledge,
            selectedGroupOrderForPledge = dialogs.pledgeOrder,
            showCreateGroupOrderDialog = dialogs.createOrder,
            showCurrencyConverterDialog = dialogs.currencyDlg,
            showAiAdvisorDialog = dialogs.aiDlg,
            showAppGuideDialog = dialogs.guideDlg,
            subscriptionPlan = monet.plan,
            subscriptionStatus = monet.status,
            subscriptionRenewalDate = monet.renewDate,
            subscriptionPaymentMethod = monet.payMethod,
            totalCommissionsEarned = totalCommissionsEarned,
            availableCommissionBalance = availableCommissionBalance,
            withdrawnCommissions = withdrawnCommissions,
            payoutHistory = monet.payoutHistory,
            showUpgradePremiumDialog = monet.showUpgradeDlg,
            showPayoutRequestDialog = monet.showPayoutDlg
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CarnetUiState()
    )

    fun setFilterType(filter: CustomerFilter) {
        _filterType.value = filter
    }

    fun setSortType(sort: CustomerSort) {
        _sortType.value = sort
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCustomer(customerWithBalance: CustomerWithBalance?) {
        _selectedCustomerId.value = customerWithBalance?.customer?.id
    }

    fun setShowAddCustomerDialog(show: Boolean) {
        _showAddCustomerDialog.value = show
    }

    fun setShowAddRecordDialog(show: Boolean) {
        _showAddRecordDialog.value = show
    }

    fun setShowReminderDialog(show: Boolean) {
        _showReminderDialog.value = show
    }

    fun openPledgeDialog(order: GroupOrderEntity) {
        _selectedGroupOrderForPledge.value = order
        _showGroupPledgeDialog.value = true
    }

    fun closePledgeDialog() {
        _showGroupPledgeDialog.value = false
        _selectedGroupOrderForPledge.value = null
    }

    fun setShowCreateGroupOrderDialog(show: Boolean) {
        _showCreateGroupOrderDialog.value = show
    }

    fun toggleInsuranceShield() {
        _isInsuranceActive.value = !_isInsuranceActive.value
    }

    fun addCustomer(name: String, phone: String, neighborhood: String, notes: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val newCust = CustomerEntity(
                name = name.trim(),
                phone = phone.trim(),
                neighborhood = neighborhood.trim(),
                notes = notes.trim()
            )
            val newId = repository.insertCustomer(newCust)
            _selectedCustomerId.value = newId
            _showAddCustomerDialog.value = false
        }
    }

    fun addDebtRecord(customerId: Long, amount: Double, itemDescription: String, type: String) {
        if (amount <= 0) return
        viewModelScope.launch {
            val record = DebtRecordEntity(
                customerId = customerId,
                amount = amount,
                itemDescription = itemDescription.ifBlank { if (type == "CREDIT") "Crédit boutique" else "Paiement en espèces" },
                type = type,
                isPaid = type == "PAYMENT"
            )
            repository.insertRecord(record)
            _showAddRecordDialog.value = false
        }
    }

    fun pledgeToOrder(order: GroupOrderEntity, boutiqueName: String, phone: String, qty: Int) {
        if (qty <= 0) return
        viewModelScope.launch {
            val pledge = GroupOrderPledgeEntity(
                orderId = order.id,
                boutiqueName = boutiqueName.ifBlank { "Boutique Kredi" },
                phone = phone.ifBlank { "+243000000000" },
                quantity = qty
            )
            repository.pledgeToOrder(pledge, order)
            closePledgeDialog()
        }
    }

    fun createGroupOrder(
        title: String,
        supplierName: String,
        regularPrice: Double,
        groupPrice: Double,
        unitName: String,
        targetQty: Int,
        category: String
    ) {
        if (title.isBlank() || targetQty <= 0) return
        viewModelScope.launch {
            val order = GroupOrderEntity(
                title = title.trim(),
                supplierName = supplierName.ifBlank { "Grossiste Local" },
                regularUnitPrice = regularPrice,
                groupUnitPrice = groupPrice,
                unitName = unitName.ifBlank { "unité" },
                targetQuantity = targetQty,
                currentQuantity = 0,
                deadlineTimestamp = System.currentTimeMillis() + (5 * 86400000L),
                status = "OPEN",
                category = category
            )
            repository.insertGroupOrder(order)
            _showCreateGroupOrderDialog.value = false
        }
    }

    fun deleteCustomer(customer: CustomerEntity) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
            if (_selectedCustomerId.value == customer.id) {
                _selectedCustomerId.value = null
            }
        }
    }

    fun setShowAppGuideDialog(show: Boolean) {
        _showAppGuideDialog.value = show
    }

    fun setShowUpgradePremiumDialog(show: Boolean) {
        _showUpgradePremiumDialog.value = show
    }

    fun setShowPayoutRequestDialog(show: Boolean) {
        _showPayoutRequestDialog.value = show
    }

    fun upgradeSubscription(plan: SubscriptionPlan, paymentMethod: String) {
        _subscriptionPlan.value = plan
        _subscriptionStatus.value = SubscriptionStatus.ACTIVE
        _subscriptionPaymentMethod.value = paymentMethod
        _subscriptionRenewalDate.value = "30 Août 2026"
        _showUpgradePremiumDialog.value = false
    }

    fun cancelSubscription() {
        _subscriptionPlan.value = SubscriptionPlan.FREE
        _subscriptionStatus.value = SubscriptionStatus.INACTIVE
        _subscriptionPaymentMethod.value = "Aucun"
    }

    fun requestCommissionPayout(amountFcfa: Double, provider: String, phone: String) {
        val newRecord = CommissionPayoutRecord(
            id = "PO-${(1000..9999).random()}",
            amountFcfa = amountFcfa,
            provider = provider,
            phoneNumber = phone,
            dateTimestamp = System.currentTimeMillis(),
            status = "SUCCESS"
        )
        _payoutHistory.value = listOf(newRecord) + _payoutHistory.value
        _showPayoutRequestDialog.value = false
    }
}

class CarnetViewModelFactory(private val repository: CarnetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarnetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CarnetViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
