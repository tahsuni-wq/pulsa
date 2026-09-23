#!/usr/bin/env bash
# ==============================================================================
# Script Instalasi Otomatis Server PulsaPay High-Concurrency di Proxmox LXC / VM (Ubuntu/Debian)
# ==============================================================================

set -e

echo "=================================================="
echo "🚀 Memulai Instalasi Server PulsaPay H2H (Ribuan Customer)"
echo "📡 Direct Telco (Telkomsel, Indosat, XL, PLN) + Agregator H2H"
echo "=================================================="

# 1. Update package & Install Node.js LTS jika belum ada
if ! command -v node &> /dev/null; then
    echo "📦 Menginstall Node.js 20 LTS & dependencies sistem..."
    apt-get update -y
    apt-get install -y curl git ufw nginx certbot python3-certbot-nginx build-essential
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash -
    apt-get install -y nodejs
else
    echo "✅ Node.js sudah terpasang: $(node -v)"
fi

# 2. Setup Folder & Install Dependencies
APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$APP_DIR"

echo "📦 Menginstall package npm (express, cors)..."
npm install --production

# 3. Tuning Limit Koneksi Sistem (Untuk ribuan customer simultan)
echo "⚡ Mengoptimalkan limit koneksi sistem (ulimit file descriptors)..."
cat <<EOF > /etc/security/limits.d/99-pulsapay.conf
* soft nofile 65535
* hard nofile 65535
root soft nofile 65535
root hard nofile 65535
EOF

# 4. Buat Systemd Service agar server otomatis jalan saat Proxmox booting
echo "⚙️ Mendaftarkan systemd service (pulsapay-server.service)..."

cat <<EOF > /etc/systemd/system/pulsapay-server.service
[Unit]
Description=PulsaPay High-Concurrency Gateway & Web Server
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=$APP_DIR
ExecStart=$(which node) $APP_DIR/server.js
Restart=always
RestartSec=3
LimitNOFILE=65535
Environment=APP_PORT=3000
Environment=NODE_ENV=production

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable pulsapay-server
systemctl restart pulsapay-server

IP_ADDR=$(hostname -I | awk '{print $1}')

echo "=================================================="
echo "✅ Server Berhasil Dijalankan & Siap Melayani Ribuan Customer!"
echo "🌐 Akses Web Portal & Dashboard: http://${IP_ADDR}:3000"
echo "📡 Gateway REST API Android: http://${IP_ADDR}:3000/api/v1/transaksi"
echo "=================================================="
