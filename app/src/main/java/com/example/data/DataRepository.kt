package com.example.data

import com.example.model.CustomerDebtRecord
import com.example.model.H2HProvider
import com.example.model.ProductCategory
import com.example.model.ProductItem
import com.example.model.StoreProfile
import com.example.model.TransactionRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class DataRepository(private val db: AppDatabase) {
    private val providerDao = db.h2hProviderDao()
    private val productDao = db.productDao()
    private val transactionDao = db.transactionDao()
    private val debtDao = db.customerDebtDao()

    val allProviders: Flow<List<H2HProvider>> = providerDao.getAllProviders()
    val activeProviders: Flow<List<H2HProvider>> = providerDao.getActiveProviders()
    val allProducts: Flow<List<ProductItem>> = productDao.getAllProducts()
    val allTransactions: Flow<List<TransactionRecord>> = transactionDao.getAllTransactions()
    val allDebts: Flow<List<CustomerDebtRecord>> = debtDao.getAllDebts()
    val totalSales: Flow<Long?> = transactionDao.getTotalSales()
    val totalProfit: Flow<Long?> = transactionDao.getTotalProfit()
    val totalUnpaidDebts: Flow<Long?> = debtDao.getTotalUnpaid()

    suspend fun initializeIfEmpty() = withContext(Dispatchers.IO) {
        val existingProviders = providerDao.getAllProviders().first()
        if (existingProviders.isEmpty()) {
            providerDao.insertProviders(DefaultCatalogData.initialProviders)
        }

        val existingProducts = productDao.getAllProducts().first()
        if (existingProducts.isEmpty()) {
            productDao.insertProducts(DefaultCatalogData.initialProducts)
        }
    }

    suspend fun saveProvider(provider: H2HProvider) = withContext(Dispatchers.IO) {
        providerDao.insertProvider(provider)
    }

    suspend fun updateProvider(provider: H2HProvider) = withContext(Dispatchers.IO) {
        providerDao.updateProvider(provider)
    }

    suspend fun deleteProvider(provider: H2HProvider) = withContext(Dispatchers.IO) {
        providerDao.deleteProvider(provider)
    }

    suspend fun setPrimaryProvider(providerId: String) = withContext(Dispatchers.IO) {
        providerDao.setPrimaryProvider(providerId)
    }

    suspend fun updateProviderBalance(id: String, balance: Long, status: String) = withContext(Dispatchers.IO) {
        providerDao.updateBalance(id, balance, System.currentTimeMillis(), status)
    }

    suspend fun insertTransaction(trx: TransactionRecord): Long = withContext(Dispatchers.IO) {
        transactionDao.insertTransaction(trx)
    }

    suspend fun updateTransaction(trx: TransactionRecord) = withContext(Dispatchers.IO) {
        transactionDao.updateTransaction(trx)
    }

    suspend fun getTransactionByRef(refId: String): TransactionRecord? = withContext(Dispatchers.IO) {
        transactionDao.getTransactionByRef(refId)
    }

    suspend fun saveCustomerDebt(debt: CustomerDebtRecord): Long = withContext(Dispatchers.IO) {
        debtDao.insertDebt(debt)
    }

    suspend fun markDebtAsPaid(debtId: Long) = withContext(Dispatchers.IO) {
        debtDao.markAsPaid(debtId)
    }

    suspend fun applyGlobalMarkup(markup: Long) = withContext(Dispatchers.IO) {
        productDao.applyGlobalMarkup(markup)
    }

    suspend fun updateProduct(product: ProductItem) = withContext(Dispatchers.IO) {
        productDao.updateProduct(product)
    }

    fun getProductsByCategory(category: ProductCategory): Flow<List<ProductItem>> {
        return productDao.getProductsByCategory(category)
    }
}
