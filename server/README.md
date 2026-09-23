# PulsaPay H2H Server & Web Portal (High-Concurrency Pulsa Gateway)

Server Pulsa & PPOB berkinerja tinggi yang dirancang untuk melayani **ribuan customer secara simultan** melalui web portal, aplikasi Android, dan downstream partner API. Mendukung koneksi langsung ke **Provider Direct Telco** (Telkomsel DigiPOS, Indosat MOBO, XL SiDOMPUL, PLN Host-to-Host) serta **Multi-Agregator H2H** (Digiflazz, VIP Reseller, Tripay, Tokovoucher) dengan failover otomatis secepat kilat.

---

## 🌟 Fitur Utama Server Pulsa Web

### 1. Web Portal Pelanggan (Self-Service Web Kiosk)
- **Akses Langsung via Browser:** Pelanggan atau kasir toko dapat membeli Pulsa, Paket Data, Token PLN, dan Topup E-Money langsung lewat website.
- **Deteksi Otomatis Operator:** Cukup ketik nomor telepon, sistem langsung mendeteksi Telkomsel, Indosat, XL, Axis, Tri, Smartfren, atau PLN beserta logonya.
- **Simulasi QRIS Real-time:** Tampilan QRIS instan dengan QR dinamis, timer hitung mundur, dan verifikasi otomatis.
- **Cetak Struk Transaksi Digital:** Download atau cetak struk thermal resmi (dengan nomor SN, waktu, dan harga jual).

### 2. Arsitektur High-Concurrency (Melayani Ribuan Customer)
- **In-Memory Asynchronous Worker Queue:** 50 worker paralel memproses ratusan hingga ribuan order per detik tanpa macet.
- **Idempotency Guard & Anti Double-Trx:** Mencegah pemotongan saldo ganda jika customer menekan tombol berkali-kali dalam rentang 60 detik.
- **Live TPS Counter:** Memantau throughput transaksi per detik (TPS), latensi eksekusi dalam milidetik, dan antrean queue.
- **Stress-Test Simulator:** Dashboard dilengkapi simulator beban 100, 300, hingga 600 transaksi simultan untuk menguji performa server secara langsung.

### 3. Koneksi Provider Langsung (Direct Telco) & Agregator
- **Telkomsel Direct DigiPOS:**
  - Terhubung langsung ke API Host-to-Host / STK DigiPOS Telkomsel dengan harga modal chip resmi (tanpa biaya perantara).
  - Waktu eksekusi rata-rata 35-50ms.
- **Indosat IM3 MOBO Direct:**
  - Koneksi langsung ke Multi Operator Business Order (MOBO) Indosat Ooredoo Hutchison.
- **XL SiDOMPUL Enterprise Direct:**
  - Jalur direct dealer XL dan Axis dengan alokasi kuota dan pulsa resmi.
- **PLN Direct Host-to-Host (B2B):**
  - Pembelian token listrik PLN resmi 20 digit instan beserta info kWh dan nama pelanggan.
- **Multi-Agregator Cadangan:**
  - **Digiflazz**, **VIP Reseller**, **Tripay**, **Tokovoucher**.
- **Smart Prefix Routing Engine:**
  - Nomor Telkomsel (`0811`, `0812`, `0821`, `0852`, dll) diarahkan ke Telkomsel Direct DigiPOS terlebih dahulu. Jika saldo habis atau terjadi gangguan, sistem secara otomatis mengalihkan rute ke Digiflazz lalu VIP Reseller.

### 4. Notifikasi Real-time ke Telegram Bot API
- **Alert Otomatis Sukses / Gagal:** Setiap transaksi yang selesai diproses akan langsung dikirimkan rinciannya ke akun Telegram pribadi admin atau Channel/Grup konter.
- **Rincian Notifikasi Lengkap:** Menampilkan Nomor Tujuan, Operator, Nama Produk & SKU, Harga Jual, Margin Laba, Jalur Provider/Agregator yang mengeksekusi, Nomor Seri (SN), Nama Customer, Waktu Transaksi, dan ID Ref.
- **Asynchronous & Non-blocking:** Pengiriman notifikasi berjalan di background tanpa membebani antrean transaksi utama.
- **Tombol Test Koneksi:** Uji coba pengiriman pesan bot langsung dari dashboard web sebelum diaktifkan secara live.

