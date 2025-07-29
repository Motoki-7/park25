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
function feeCalculation(parkinglotId) {
	console.log(parkinglotId);
	let id_entryDate = $('#id_entryDate').val();
	let id_entryTime = $('#id_entryTime').val();
	let id_exitDate = $('#id_exitDate').val();
	let id_exitTime = $('#id_exitTime').val();
	$('#id_calcResult').text("-" + "円");
	if (id_entryDate != "" && id_entryTime != "" && id_exitDate != "" && id_exitTime != "") {
		$('#id_errorMsg').text("");
		const entry = new Date(`${id_entryDate}T${id_entryTime}`);
		const exit = new Date(`${id_exitDate}T${id_exitTime}`);
		if (entry >= exit) {
			$('#id_errorMsg').text("入庫時刻は出庫時刻より前でなければなりません。");
			return ;
		}
		var url = "/aaa?";
		url += "id=" + parkinglotId;
		url += "&entryDate=" + id_entryDate;
		url += "&entryTime=" + id_entryTime;
		url += "&exitDate=" + id_exitDate;
		url += "&exitTime=" + id_exitTime;
		fetch(url)
			.then((response) => {
				if (!response.ok) throw new Error(`通信エラー: ${response.status}`);
				return response.json();
			})
			.then((result) => {
				console.log(result);
				$('#id_calcResult').text(result + "円");
			})
			.catch((error) => {
				throw new Error("Get失敗");
			});
	} else {
		$('#id_errorMsg').text("値を入力してください");
	}
}
