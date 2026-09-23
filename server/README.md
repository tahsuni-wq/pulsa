# PulsaPay H2H Server & Web Dashboard (Proxmox Gateway)

Aplikasi server backend & web dashboard terpusat untuk memantau transaksi penjualan pulsa & PPOB, berkomunikasi dengan berbagai agregator H2H (Digiflazz, VIP Reseller, Tripay), melakukan failover otomatis, serta menjadi gateway pusat untuk aplikasi Android PulsaPay.

---

## 🌟 Fitur Utama

1. **Web Dashboard Modern & Real-time:**
   - Ringkasan omset harian, estimasi keuntungan bersih (margin laba), dan tingkat kesuksesan.
   - Grafik aktivitas transaksi harian interaktif.
   - Monitor transaksi real-time dengan auto-refresh setiap 5 detik.
   - Detail transaksi lengkap dengan raw payload JSON request & response ke supplier.
2. **Komunikasi Multi-Agregator H2H:**
   - Terintegrasi langsung dengan **Digiflazz**, **VIP Reseller**, dan **Tripay**.
   - Cek saldo real-time langsung ke supplier dengan 1 klik.
   - Smart Failover: jika supplier utama error atau sedang gangguan (RC 49), otomatis dialihkan ke supplier backup.
   - Mode Sandbox / Simulasi aman untuk pengujian.
3. **Gateway API untuk Aplikasi Android:**
   - Endpoint aman: `POST /api/v1/transaksi` & `POST /api/v1/cek-saldo`.
   - Menggunakan autentikasi Token & PIN.
4. **Simulator / Test Transaksi:**
   - Form uji coba kirim pulsa langsung dari browser web.
5. **Webhook Handler:**
   - Menerima callback status otomatis dari Digiflazz, VIP Reseller, dan Tripay.

---

## 🚀 Cara Menjalankan di Proxmox (LXC Container / VM)

### Langkah 1: Masuk ke Container Proxmox Anda via SSH / Terminal
Contoh jika Anda berada di `ct102` (LXC Ubuntu/Debian):
```bash
# Masuk ke folder server
cd /path/ke/folder/server

# Beri izin eksekusi script instalasi
chmod +x deploy-proxmox.sh

# Jalankan script
./deploy-proxmox.sh
```

Atau instalasi manual dengan 3 baris perintah:
```bash
npm install
node server.js
```

Server akan langsung aktif di port `3000`.

---

## 🌐 Menghubungkan ke Domain & SSL (Nginx Reverse Proxy)

Buat file konfigurasi Nginx di `/etc/nginx/sites-available/pulsapay`:

```nginx
server {
    server_name api.domain-anda.com;

    location / {
        proxy_pass http://127.0.0.1:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }
}
```

Aktifkan konfigurasi dan pasang SSL gratis:
```bash
ln -s /etc/nginx/sites-available/pulsapay /etc/nginx/sites-enabled/
nginx -t
systemctl reload nginx
certbot --nginx -d api.domain-anda.com
```

---

## 📱 Menghubungkan Aplikasi Android PulsaPay ke Server Proxmox

Di aplikasi Android Anda:
1. Buka tab **Provider H2H**.
2. Klik tombol **`+` (Tambah Provider)**.
3. Isi konfigurasi:
   - **Nama:** Server Proxmox Saya
   - **Tipe Provider:** `CUSTOM REST`
   - **Base URL Endpoint:** `https://api.domain-anda.com/api/v1`
   - **API Key:** `PROXMOX_PULSAPAY_SECURE_TOKEN_2026`
   - **PIN:** `1234`
   - **Mode Sandbox:** Nonaktifkan (jika sudah siap transaksi nyata)
4. Simpan, lalu jadikan sebagai **Jalur Utama**.
