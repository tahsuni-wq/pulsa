/**
 * PulsaPay H2H Server & Central Web Dashboard
 * Compatible with Node.js 18+ (uses native fetch and crypto)
 */

const express = require('express');
const cors = require('cors');
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

const app = express();
const PORT = process.env.PORT || 3000;
const DATA_DIR = path.join(__dirname, 'data');
const DB_FILE = path.join(DATA_DIR, 'database.json');

// Ensure data directory exists
if (!fs.existsSync(DATA_DIR)) {
  fs.mkdirSync(DATA_DIR, { recursive: true });
}

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(express.static(path.join(__dirname, 'public')));

// Helpers
function md5(input) {
  return crypto.createHash('md5').update(input).digest('hex');
}

// Initial Database Template
const DEFAULT_DB = {
  settings: {
    serverName: "PulsaPay Central Proxmox Gateway",
    clientApiToken: "PROXMOX_PULSAPAY_SECURE_TOKEN_2026",
    clientPin: "1234",
    defaultMarkup: 1500,
    webhookSecret: "whsec_pulsepay_proxmox_99",
    autoFailover: true
  },
  providers: [
    {
      id: "digiflazz",
      name: "Digiflazz H2H",
      type: "DIGIFLAZZ",
      apiUrl: "https://api.digiflazz.com/v1",
      apiUsername: "demo_pulsa",
      apiKey: "dev-digiflazz-key",
      secretOrPin: "1234",
      isActive: true,
      isPrimary: true,
      priorityOrder: 1,
      isSandbox: true,
      balance: 1450000,
      lastCheck: Date.now(),
      statusMessage: "Terhubung (Sandbox)"
    },
    {
      id: "vip_reseller",
      name: "VIP Reseller",
      type: "VIP_RESELLER",
      apiUrl: "https://vip-reseller.co.id/api",
      apiUsername: "vip_merchant_demo",
      apiKey: "vip_key_dev_mode",
      secretOrPin: "1234",
      isActive: true,
      isPrimary: false,
      priorityOrder: 2,
      isSandbox: true,
      balance: 850000,
      lastCheck: Date.now(),
      statusMessage: "Siap Sebagai Backup Failover"
    },
    {
      id: "tripay",
      name: "Tripay H2H",
      type: "TRIPAY",
      apiUrl: "https://tripay.id/api/v2",
      apiUsername: "tripay_user",
      apiKey: "DEV-tripay-key",
      secretOrPin: "1234",
      isActive: false,
      isPrimary: false,
      priorityOrder: 3,
      isSandbox: true,
      balance: 500000,
      lastCheck: Date.now(),
      statusMessage: "Tidak Aktif"
    }
  ],
  products: [
    { code: "TSEL5", name: "Telkomsel 5.000", operator: "TELKOMSEL", basePrice: 5350, sellPrice: 7000, active: true },
    { code: "TSEL10", name: "Telkomsel 10.000", operator: "TELKOMSEL", basePrice: 10350, sellPrice: 12000, active: true },
    { code: "TSEL20", name: "Telkomsel 20.000", operator: "TELKOMSEL", basePrice: 20100, sellPrice: 22000, active: true },
    { code: "TSEL25", name: "Telkomsel 25.000", operator: "TELKOMSEL", basePrice: 24900, sellPrice: 27000, active: true },
    { code: "TSEL50", name: "Telkomsel 50.000", operator: "TELKOMSEL", basePrice: 49400, sellPrice: 52000, active: true },
    { code: "ISAT5", name: "Indosat 5.000", operator: "INDOSAT", basePrice: 5450, sellPrice: 7000, active: true },
    { code: "ISAT10", name: "Indosat 10.000", operator: "INDOSAT", basePrice: 10450, sellPrice: 12000, active: true },
    { code: "ISAT25", name: "Indosat 25.000", operator: "INDOSAT", basePrice: 24850, sellPrice: 27000, active: true },
    { code: "XL10", name: "XL Axiata 10.000", operator: "XL", basePrice: 10250, sellPrice: 12000, active: true },
    { code: "AXIS10", name: "Axis 10.000", operator: "AXIS", basePrice: 10200, sellPrice: 12000, active: true },
    { code: "TRI10", name: "Tri Three 10.000", operator: "TRI", basePrice: 10150, sellPrice: 12000, active: true },
    { code: "PLN20", name: "Token PLN 20.000", operator: "PLN", basePrice: 20500, sellPrice: 22500, active: true },
    { code: "PLN50", name: "Token PLN 50.000", operator: "PLN", basePrice: 50500, sellPrice: 52500, active: true },
    { code: "DANA20", name: "Topup DANA 20.000", operator: "E-MONEY", basePrice: 20300, sellPrice: 22000, active: true },
    { code: "GOPAY20", name: "GOPAY 20.000", operator: "E-MONEY", basePrice: 20300, sellPrice: 22000, active: true }
  ],
  transactions: [
    {
      id: "TRX-SAMPLE-01",
      date: new Date(Date.now() - 3600000).toISOString(),
      destination: "081234567890",
      productCode: "TSEL10",
      productName: "Telkomsel 10.000",
      costPrice: 10350,
      sellPrice: 12000,
      profit: 1650,
      status: "SUCCESS",
      providerId: "digiflazz",
      providerName: "Digiflazz H2H",
      sn: "092309110001/TSEL/081234567890",
      responseMessage: "Transaksi Berhasil",
      failoverLog: "Direct route OK",
      rawRequest: "{\"customer_no\": \"081234567890\", \"buyer_sku\": \"tsel10\"}",
      rawResponse: "{\"status\": \"Sukses\", \"rc\": \"00\"}"
    }
  ]
};

