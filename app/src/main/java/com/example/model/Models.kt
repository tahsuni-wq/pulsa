package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ProviderType {
    DIGIFLAZZ,
    VIP_RESELLER,
    TRIPAY,
    PORTALPULSA,
    CUSTOM_REST
}

@Entity(tableName = "h2h_providers")
data class H2HProvider(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: ProviderType,
    val apiUrl: String,
    val apiUsername: String, // username or api_id
    val apiKey: String,
    val secretOrPin: String, // pin or secret or webhook secret
    val isActive: Boolean = true,
    val isPrimary: Boolean = false,
    val priorityOrder: Int = 1, // 1 = Primary, 2 = Secondary, 3 = Tertiary
    val isSandbox: Boolean = false,
    val balance: Long = 0L,
    val lastBalanceCheck: Long = 0L,
    val statusMessage: String = "Ready"
)

enum class ProductCategory {
    PULSA_REGULER,
    PAKET_DATA,
    TOKEN_PLN,
    EWALLET,
    VOUCHER_GAME,
    TAGIHAN_PPOB
}

@Entity(tableName = "products")
data class ProductItem(
    @PrimaryKey
    val code: String,
    val name: String,
    val category: ProductCategory,
    val operator: String,
    val nominal: Long,
    val basePrice: Long,      // Harga modal dari H2H
    val sellingPrice: Long,   // Harga jual ke pelanggan
    val description: String,
    val digiflazzSku: String = "",
    val vipResellerSku: String = "",
    val tripaySku: String = "",
    val customSku: String = "",
    val isAvailable: Boolean = true
)

@Entity(tableName = "transactions")
data class TransactionRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val refId: String,
    val targetNumber: String,
    val productCode: String,
    val productName: String,
    val category: String,
    val operator: String,
    val providerId: String,
    val providerName: String,
    val basePrice: Long,
    val sellingPrice: Long,
    val profit: Long,
    val paymentStatus: String, // "LUNAS_TUNAI", "LUNAS_TRANSFER", "BELUM_LUNAS"
    val customerName: String = "",
    val trxStatus: String,      // "PENDING", "SUKSES", "GAGAL"
    val serialNumber: String = "",
    val rcMessage: String = "",
    val requestPayload: String = "",
    val responsePayload: String = "",
    val failoverReason: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "customer_debts")
data class CustomerDebtRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerName: String,
    val customerPhone: String,
    val transactionRefId: String,
    val productName: String,
    val targetNumber: String,
    val amount: Long,
    val isPaid: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val paidAt: Long? = null,
    val notes: String = ""
)

data class StoreProfile(
    val storeName: String = "KONTER PULSAPAY",
    val address: String = "Jl. Merdeka No. 45, Indonesia",
    val phone: String = "0812-3456-7890",
    val footerMessage: String = "Terima kasih atas kunjungan Anda!\nSimpan struk ini sebagai bukti pembayaran sah."
)
