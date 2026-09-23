package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.CustomerDebtRecord
import com.example.model.H2HProvider
import com.example.model.ProductCategory
import com.example.model.ProductItem
import com.example.model.TransactionRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface H2HProviderDao {
    @Query("SELECT * FROM h2h_providers ORDER BY priorityOrder ASC")
    fun getAllProviders(): Flow<List<H2HProvider>>

    @Query("SELECT * FROM h2h_providers WHERE isActive = 1 ORDER BY priorityOrder ASC")
    fun getActiveProviders(): Flow<List<H2HProvider>>

    @Query("SELECT * FROM h2h_providers WHERE id = :id LIMIT 1")
    suspend fun getProviderById(id: String): H2HProvider?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProvider(provider: H2HProvider)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProviders(providers: List<H2HProvider>)

    @Update
    suspend fun updateProvider(provider: H2HProvider)

    @Delete
    suspend fun deleteProvider(provider: H2HProvider)

    @Query("UPDATE h2h_providers SET balance = :balance, lastBalanceCheck = :timestamp, statusMessage = :status WHERE id = :id")
    suspend fun updateBalance(id: String, balance: Long, timestamp: Long, status: String)

    @Query("UPDATE h2h_providers SET isPrimary = CASE WHEN id = :primaryId THEN 1 ELSE 0 END")
    suspend fun setPrimaryProvider(primaryId: String)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY nominal ASC")
    fun getAllProducts(): Flow<List<ProductItem>>

    @Query("SELECT * FROM products WHERE category = :category ORDER BY nominal ASC")
    fun getProductsByCategory(category: ProductCategory): Flow<List<ProductItem>>

    @Query("SELECT * FROM products WHERE operator = :operator ORDER BY nominal ASC")
    fun getProductsByOperator(operator: String): Flow<List<ProductItem>>

    @Query("SELECT * FROM products WHERE operator = :operator AND category = :category ORDER BY nominal ASC")
    fun getProductsByOperatorAndCategory(operator: String, category: ProductCategory): Flow<List<ProductItem>>

    @Query("SELECT * FROM products WHERE code = :code LIMIT 1")
    suspend fun getProductByCode(code: String): ProductItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductItem)

    @Update
    suspend fun updateProduct(product: ProductItem)

    @Query("UPDATE products SET sellingPrice = basePrice + :markup")
    suspend fun applyGlobalMarkup(markup: Long)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionRecord>>

    @Query("SELECT * FROM transactions WHERE trxStatus = :status ORDER BY timestamp DESC")
    fun getTransactionsByStatus(status: String): Flow<List<TransactionRecord>>

    @Query("SELECT * FROM transactions WHERE refId = :refId LIMIT 1")
    suspend fun getTransactionByRef(refId: String): TransactionRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(trx: TransactionRecord): Long

    @Update
    suspend fun updateTransaction(trx: TransactionRecord)

    @Query("SELECT SUM(sellingPrice) FROM transactions WHERE trxStatus = 'SUKSES'")
    fun getTotalSales(): Flow<Long?>

    @Query("SELECT SUM(profit) FROM transactions WHERE trxStatus = 'SUKSES'")
    fun getTotalProfit(): Flow<Long?>

    @Query("SELECT COUNT(*) FROM transactions WHERE trxStatus = 'SUKSES'")
    fun getSuccessCount(): Flow<Int>
}

@Dao
interface CustomerDebtDao {
    @Query("SELECT * FROM customer_debts ORDER BY createdAt DESC")
    fun getAllDebts(): Flow<List<CustomerDebtRecord>>

    @Query("SELECT * FROM customer_debts WHERE isPaid = 0 ORDER BY createdAt DESC")
    fun getUnpaidDebts(): Flow<List<CustomerDebtRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: CustomerDebtRecord): Long

    @Update
    suspend fun updateDebt(debt: CustomerDebtRecord)

    @Query("UPDATE customer_debts SET isPaid = 1, paidAt = :paidTime WHERE id = :id")
    suspend fun markAsPaid(id: Long, paidTime: Long = System.currentTimeMillis())

    @Query("SELECT SUM(amount) FROM customer_debts WHERE isPaid = 0")
    fun getTotalUnpaid(): Flow<Long?>
}