// Database Read/Write Functions
function loadDb() {
  try {
    if (fs.existsSync(DB_FILE)) {
      const content = fs.readFileSync(DB_FILE, 'utf8');
      return JSON.parse(content);
    }
  } catch (e) {
    console.error("Error reading database file, using default:", e.message);
  }
  saveDb(DEFAULT_DB);
  return DEFAULT_DB;
}

function saveDb(data) {
  try {
    fs.writeFileSync(DB_FILE, JSON.stringify(data, null, 2), 'utf8');
  } catch (e) {
    console.error("Error writing database file:", e.message);
  }
}

// -------------------------------------------------------------
// AGGREGATOR EXECUTION ENGINE
// -------------------------------------------------------------

async function executeAggregator(provider, destination, productCode, refId) {
  const isSandbox = provider.isSandbox || provider.apiKey.includes("dev") || provider.apiKey.includes("demo");

  // 1. DIGIFLAZZ
  if (provider.type === "DIGIFLAZZ") {
    const sku = productCode.toLowerCase();
    const sign = md5(`${provider.apiUsername}${provider.apiKey}${refId}`);
    const reqBody = {
      username: provider.apiUsername,
      buyer_sku_code: sku,
      customer_no: destination,
      ref_id: refId,
      sign: sign,
      testing: isSandbox
    };

    if (isSandbox) {
      const sn = `${Date.now()}/DIGI/${destination}`;
      return {
        isSuccess: true,
        isPending: false,
        status: "SUCCESS",
        sn: sn,
        message: "Transaksi Berhasil (Digiflazz Sandbox)",
        rawReq: JSON.stringify(reqBody, null, 2),
        rawResp: JSON.stringify({ data: { status: "Sukses", rc: "00", sn: sn } }, null, 2)
      };
    }

    try {
      const resp = await fetch(`${provider.apiUrl.replace(/\/$/, '')}/transaction`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(reqBody)
      });
      const data = await resp.json();
      const statusStr = data?.data?.status || "Gagal";
      const isSuccess = statusStr.toLowerCase() === "sukses" || data?.data?.rc === "00";
      const isPending = statusStr.toLowerCase() === "pending";
      return {
        isSuccess,
        isPending,
        status: isSuccess ? "SUCCESS" : isPending ? "PENDING" : "FAILED",
        sn: data?.data?.sn || "",
        message: data?.data?.message || `Status: ${statusStr}`,
        rawReq: JSON.stringify(reqBody, null, 2),
        rawResp: JSON.stringify(data, null, 2)
      };
    } catch (err) {
      return {
        isSuccess: false,
        isPending: false,
        status: "FAILED",
        sn: "",
        message: `Koneksi Digiflazz Error: ${err.message}`,
        rawReq: JSON.stringify(reqBody, null, 2),
        rawResp: JSON.stringify({ error: err.message }, null, 2)
      };
    }
  }

  // 2. VIP RESELLER
  if (provider.type === "VIP_RESELLER") {
    const sign = md5(`${provider.apiUsername}${provider.apiKey}`);
    const reqBody = {
      key: provider.apiKey,
      sign: sign,
      type: "order",
      service: productCode.toLowerCase(),
      data_no: destination,
      ref_id: refId
    };

    if (isSandbox) {
      const sn = `${Date.now()}/VIP/${destination}`;
      return {
        isSuccess: true,
        isPending: false,
        status: "SUCCESS",
        sn: sn,
        message: "Pesanan Berhasil (VIP Reseller Sandbox)",
        rawReq: JSON.stringify(reqBody, null, 2),
        rawResp: JSON.stringify({ result: true, data: { status: "success", note: sn } }, null, 2)
      };
    }

    try {
      const resp = await fetch(`${provider.apiUrl.replace(/\/$/, '')}/prepaid`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(reqBody)
      });
      const data = await resp.json();
      const isSuccess = data?.result === true;
      return {
        isSuccess,
        isPending: false,
        status: isSuccess ? "SUCCESS" : "FAILED",
        sn: data?.data?.note || "",
        message: data?.message || "VIP Reseller Respon",
        rawReq: JSON.stringify(reqBody, null, 2),
        rawResp: JSON.stringify(data, null, 2)
      };
    } catch (err) {
      return {
        isSuccess: false,
        isPending: false,
        status: "FAILED",
        sn: "",
        message: `Koneksi VIP Reseller Error: ${err.message}`,
        rawReq: JSON.stringify(reqBody, null, 2),
        rawResp: JSON.stringify({ error: err.message }, null, 2)
      };
    }
  }

  // 3. TRIPAY
  if (provider.type === "TRIPAY") {
    const reqBody = {
      api_key: provider.apiKey,
      pin: provider.secretOrPin,
      inquiry: "PLN",
      code: productCode,
      phone: destination,
      api_trxid: refId
    };

    if (isSandbox) {
      const sn = `TRIPAY-${Date.now()}-OK`;
      return {
        isSuccess: true,
        isPending: false,
        status: "SUCCESS",
        sn: sn,
        message: "Transaksi Berhasil (Tripay Sandbox)",
        rawReq: JSON.stringify(reqBody, null, 2),
        rawResp: JSON.stringify({ success: true, data: { sn: sn, status: 1 } }, null, 2)
      };
    }

    try {
      const resp = await fetch(`${provider.apiUrl.replace(/\/$/, '')}/transaksi`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(reqBody)
      });
      const data = await resp.json();
      const isSuccess = data?.success === true;
      return {
        isSuccess,
        isPending: false,
        status: isSuccess ? "SUCCESS" : "FAILED",
        sn: data?.data?.sn || "",
        message: data?.message || "Tripay Respon",
        rawReq: JSON.stringify(reqBody, null, 2),
        rawResp: JSON.stringify(data, null, 2)
      };
    } catch (err) {
      return {
        isSuccess: false,
        isPending: false,
        status: "FAILED",
        sn: "",
        message: `Koneksi Tripay Error: ${err.message}`,
        rawReq: JSON.stringify(reqBody, null, 2),
        rawResp: JSON.stringify({ error: err.message }, null, 2)
      };
    }
  }

  // Default Custom fallback
  const sn = `CUSTOM-${Date.now()}`;
  return {
    isSuccess: true,
    isPending: false,
    status: "SUCCESS",
    sn: sn,
    message: "Diproses oleh Internal Gateway",
    rawReq: "{}",
    rawResp: "{}"
  };
}

