function changeToLatAndLon(address, callback){
  if(!address){ return callback(null); }
  const url = `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(address)}&format=json`;
  $.getJSON(url)
    .done(data => callback(data.length ? [data[0].lat, data[0].lon] : null))
    .fail(() => callback(null));
}

function displayMap(lat, lon, label, zoom = 15){
  const map = L.map('map').setView([lat, lon], zoom);
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
  }).addTo(map);
  L.marker([lat, lon]).addTo(map).bindPopup(label).openPopup();
}

// main --------------------------------------------------
$(function(){
  // 住所文字列生成（空チェック）
  const address = `${form.address1 || ''}${form.address2 || ''}${form.address3 || ''}`;
  const label   = form.name || '';

  if(!address.trim()){
    console.warn("住所が空のため地図を描画しません");
    return;
  }

  changeToLatAndLon(address, coords => {
    if(!coords){
      console.warn("緯度経度取得失敗");
      return;
    }
    const [lat, lon] = coords;
    displayMap(lat, lon, label, 18);
  });
});
