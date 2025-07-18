// === 外部スクリプト例: ParkingDetail.js (/src/main/resources/static/js/ParkingDetail.js) ===

// 住所から緯度経度を取得する関数
async function changeToLatAndLon(address) {
  if (!address) return null;
  const encoded = encodeURIComponent(address);
  const url = `https://nominatim.openstreetmap.org/search?q=${encoded}&format=json`;
  try {
    const res = await fetch(url);
    const data = await res.json();
    if (data.length > 0) {
      const { lat, lon } = data[0];
      return [lat, lon];
    }
  } catch (e) {
    console.error('API 呼び出しエラー:', e);
  }
  return null;
}

// Leaflet.js を使って地図を初期化し、マーカーを表示する関数
function displayMap(lat, lon, label, zoom = 15) {
  const map = L.map('map').setView([lat, lon], zoom);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
  }).addTo(map);
  L.marker([lat, lon]).addTo(map)
    .bindPopup(label)
    .openPopup();
}

// DOMContentLoaded イベントで初期化
window.addEventListener('DOMContentLoaded', async () => {
  const address = `${form.address1 || ''}${form.address2 || ''}${form.address3 || ''}`;
  const label = `${form.name || ''}`;
  const result = await changeToLatAndLon(address);
  if (result) {
    const [lat, lon] = result;
    displayMap(lat, lon, label, 18);
  }
});