// -------------------------------------------------------------
// REST API FOR ANDROID APP (GATEWAY)
// -------------------------------------------------------------

// POST /api/v1/transaksi (Directly consumed by Android PulsaPay)
app.post('/api/v1/transaksi', async (req, res) => {
  const db = loadDb();
  const { destination, product_code, partner_ref, token, pin } = req.body;

  // 1. Authenticate Request
  if (token !== db.settings.clientApiToken) {
    return res.status(401).json({
      status: "FAILED",
      rc: "401",
      message: "API Token tidak valid! Periksa pengaturan token di aplikasi Android."
    });
  }

  if (pin && db.settings.clientPin && pin !== db.settings.clientPin) {
    return res.status(403).json({
      status: "FAILED",
      rc: "403",
      message: "PIN Transaksi salah."
    });
  }

  if (!destination || !product_code) {
    return res.status(400).json({
      status: "FAILED",
      rc: "400",
      message: "Nomor tujuan (destination) dan kode produk (product_code) harus diisi."
    });
  }

  const refId = partner_ref || `TRX-${Date.now()}`;

  // Find product
  const product = db.products.find(p => p.code.toUpperCase() === product_code.toUpperCase()) || {
    code: product_code,
    name: `Produk ${product_code}`,
    basePrice: 10000,
    sellPrice: 12000
  };

  // Get Active Providers sorted by Priority
  const activeProviders = db.providers
    .filter(p => p.isActive)
    .sort((a, b) => (a.priorityOrder || 1) - (b.priorityOrder || 1));

  if (activeProviders.length === 0) {
    return res.status(503).json({
      status: "FAILED",
      rc: "503",
      message: "Tidak ada jalur agregator H2H yang aktif di server Proxmox."
    });
  }

  let finalResult = null;
  let providerUsed = null;
  let failoverLogs = [];

  for (const prov of activeProviders) {
    providerUsed = prov;
    const result = await executeAggregator(prov, destination, product.code, refId);
    if (result.isSuccess || result.isPending) {
      finalResult = result;
      break;
    } else {
      failoverLogs.push(`[${prov.name} Gagal: ${result.message}]`);
      if (!db.settings.autoFailover) {
        finalResult = result;
        break;
      }
    }
  }

  if (!finalResult) {
    finalResult = {
      isSuccess: false,
      isPending: false,
      status: "FAILED",
      sn: "",
      message: "Semua jalur agregator gagal memproses.",
      rawReq: "{}",
      rawResp: "{}"
    };
  }

  // Record Transaction to Database
  const profit = Math.max(0, product.sellPrice - product.basePrice);
  const newTx = {
    id: refId,
    date: new Date().toISOString(),
    destination: destination,
    productCode: product.code,
    productName: product.name,
    costPrice: product.basePrice,
    sellPrice: product.sellPrice,
    profit: profit,
    status: finalResult.status,
    providerId: providerUsed?.id || "none",
    providerName: providerUsed?.name || "Server Internal",
    sn: finalResult.sn || "",
    responseMessage: finalResult.message,
    failoverLog: failoverLogs.join(" -> "),
    rawRequest: finalResult.rawReq,
    rawResponse: finalResult.rawResp
  };

  db.transactions.unshift(newTx);
  // Keep max 1000 transactions
  if (db.transactions.length > 1000) db.transactions = db.transactions.slice(0, 1000);
  saveDb(db);

  return res.json({
    status: finalResult.status,
    rc: finalResult.isSuccess ? "00" : finalResult.isPending ? "03" : "99",
    sn: finalResult.sn,
    ref: refId,
    message: finalResult.message,
    provider: providerUsed?.name,
    failover: failoverLogs.length > 0 ? failoverLogs.join(" -> ") : "Jalur Utama"
  });
});

