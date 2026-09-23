#!/usr/bin/env bash
# ==============================================================================
# Script Instalasi Otomatis Server PulsaPay H2H di Proxmox LXC / VM (Ubuntu/Debian)
# ==============================================================================

set -e

echo "=================================================="
echo "🚀 Memulai Instalasi PulsaPay H2H Server di Proxmox"
echo "=================================================="

# 1. Update package & Install Node.js jika belum ada
if ! command -v node &> /dev/null; then
    echo "📦 Menginstall Node.js 20 LTS & dependencies..."
    apt-get update -y
    apt-get install -y curl git ufw nginx certbot python3-certbot-nginx
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

# 3. Buat Systemd Service agar server otomatis jalan saat Proxmox booting
echo "⚙️ Mendaftarkan systemd service (pulsapay-server.service)..."

cat <<EOF > /etc/systemd/system/pulsapay-server.service
[Unit]
Description=PulsaPay H2H Gateway & Web Dashboard
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=$APP_DIR
ExecStart=$(which node) $APP_DIR/server.js
Restart=always
RestartSec=5
Environment=PORT=3000
Environment=NODE_ENV=production

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable pulsapay-server
systemctl restart pulsapay-server

echo "=================================================="
echo "✅ Server Berhasil Dijalankan!"
echo "🌐 Buka Web Dashboard di: http://$(hostname -I | awk '{print $1}'):3000"
echo "=================================================="
echo ""
echo "Catatan untuk menghubungkan Domain & SSL Nginx:"
echo "Gunakan konfigurasi Nginx Reverse Proxy ke port 3000."
