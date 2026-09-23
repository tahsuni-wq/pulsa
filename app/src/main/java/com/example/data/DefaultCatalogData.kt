package com.example.data

import com.example.model.H2HProvider
import com.example.model.ProductCategory
import com.example.model.ProductItem
import com.example.model.ProviderType

object DefaultCatalogData {
    val initialProviders = listOf(
        H2HProvider(
            id = "custom_rest",
            name = "Server Kirana Solusi (Proxmox)",
            type = ProviderType.CUSTOM_REST,
            apiUrl = "https://pulsa.kiranasolusi.web.id/api/v1",
            apiUsername = "kirana_admin",
            apiKey = "PROXMOX_PULSAPAY_SECURE_TOKEN_2026",
            secretOrPin = "1234",
            isActive = true,
            isPrimary = true,
            priorityOrder = 1,
            isSandbox = false,
            balance = 2500000L,
            lastBalanceCheck = System.currentTimeMillis(),
            statusMessage = "Terhubung ke kiranasolusi.web.id"
        ),
        H2HProvider(
            id = "digiflazz",
            name = "Digiflazz H2H (Backup Failover)",
            type = ProviderType.DIGIFLAZZ,
            apiUrl = "https://api.digiflazz.com/v1",
            apiUsername = "demo_pulsapay",
            apiKey = "dev-b3848b50-329b-11ea",
            secretOrPin = "dflazz_secret_99",
            isActive = true,
            isPrimary = false,
            priorityOrder = 2,
            isSandbox = true,
            balance = 1450000L,
            lastBalanceCheck = System.currentTimeMillis() - 120000L,
            statusMessage = "Rute Cadangan Tier-2"
        ),
        H2HProvider(
            id = "vipreseller",
            name = "VIP Reseller API",
            type = ProviderType.VIP_RESELLER,
            apiUrl = "https://vip-reseller.co.id/api",
            apiUsername = "vip_merchant_88",
            apiKey = "vip_key_9981240182",
            secretOrPin = "123456",
            isActive = true,
            isPrimary = false,
            priorityOrder = 2,
            isSandbox = true,
            balance = 820000L,
            lastBalanceCheck = System.currentTimeMillis() - 360000L,
            statusMessage = "Cadangan Utama (Failover Ready)"
        ),
        H2HProvider(
            id = "tripay",
            name = "Tripay H2H PPOB",
            type = ProviderType.TRIPAY,
            apiUrl = "https://tripay.id/api/v2",
            apiUsername = "TRIPAY-ACC-291",
            apiKey = "DEV-x9281nxa87219",
            secretOrPin = "9821",
            isActive = true,
            isPrimary = false,
            priorityOrder = 3,
            isSandbox = true,
            balance = 560000L,
            lastBalanceCheck = System.currentTimeMillis() - 720000L,
            statusMessage = "Cadangan Tier-3"
        )
    )