// POST /api/v1/cek-saldo (Consumed by Android PulsaPay for balance check)
app.post('/api/v1/cek-saldo', (req, res) => {
  const db = loadDb();
  const { token } = req.body;

  if (token && token !== db.settings.clientApiToken) {
    return res.status(401).json({ status: "FAILED", message: "Invalid token" });
  }

  const totalBalance = db.providers.filter(p => p.isActive).reduce((sum, p) => sum + (p.balance || 0), 0);

  return res.json({
    status: "SUCCESS",
    balance: totalBalance,
    providersCount: db.providers.length,
    activeCount: db.providers.filter(p => p.isActive).length,
    message: "Koneksi Proxmox Central OK"
  });
});

// -------------------------------------------------------------
// WEBHOOK RECEIVERS FROM PROVIDERS
// -------------------------------------------------------------

app.post('/api/webhook/digiflazz', (req, res) => {
  console.log("Inbound Digiflazz Webhook:", JSON.stringify(req.body));
  const { data } = req.body;
  if (data && data.ref_id) {
    const db = loadDb();
    const tx = db.transactions.find(t => t.id === data.ref_id);
    if (tx) {
      tx.status = data.status === "Sukses" ? "SUCCESS" : data.status === "Pending" ? "PENDING" : "FAILED";
      if (data.sn) tx.sn = data.sn;
      tx.responseMessage = `Webhook: ${data.message || data.status}`;
      saveDb(db);
    }
  }
  return res.json({ status: "received" });
});

