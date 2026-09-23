package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.DataRepository
import com.example.model.CustomerDebtRecord
import com.example.model.H2HProvider
import com.example.model.ProductCategory
import com.example.model.ProductItem
import com.example.model.StoreProfile
import com.example.model.TransactionRecord
import com.example.network.H2HBalanceResult
import com.example.network.H2HNetworkEngine
import com.example.network.H2HTransactionResult
import com.example.network.OperatorDetector
import com.example.network.OperatorInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class PulsaUiState(
    val selectedTab: Int = 0,
    val phoneNumber: String = "",
    val detectedOperator: OperatorInfo? = null,
    val selectedCategory: ProductCategory = ProductCategory.PULSA_REGULER,
    val selectedProduct: ProductItem? = null,
    val selectedRouteId: String = "AUTO", // "AUTO" or specific provider ID
    val paymentMethod: String = "TUNAI",   // "TUNAI", "QRIS_TRANSFER", "HUTANG_BON"
    val customerName: String = "",
    val isProcessing: Boolean = false,
    val lastTrxResult: H2HTransactionResult? = null,
    val activeReceiptTrx: TransactionRecord? = null,
    val showReceiptDialog: Boolean = false,
    val storeProfile: StoreProfile = StoreProfile(),
    val balanceCheckStates: Map<String, Boolean> = emptyMap(),
    val snackbarMessage: String? = null
)

class PulsaViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = DataRepository(db)
    private val networkEngine = H2HNetworkEngine()

    private val _uiState = MutableStateFlow(PulsaUiState())
    val uiState: StateFlow<PulsaUiState> = _uiState.asStateFlow()

    val providers: StateFlow<List<H2HProvider>> = repository.allProviders.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val activeProviders: StateFlow<List<H2HProvider>> = repository.activeProviders.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val products: StateFlow<List<ProductItem>> = repository.allProducts.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val transactions: StateFlow<List<TransactionRecord>> = repository.allTransactions.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val debts: StateFlow<List<CustomerDebtRecord>> = repository.allDebts.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val totalSales: StateFlow<Long> = repository.totalSales.combine(MutableStateFlow(0L)) { sales, _ ->
        sales ?: 0L
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val totalProfit: StateFlow<Long> = repository.totalProfit.combine(MutableStateFlow(0L)) { profit, _ ->
        profit ?: 0L
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val totalUnpaidDebt: StateFlow<Long> = repository.totalUnpaidDebts.combine(MutableStateFlow(0L)) { debt, _ ->
        debt ?: 0L
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    init {
        viewModelScope.launch {
            repository.initializeIfEmpty()
        }
    }

    fun setTab(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun onPhoneNumberChanged(number: String) {
        val detected = OperatorDetector.detect(number)
        _uiState.value = _uiState.value.copy(
            phoneNumber = number,
            detectedOperator = detected
        )
    }

    fun onCategoryChanged(category: ProductCategory) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            selectedProduct = null
        )
    }

    fun selectProduct(product: ProductItem) {
        _uiState.value = _uiState.value.copy(selectedProduct = product)
    }

    fun setRouteId(routeId: String) {
        _uiState.value = _uiState.value.copy(selectedRouteId = routeId)
    }

    fun setPaymentMethod(method: String) {
        _uiState.value = _uiState.value.copy(paymentMethod = method)
    }

    fun setCustomerName(name: String) {
        _uiState.value = _uiState.value.copy(customerName = name)
    }

    fun clearReceiptDialog() {
        _uiState.value = _uiState.value.copy(
            showReceiptDialog = false,
            activeReceiptTrx = null
        )
    }

    fun openReceiptForTransaction(trx: TransactionRecord) {
        _uiState.value = _uiState.value.copy(
            activeReceiptTrx = trx,
            showReceiptDialog = true
        )
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun showSnackbar(msg: String) {
        _uiState.value = _uiState.value.copy(snackbarMessage = msg)
    }

    fun executeTransaction() {
        val state = _uiState.value
        val product = state.selectedProduct ?: return
        val target = state.phoneNumber.trim()

        if (target.length < 8) {
            showSnackbar("Nomor tujuan minimal 8 digit.")
            return
        }

        if (state.paymentMethod == "HUTANG_BON" && state.customerName.isBlank()) {
            showSnackbar("Untuk metode Catat Bon / Hutang, wajib isi nama pelanggan!")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isProcessing = true)

            val allActive = activeProviders.value
            val candidateProviders = if (state.selectedRouteId == "AUTO") {
                allActive.sortedBy { it.priorityOrder }
            } else {
                allActive.filter { it.id == state.selectedRouteId }
            }

            val timestamp = System.currentTimeMillis()
            val dateCode = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(timestamp))
            val refId = "TRX-$dateCode-${Random.nextInt(10000, 99999)}"

            val result = networkEngine.executeWithFailover(
                providers = candidateProviders,
                product = product,
                targetNumber = target,
                refId = refId
            )

            val profit = if (result.isSuccess) {
                (product.sellingPrice - product.basePrice).coerceAtLeast(0L)
            } else 0L

            val paymentStatus = when (state.paymentMethod) {
                "HUTANG_BON" -> "BELUM_LUNAS"
                "QRIS_TRANSFER" -> "LUNAS_TRANSFER"
                else -> "LUNAS_TUNAI"
            }

            val trxRecord = TransactionRecord(
                refId = refId,
                targetNumber = target,
                productCode = product.code,
                productName = product.name,
                category = product.category.name,
                operator = product.operator,
                providerId = result.providerUsed.id,
                providerName = result.providerUsed.name,
                basePrice = product.basePrice,
                sellingPrice = product.sellingPrice,
                profit = profit,
                paymentStatus = paymentStatus,
                customerName = state.customerName,
                trxStatus = result.status,
                serialNumber = result.serialNumber,
                rcMessage = result.message,
                requestPayload = result.requestPayload,
                responsePayload = result.responsePayload,
                failoverReason = result.failoverLog,
                timestamp = timestamp
            )

            val insertedId = repository.insertTransaction(trxRecord)
            val savedRecord = trxRecord.copy(id = insertedId)

            // If debt, record to customer debts
            if (state.paymentMethod == "HUTANG_BON") {
                repository.saveCustomerDebt(
                    CustomerDebtRecord(
                        customerName = state.customerName,
                        customerPhone = target,
                        transactionRefId = refId,
                        productName = product.name,
                        targetNumber = target,
                        amount = product.sellingPrice,
                        isPaid = false,
                        notes = "Pembelian pulsa via ${result.providerUsed.name}"
                    )
                )
            }

            // Update provider balance in DB if success
            if (result.isSuccess && result.providerUsed.id != "none") {
                val newBal = (result.providerUsed.balance - product.basePrice).coerceAtLeast(0L)
                repository.updateProviderBalance(result.providerUsed.id, newBal, "Transaksi Sukses")
            }

            _uiState.value = _uiState.value.copy(
                isProcessing = false,
                lastTrxResult = result,
                activeReceiptTrx = savedRecord,
                showReceiptDialog = true,
                selectedProduct = null,
                customerName = "",
                snackbarMessage = if (result.isSuccess) "Transaksi Sukses via ${result.providerUsed.name}!" else "Transaksi ${result.status}: ${result.message}"
            )
        }
    }

    fun checkProviderBalance(provider: H2HProvider) {
        viewModelScope.launch {
            val currentMap = _uiState.value.balanceCheckStates.toMutableMap()
            currentMap[provider.id] = true
            _uiState.value = _uiState.value.copy(balanceCheckStates = currentMap)

            val balanceResult = networkEngine.checkBalance(provider)

            repository.updateProviderBalance(
                id = provider.id,
                balance = balanceResult.balance,
                status = balanceResult.message
            )

            currentMap[provider.id] = false
            _uiState.value = _uiState.value.copy(
                balanceCheckStates = currentMap,
                snackbarMessage = "Saldo ${provider.name}: Rp ${balanceResult.balance}"
            )
        }
    }

    fun checkAllBalances() {
        viewModelScope.launch {
            val list = activeProviders.value
            for (p in list) {
                checkProviderBalance(p)
            }
        }
    }

    fun saveProvider(provider: H2HProvider) {
        viewModelScope.launch {
            repository.saveProvider(provider)
            showSnackbar("Provider ${provider.name} berhasil disimpan.")
        }
    }

    fun toggleProviderActive(provider: H2HProvider) {
        viewModelScope.launch {
            repository.updateProvider(provider.copy(isActive = !provider.isActive))
        }
    }

    fun setPrimaryProvider(providerId: String) {
        viewModelScope.launch {
            repository.setPrimaryProvider(providerId)
            showSnackbar("Provider utama berhasil diubah.")
        }
    }

    fun deleteProvider(provider: H2HProvider) {
        viewModelScope.launch {
            repository.deleteProvider(provider)
            showSnackbar("Provider ${provider.name} dihapus.")
        }
    }

    fun markDebtPaid(debtId: Long) {
        viewModelScope.launch {
            repository.markDebtAsPaid(debtId)
            showSnackbar("Tagihan ditandai LUNAS!")
        }
    }

    fun applyGlobalMarkup(markupAmount: Long) {
        viewModelScope.launch {
            repository.applyGlobalMarkup(markupAmount)
            showSnackbar("Markup margin Rp $markupAmount berhasil diterapkan ke semua produk.")
        }
    }

    fun updateStoreProfile(profile: StoreProfile) {
        _uiState.value = _uiState.value.copy(storeProfile = profile)
        showSnackbar("Profil konter berhasil diperbarui.")
    }
}
