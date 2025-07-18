$(document).ready(() => {
	//初回実行時
	const address = "東京都品川区西五反田３丁目";
	const label = "本社";
	async function changeToLatAndLon(address){
	  const response = await fetch('https://nominatim.openstreetmap.org/search?q=' + encodeURIComponent(address) + '&format=json');
	  const data = await response.json();
	
	  if (data.length > 0) {
	    const lat = data[0].lat;
	    const lon = data[0].lon;
	    console.log("緯度:", lat, "経度:", lon);
	    return [lat, lon];
	  } else {
	    console.log("位置情報が見つかりませんでした");
	    return null;
	  }
	}
});

function displayMap(lat, lon, label, zoom){
  // 地図の初期化（緯度, 経度, ズームレベル）
  var map = L.map('map').setView([lat, lon], zoom);

  // OSMのタイルを読み込む
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
  }).addTo(map);

  // マーカーを追加
  L.marker([lat, lon]).addTo(map)
      .bindPopup(label)
      .openPopup();
}

(async () => {
  const result = await changeToLatAndLon(address);
  if (result) {
    const [lat, lon] = result;
    displayMap(lat, lon, label, 20)
  }
	})();