app.post('/api/webhook/tripay', (req, res) => {
  console.log("Inbound Tripay Webhook:", JSON.stringify(req.body));
  return res.json({ success: true });
});

app.post('/api/webhook/vip', (req, res) => {
  console.log("Inbound VIP Webhook:", JSON.stringify(req.body));
  return res.json({ result: true });
});

// -------------------------------------------------------------
// WEB DASHBOARD MANAGEMENT APIS
// -------------------------------------------------------------

// GET /api/stats
app.get('/api/stats', (req, res) => {
  const db = loadDb();
  const now = new Date();
  const todayStr = now.toISOString().slice(0, 10);

  const todayTxs = db.transactions.filter(t => t.date && t.date.startsWith(todayStr));
  const successTxs = db.transactions.filter(t => t.status === "SUCCESS");
  const failedTxs = db.transactions.filter(t => t.status === "FAILED");
  const pendingTxs = db.transactions.filter(t => t.status === "PENDING");

  const todayRevenue = todayTxs.filter(t => t.status === "SUCCESS").reduce((acc, t) => acc + (t.sellPrice || 0), 0);
  const totalProfit = successTxs.reduce((acc, t) => acc + (t.profit || 0), 0);
  const totalAggregatorBalance = db.providers.reduce((acc, p) => acc + (p.balance || 0), 0);

  return res.json({
    totalTransactions: db.transactions.length,
    todayTransactions: todayTxs.length,
    todayRevenue,
    totalProfit,
    successCount: successTxs.length,
    failedCount: failedTxs.length,
    pendingCount: pendingTxs.length,
    totalAggregatorBalance,
    providers: db.providers
  });
});

// GET /api/transactions
app.get('/api/transactions', (req, res) => {
  const db = loadDb();
  const { status, q, limit } = req.query;

  let list = db.transactions;
  if (status && status !== 'ALL') {
    list = list.filter(t => t.status === status);
  }
  if (q) {
    const search = q.toLowerCase();
    list = list.filter(t =>
      (t.destination && t.destination.includes(search)) ||
      (t.id && t.id.toLowerCase().includes(search)) ||
      (t.sn && t.sn.toLowerCase().includes(search)) ||
      (t.productCode && t.productCode.toLowerCase().includes(search))
    );
  }

  const max = parseInt(limit, 10) || 50;
  return res.json(list.slice(0, max));
});

// POST /api/transactions/retry
app.post('/api/transactions/retry', async (req, res) => {
  const { id } = req.body;
  const db = loadDb();
  const tx = db.transactions.find(t => t.id === id);
  if (!tx) return res.status(404).json({ error: "Transaksi tidak ditemukan" });

  const activeProviders = db.providers.filter(p => p.isActive);
  if (activeProviders.length === 0) {
    return res.status(500).json({ error: "Tidak ada provider aktif" });
  }

  // Try next provider or primary
  const targetProv = activeProviders.find(p => p.id !== tx.providerId) || activeProviders[0];
  const newRef = `${tx.id}-R${Math.floor(Math.random() * 100)}`;
  const result = await executeAggregator(targetProv, tx.destination, tx.productCode, newRef);

  tx.status = result.status;
  if (result.sn) tx.sn = result.sn;
  tx.providerId = targetProv.id;
  tx.providerName = targetProv.name;
  tx.responseMessage = `Manual Retry: ${result.message}`;
  tx.failoverLog = `Retried via ${targetProv.name}`;
  saveDb(db);

  return res.json({ success: true, result, tx });
});

