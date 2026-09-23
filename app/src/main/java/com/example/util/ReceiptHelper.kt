package com.example.util

import android.content.Context
import android.content.Intent
import com.example.model.StoreProfile
import com.example.model.TransactionRecord
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReceiptHelper {
    fun formatRupiah(amount: Long): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        format.maximumFractionDigits = 0
        return format.format(amount).replace("Rp", "Rp ")
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("id", "ID"))
        return sdf.format(Date(timestamp))
    }

    fun generateReceiptText(trx: TransactionRecord, store: StoreProfile): String {
        val dateStr = formatDate(trx.timestamp)
        val formattedPrice = formatRupiah(trx.sellingPrice)
        val line = "--------------------------------"

        return buildString {
            appendLine("       ${store.storeName.uppercase()}       ")
            appendLine(store.address)
            appendLine("Telp/WA: ${store.phone}")
            appendLine(line)
            appendLine("WAKTU  : $dateStr")
            appendLine("NO REFF: ${trx.refId}")
            appendLine("OPERATOR: ${trx.operator.uppercase()}")
            appendLine("PRODUK : ${trx.productName}")
            appendLine("TUJUAN : ${trx.targetNumber}")
            if (trx.customerName.isNotBlank()) {
                appendLine("PEMBELI: ${trx.customerName}")
            }
            appendLine("H2H    : ${trx.providerName}")
            appendLine(line)
            appendLine("STATUS : ${trx.trxStatus}")
            if (trx.serialNumber.isNotBlank()) {
                appendLine("SN/TOKEN:")
                appendLine(trx.serialNumber)
            }
            appendLine(line)
            appendLine("TOTAL BAYAR: $formattedPrice")
            appendLine("METODE     : ${trx.paymentStatus.replace("_", " ")}")
            appendLine(line)
            appendLine(store.footerMessage)
            appendLine("=== STRUK ELEKTRONIK RESMI ===")
        }
    }

    fun shareReceipt(context: Context, receiptText: String, phoneOptional: String? = null) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, receiptText)
            putExtra(Intent.EXTRA_SUBJECT, "Bukti Transaksi Pulsa & PPOB")
        }
        val shareIntent = Intent.createChooser(sendIntent, "Bagikan Bukti Struk via")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun shareWhatsAppDebtReminder(context: Context, customerName: String, customerPhone: String, amount: Long, productName: String) {
        val message = "Halo Kak $customerName, mengingatkan tagihan pembelian $productName sebesar ${formatRupiah(amount)} di konter pulsa kami. Mohon kesediaannya untuk pelunasan ya. Terima kasih banyak!"
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        val shareIntent = Intent.createChooser(sendIntent, "Kirim Pengingat Tagihan via")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }
}