    val initialProducts = listOf(
        // Telkomsel Pulsa
        ProductItem(
            code = "TSEL5",
            name = "Telkomsel 5.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "Telkomsel",
            nominal = 5000L,
            basePrice = 5350L,
            sellingPrice = 7000L,
            description = "Pulsa Reguler Telkomsel 5rb menambah masa aktif 7 hari",
            digiflazzSku = "htsel5000",
            vipResellerSku = "t5",
            tripaySku = "TSEL5"
        ),
        ProductItem(
            code = "TSEL10",
            name = "Telkomsel 10.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "Telkomsel",
            nominal = 10000L,
            basePrice = 10250L,
            sellingPrice = 12000L,
            description = "Pulsa Reguler Telkomsel 10rb menambah masa aktif 15 hari",
            digiflazzSku = "htsel10000",
            vipResellerSku = "t10",
            tripaySku = "TSEL10"
        ),
        ProductItem(
            code = "TSEL20",
            name = "Telkomsel 20.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "Telkomsel",
            nominal = 20000L,
            basePrice = 20100L,
            sellingPrice = 22000L,
            description = "Pulsa Reguler Telkomsel 20rb masa aktif 30 hari",
            digiflazzSku = "htsel20000",
            vipResellerSku = "t20",
            tripaySku = "TSEL20"
        ),
        ProductItem(
            code = "TSEL25",
            name = "Telkomsel 25.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "Telkomsel",
            nominal = 25000L,
            basePrice = 24950L,
            sellingPrice = 27000L,
            description = "Pulsa Reguler Telkomsel 25rb masa aktif 30 hari",
            digiflazzSku = "htsel25000",
            vipResellerSku = "t25",
            tripaySku = "TSEL25"
        ),
        ProductItem(
            code = "TSEL50",
            name = "Telkomsel 50.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "Telkomsel",
            nominal = 50000L,
            basePrice = 49700L,
            sellingPrice = 52000L,
            description = "Pulsa Reguler Telkomsel 50rb masa aktif 45 hari",
            digiflazzSku = "htsel50000",
            vipResellerSku = "t50",
            tripaySku = "TSEL50"
        ),
        ProductItem(
            code = "TSEL100",
            name = "Telkomsel 100.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "Telkomsel",
            nominal = 100000L,
            basePrice = 98200L,
            sellingPrice = 102000L,
            description = "Pulsa Reguler Telkomsel 100rb masa aktif 60 hari",
            digiflazzSku = "htsel100000",
            vipResellerSku = "t100",
            tripaySku = "TSEL100"
        ),

        // Indosat Pulsa
        ProductItem(
            code = "ISAT5",
            name = "Indosat 5.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "Indosat",
            nominal = 5000L,
            basePrice = 5400L,
            sellingPrice = 7000L,
            description = "Pulsa Reguler Indosat Ooredoo IM3 5rb",
            digiflazzSku = "isat5",
            vipResellerSku = "i5",
            tripaySku = "ISAT5"
        ),
        ProductItem(
            code = "ISAT10",
            name = "Indosat 10.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "Indosat",
            nominal = 10000L,
            basePrice = 10300L,
            sellingPrice = 12000L,
            description = "Pulsa Reguler Indosat Ooredoo IM3 10rb",
            digiflazzSku = "isat10",
            vipResellerSku = "i10",
            tripaySku = "ISAT10"
        ),
        ProductItem(
            code = "ISAT25",
            name = "Indosat 25.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "Indosat",
            nominal = 25000L,
            basePrice = 24900L,
            sellingPrice = 27000L,
            description = "Pulsa Reguler Indosat IM3 25rb",
            digiflazzSku = "isat25",
            vipResellerSku = "i25",
            tripaySku = "ISAT25"
        ),
        ProductItem(
            code = "ISAT50",
            name = "Indosat 50.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "Indosat",
            nominal = 50000L,
            basePrice = 49500L,
            sellingPrice = 52000L,
            description = "Pulsa Reguler Indosat IM3 50rb",
            digiflazzSku = "isat50",
            vipResellerSku = "i50",
            tripaySku = "ISAT50"
        ),

        // XL Axiata Pulsa
        ProductItem(
            code = "XL5",
            name = "XL 5.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "XL",
            nominal = 5000L,
            basePrice = 5450L,
            sellingPrice = 7000L,
            description = "Pulsa Reguler XL Axiata 5rb",
            digiflazzSku = "xl5",
            vipResellerSku = "x5",
            tripaySku = "XL5"
        ),
        ProductItem(
            code = "XL10",
            name = "XL 10.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "XL",
            nominal = 10000L,
            basePrice = 10400L,
            sellingPrice = 12000L,
            description = "Pulsa Reguler XL Axiata 10rb",
            digiflazzSku = "xl10",
            vipResellerSku = "x10",
            tripaySku = "XL10"
        ),
        ProductItem(
            code = "XL25",
            name = "XL 25.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "XL",
            nominal = 25000L,
            basePrice = 24850L,
            sellingPrice = 27000L,
            description = "Pulsa Reguler XL Axiata 25rb",
            digiflazzSku = "xl25",
            vipResellerSku = "x25",
            tripaySku = "XL25"
        ),
        ProductItem(
            code = "XL50",
            name = "XL 50.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "XL",
            nominal = 50000L,
            basePrice = 49600L,
            sellingPrice = 52000L,
            description = "Pulsa Reguler XL Axiata 50rb",
            digiflazzSku = "xl50",
            vipResellerSku = "x50",
            tripaySku = "XL50"
        ),

        // AXIS Pulsa
        ProductItem(
            code = "AXIS5",
            name = "AXIS 5.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "AXIS",
            nominal = 5000L,
            basePrice = 5400L,
            sellingPrice = 7000L,
            description = "Pulsa Reguler AXIS 5rb",
            digiflazzSku = "ax5",
            vipResellerSku = "ax5",
            tripaySku = "AXIS5"
        ),
        ProductItem(
            code = "AXIS10",
            name = "AXIS 10.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "AXIS",
            nominal = 10000L,
            basePrice = 10350L,
            sellingPrice = 12000L,
            description = "Pulsa Reguler AXIS 10rb",
            digiflazzSku = "ax10",
            vipResellerSku = "ax10",
            tripaySku = "AXIS10"
        ),

        // Tri (3) Pulsa
        ProductItem(
            code = "TRI5",
            name = "Tri 5.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "Tri",
            nominal = 5000L,
            basePrice = 5150L,
            sellingPrice = 7000L,
            description = "Pulsa Reguler Three / Tri 5rb",
            digiflazzSku = "tri5",
            vipResellerSku = "tr5",
            tripaySku = "TRI5"
        ),
        ProductItem(
            code = "TRI10",
            name = "Tri 10.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "Tri",
            nominal = 10000L,
            basePrice = 10100L,
            sellingPrice = 12000L,
            description = "Pulsa Reguler Three / Tri 10rb",
            digiflazzSku = "tri10",
            vipResellerSku = "tr10",
            tripaySku = "TRI10"
        ),

        // Smartfren Pulsa
        ProductItem(
            code = "SMART5",
            name = "Smartfren 5.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "Smartfren",
            nominal = 5000L,
            basePrice = 5100L,
            sellingPrice = 7000L,
            description = "Pulsa Reguler Smartfren 5rb",
            digiflazzSku = "sm5",
            vipResellerSku = "sf5",
            tripaySku = "SF5"
        ),
        ProductItem(
            code = "SMART10",
            name = "Smartfren 10.000",
            category = ProductCategory.PULSA_REGULER,
            operator = "Smartfren",
            nominal = 10000L,
            basePrice = 10050L,
            sellingPrice = 12000L,
            description = "Pulsa Reguler Smartfren 10rb",
            digiflazzSku = "sm10",
            vipResellerSku = "sf10",
            tripaySku = "SF10"
        ),

        // Paket Data
        ProductItem(
            code = "DATA_TSEL_OMG5",
            name = "Telkomsel OMG! 5GB (30 Hari)",
            category = ProductCategory.PAKET_DATA,
            operator = "Telkomsel",
            nominal = 35000L,
            basePrice = 33500L,
            sellingPrice = 38000L,
            description = "Kuota 5GB (3GB Utama + 2GB OMG!) masa aktif 30 hari",
            digiflazzSku = "td5gb",
            vipResellerSku = "tdomg5",
            tripaySku = "TSEL_DATA5"
        ),
        ProductItem(
            code = "DATA_ISAT_FREE10",
            name = "Indosat Freedom 10GB (30 Hari)",
            category = ProductCategory.PAKET_DATA,
            operator = "Indosat",
            nominal = 42000L,
            basePrice = 39800L,
            sellingPrice = 45000L,
            description = "10GB Full kuota utama 24 Jam 30 hari",
            digiflazzSku = "id10gb",
            vipResellerSku = "idfree10",
            tripaySku = "ISAT_DATA10"
        ),
        ProductItem(
            code = "DATA_XL_XTRA20",
            name = "XL Xtra Combo Flex 20GB",
            category = ProductCategory.PAKET_DATA,
            operator = "XL",
            nominal = 55000L,
            basePrice = 51200L,
            sellingPrice = 58000L,
            description = "20GB Kuota Utama + Bonus Vidio / YouTube 30 hari",
            digiflazzSku = "xd20gb",
            vipResellerSku = "xdxtra20",
            tripaySku = "XL_DATA20"
        ),

        // Token PLN Prabayar
        ProductItem(
            code = "PLN20",
            name = "Token Listrik PLN 20.000",
            category = ProductCategory.TOKEN_PLN,
            operator = "PLN",
            nominal = 20000L,
            basePrice = 20250L,
            sellingPrice = 22500L,
            description = "Token Prabayar PLN Rp 20.000 dapat 20 digit kode stroom",
            digiflazzSku = "pln20",
            vipResellerSku = "pln20",
            tripaySku = "PLN20"
        ),
        ProductItem(
            code = "PLN50",
            name = "Token Listrik PLN 50.000",
            category = ProductCategory.TOKEN_PLN,
            operator = "PLN",
            nominal = 50000L,
            basePrice = 50250L,
            sellingPrice = 52500L,
            description = "Token Prabayar PLN Rp 50.000 dapat 20 digit kode stroom",
            digiflazzSku = "pln50",
            vipResellerSku = "pln50",
            tripaySku = "PLN50"
        ),
        ProductItem(
            code = "PLN100",
            name = "Token Listrik PLN 100.000",
            category = ProductCategory.TOKEN_PLN,
            operator = "PLN",
            nominal = 100000L,
            basePrice = 100250L,
            sellingPrice = 102500L,
            description = "Token Prabayar PLN Rp 100.000 dapat 20 digit kode stroom",
            digiflazzSku = "pln100",
            vipResellerSku = "pln100",
            tripaySku = "PLN100"
        ),

        // E-Wallet
        ProductItem(
            code = "DANA20",
            name = "Topup DANA 20.000",
            category = ProductCategory.EWALLET,
            operator = "DANA",
            nominal = 20000L,
            basePrice = 20500L,
            sellingPrice = 22000L,
            description = "Saldo DANA langsung masuk ke akun pelanggan",
            digiflazzSku = "dana20",
            vipResellerSku = "dana20",
            tripaySku = "DANA20"
        ),
        ProductItem(
            code = "DANA50",
            name = "Topup DANA 50.000",
            category = ProductCategory.EWALLET,
            operator = "DANA",
            nominal = 50000L,
            basePrice = 50500L,
            sellingPrice = 52000L,
            description = "Saldo DANA langsung masuk ke akun pelanggan",
            digiflazzSku = "dana50",
            vipResellerSku = "dana50",
            tripaySku = "DANA50"
        ),
        ProductItem(
            code = "GOPAY25",
            name = "Topup GoPay 25.000",
            category = ProductCategory.EWALLET,
            operator = "GoPay",
            nominal = 25000L,
            basePrice = 25500L,
            sellingPrice = 27000L,
            description = "Saldo GoPay Pengguna",
            digiflazzSku = "gopay25",
            vipResellerSku = "gopay25",
            tripaySku = "GOPAY25"
        ),
        ProductItem(
            code = "SHOPEE20",
            name = "Topup ShopeePay 20.000",
            category = ProductCategory.EWALLET,
            operator = "ShopeePay",
            nominal = 20000L,
            basePrice = 20500L,
            sellingPrice = 22000L,
            description = "Saldo ShopeePay Pengguna",
            digiflazzSku = "shopee20",
            vipResellerSku = "shopee20",
            tripaySku = "SHOPEE20"
        )
    )
}
