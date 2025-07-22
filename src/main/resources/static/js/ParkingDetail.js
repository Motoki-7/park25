// 住所から緯度経度を取得する関数（callback方式）
function changeToLatAndLon(address, callback) {
  if (!address) {
    return callback(null);
  }
  const encoded = encodeURIComponent(address);
  const url = `https://nominatim.openstreetmap.org/search?q=${encoded}&format=json`;

  // jQuery の getJSON で Ajax リクエスト
  $.getJSON(url)
    .done(function(data) {
      if (data.length > 0) {
        // 成功時：最初の結果から lat, lon を渡す
        callback([ data[0].lat, data[0].lon ]);
      } else {
        // 結果なし
        callback(null);
      }
    })
    .fail(function(jqxhr, textStatus, error) {
      console.error('API 呼び出しエラー:', textStatus, error);
      callback(null);
    });
}

// Leaflet を使って地図を表示する関数（そのまま流用）
function displayMap(lat, lon, label, zoom = 15) {
  const map = L.map('map').setView([lat, lon], zoom);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
  }).addTo(map);
  L.marker([lat, lon]).addTo(map)
    .bindPopup(label)
    .openPopup();
}

// DOM 準備が整ったら実行
$(function() {
  // Thymeleaf から渡された form オブジェクトを利用
  const address = `${form.address1 || ''}${form.address2 || ''}${form.address3 || ''}`;
  const label   = form.name || '';

  // Ajax で緯度経度を取得し、コールバックで地図を描画
  changeToLatAndLon(address, function(coords) {
    if (coords) {
      const [lat, lon] = coords;
      displayMap(lat, lon, label, 18);
    }
  });
});