// GET /api/providers
app.get('/api/providers', (req, res) => {
  const db = loadDb();
  return res.json(db.providers);
});

// POST /api/providers (Add or Update)
app.post('/api/providers', (req, res) => {
  const db = loadDb();
  const pData = req.body;

  const existingIdx = db.providers.findIndex(p => p.id === pData.id);
  if (existingIdx >= 0) {
    db.providers[existingIdx] = { ...db.providers[existingIdx], ...pData };
  } else {
    pData.id = pData.id || `prov_${Date.now()}`;
    db.providers.push(pData);
  }

  // Ensure only one primary
  if (pData.isPrimary) {
    db.providers.forEach(p => {
      if (p.id !== pData.id) p.isPrimary = false;
    });
  }

  saveDb(db);
  return res.json({ success: true, providers: db.providers });
});

// POST /api/providers/:id/check-balance
app.post('/api/providers/:id/check-balance', async (req, res) => {
  const db = loadDb();
  const prov = db.providers.find(p => p.id === req.params.id);
  if (!prov) return res.status(404).json({ error: "Provider tidak ditemukan" });

  const isSandbox = prov.isSandbox || prov.apiKey.includes("dev") || prov.apiKey.includes("demo");

  if (isSandbox) {
    prov.balance = Math.max(100000, prov.balance + Math.floor(Math.random() * 50000) - 10000);
    prov.lastCheck = Date.now();
    prov.statusMessage = "Saldo Terhubung (Sandbox Sync OK)";
    saveDb(db);
    return res.json({ success: true, balance: prov.balance, message: prov.statusMessage });
  }

  // Real live balance checking
  try {
    if (prov.type === "DIGIFLAZZ") {
      const sign = md5(`${prov.apiUsername}${prov.apiKey}depo`);
      const resp = await fetch(`${prov.apiUrl.replace(/\/$/, '')}/cek-saldo`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ cmd: "deposit", username: prov.apiUsername, sign })
      });
      const data = await resp.json();
      if (data?.data?.deposit !== undefined) {
        prov.balance = data.data.deposit;
      }
    } else if (prov.type === "VIP_RESELLER") {
      const sign = md5(`${prov.apiUsername}${prov.apiKey}`);
      const resp = await fetch(`${prov.apiUrl.replace(/\/$/, '')}/profile`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ key: prov.apiKey, sign })
      });
      const data = await resp.json();
      if (data?.data?.balance !== undefined) {
        prov.balance = data.data.balance;
      }
    }
    prov.lastCheck = Date.now();
    prov.statusMessage = "Tersinkronisasi Real-time";
    saveDb(db);
    return res.json({ success: true, balance: prov.balance, message: "Saldo Berhasil Diperbarui" });
  } catch (e) {
    prov.statusMessage = `Error Cek Saldo: ${e.message}`;
    saveDb(db);
    return res.json({ success: false, balance: prov.balance, message: e.message });
  }
});

// GET /api/products
app.get('/api/products', (req, res) => {
  const db = loadDb();
  return res.json(db.products);
});

// POST /api/products
app.post('/api/products', (req, res) => {
  const db = loadDb();
  const item = req.body;
  const idx = db.products.findIndex(p => p.code === item.code);
  if (idx >= 0) {
    db.products[idx] = { ...db.products[idx], ...item };
  } else {
    db.products.push(item);
  }
  saveDb(db);
  return res.json({ success: true, products: db.products });
});

// GET /api/settings
app.get('/api/settings', (req, res) => {
  const db = loadDb();
  return res.json(db.settings);
});

// POST /api/settings
app.post('/api/settings', (req, res) => {
  const db = loadDb();
  db.settings = { ...db.settings, ...req.body };
  saveDb(db);
  return res.json({ success: true, settings: db.settings });
});

// Fallback to Web Dashboard
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

// Start Server
app.listen(PORT, '0.0.0.0', () => {
  console.log(`====================================================`);
  console.log(`PulsaPay Proxmox Server & Dashboard running on port ${PORT}`);
  console.log(`Web Dashboard: http://localhost:${PORT}`);
  console.log(`Android Gateway Endpoint: http://localhost:${PORT}/api/v1/transaksi`);
  console.log(`====================================================`);
});
