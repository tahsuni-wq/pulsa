package com.example.network

import androidx.compose.ui.graphics.Color

data class OperatorInfo(
    val name: String,
    val brandColor: Color,
    val logoText: String
)

object OperatorDetector {
    fun detect(phoneNumber: String): OperatorInfo? {
        val cleanNumber = phoneNumber.replace("[^0-9]".toRegex(), "")
        if (cleanNumber.length < 4) return null

        val prefix4 = if (cleanNumber.length >= 4) cleanNumber.substring(0, 4) else ""

        return when {
            // Telkomsel
            prefix4 in listOf("0811", "0812", "0813", "0821", "0822", "0823", "0851", "0852", "0853") -> {
                OperatorInfo("Telkomsel", Color(0xFFD32F2F), "TSEL")
            }
            // Indosat Ooredoo IM3
            prefix4 in listOf("0814", "0815", "0816", "0855", "0856", "0857", "0858") -> {
                OperatorInfo("Indosat", Color(0xFFF59E0B), "ISAT")
            }
            // XL Axiata
            prefix4 in listOf("0817", "0818", "0819", "0859", "0877", "0878") -> {
                OperatorInfo("XL", Color(0xFF0284C7), "XL")
            }
            // AXIS
            prefix4 in listOf("0831", "0832", "0838") -> {
                OperatorInfo("AXIS", Color(0xFF7C3AED), "AXIS")
            }
            // Tri (3)
            prefix4 in listOf("0895", "0896", "0897", "0898", "0899") -> {
                OperatorInfo("Tri", Color(0xFFDB2777), "TRI")
            }
            // Smartfren
            prefix4 in listOf("0881", "0882", "0883", "0884", "0885", "0886", "0887", "0888", "0889") -> {
                OperatorInfo("Smartfren", Color(0xFFE11D48), "SMART")
            }
            // PLN (11-12 digits without 08 prefix)
            cleanNumber.length in 11..12 && !cleanNumber.startsWith("08") -> {
                OperatorInfo("PLN", Color(0xFF0D9488), "PLN")
            }
            else -> null
        }
    }
}