### 5. Manajemen Ribuan Customer & Agen Reseller
- **Manajemen Tier Akun:**
  - `RETAIL` (Pelanggan umum web)
  - `RESELLER` (Agen kios pulsa standar)
  - `GOLD_PARTNER` (Mitra agen besar)
  - `H2H_API` (Partner downstream dengan token API khusus)
- **Sistem Saldo & Mutasi Deposit:** Tambah dan kelola deposit saldo pelanggan langsung dari dashboard.

---

## 🚀 Panduan Instalasi di Proxmox (LXC / VM) atau Ubuntu/Debian VPS

### Langkah Cepat (1 Perintah)
Masuk ke terminal server Proxmox / VPS Anda dan jalankan:
```bash
chmod +x deploy-proxmox.sh
./deploy-proxmox.sh
```

### Langkah Manual
```bash
# 1. Masuk ke folder server
cd server

# 2. Install dependencies
npm install

# 3. Jalankan server di port 3000
node server.js
```

---

## 🌐 Menghubungkan Domain & SSL Nginx

Buat file konfigurasi Nginx di `/etc/nginx/sites-available/pulsapay`:

```nginx
server {
    listen 80;
    server_name api.serverpulsa-anda.com;

    location / {
        proxy_pass http://127.0.0.1:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Aktifkan konfigurasi dan pasang SSL gratis Let's Encrypt:
```bash
ln -s /etc/nginx/sites-available/pulsapay /etc/nginx/sites-enabled/
nginx -t
systemctl reload nginx
certbot --nginx -d api.serverpulsa-anda.com
```

---

## 📡 Dokumentasi Endpoint REST API

### 1. Transaksi Pulsa & PPOB
- **Method:** `POST`
- **Path:** `/api/v1/transaksi`
- **Headers:** `Content-Type: application/json`
- **Request Body:**
```json
{
  "destination": "081234567890",
  "product_code": "TSEL10",
  "partner_ref": "REF-PARTNER-001",
  "token": "PROXMOX_PULSAPAY_SECURE_TOKEN_2026",
  "pin": "1234"
}
```

- **Response:**
```json
{
  "status": "SUCCESS",
  "rc": "00",
  "sn": "260923113440/TSEL/7890/6740",
  "ref": "REF-PARTNER-001",
  "message": "Sukses via Telkomsel DigiPOS Direct (Chip Outlet Terhubung)",
  "provider": "Telkomsel Direct DigiPOS",
  "operator": "TELKOMSEL",
  "productName": "Telkomsel 10.000",
  "price": 10450,
  "failover": "Jalur Utama Langsung"
}
```

### 2. Cek Saldo Server & Akun
- **Method:** `POST`
- **Path:** `/api/v1/cek-saldo`
- **Body:** `{ "token": "PROXMOX_PULSAPAY_SECURE_TOKEN_2026" }`

### 3. Ambil Daftar Produk & Harga
- **Method:** `GET`
- **Path:** `/api/v1/produk?operator=TELKOMSEL&category=PULSA`

### 4. Konfigurasi Bot Telegram Real-time
- **Method:** `GET` / `/api/telegram/config` : Melihat pengaturan bot Telegram saat ini.
- **Method:** `POST` / `/api/telegram/config` : Menyimpan Bot Token, Admin Chat ID, dan opsi filter notifikasi.
- **Method:** `POST` / `/api/telegram/test` : Mengirim pesan tes interaktif ke akun Telegram admin untuk memverifikasi token dan chat ID.

### 5. Tren Keuntungan Harian (Recharts Analytics)
- **Method:** `GET`
- **Path:** `/api/stats/daily-profit?days=7` (Mendukung rentang `7`, `14`, hingga `30` hari)
- **Output:** Array data harian berformat tanggal, laba bersih (`profit`), total omset (`revenue`), jumlah transaksi sukses (`transactions`), persentase margin keuntungan (`margin`), dan ringkasan metrik finansial (`summary`).


