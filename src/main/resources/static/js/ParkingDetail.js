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
		var url = "/calc?";
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

function feeDisplay(formBuf){
	const table = document.createElement("table");
	table.border = "1";
	const dailyList = formBuf.dailyList;
	const days = ["monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday", "holiday"];
	const daysJ = ["月", "火", "水", "木", "金", "土", "日", "祝"];
	if(formBuf.baseFeeRadio == 0){
		const row = document.createElement("tr");
		const td1 = document.createElement("td");
		td1.textContent = "全日";
		row.appendChild(td1);
		const td2 = document.createElement("td");
		const feeBuf = dailyList[0].timeDailly + "分/" + dailyList[0].amountDaily + "円";
		td2.textContent = "00:00-00:00　" + feeBuf || "";
		row.appendChild(td2);
		table.appendChild(row);
	}else if(formBuf.baseFeeRadio == 1){
		for (let i = 0; i < dailyList.length; i++) {
		  const row = document.createElement("tr");
		  const daily = dailyList[i];
		  const td1 = document.createElement("td");
		  let stringBuf = "";
		  for(let j = 0; j < days.length; j++){
			if(daily[days[j]]){
			  stringBuf += daysJ[j];
			}
		  }
		  td1.textContent = stringBuf;
		  row.appendChild(td1);
		  const td2 = document.createElement("td");
		  const timeBuf = daily.startTime + ":00" + "-" + daily.endTime + ":00" + "　";
		  const feeBuf = daily.time + "分/" + daily.amount + "円　" + (daily.maxRateTimely ? "最大" + daily.maxRateTimely + "円": "");
		  td2.textContent =  timeBuf + feeBuf;
		  row.appendChild(td2);
		  table.appendChild(row);
		}  
	}else{
		
	}
	if(formBuf.optionRadio == 0){
		const lastRow = document.createElement("tr");
		const lastCell = document.createElement("td");
		lastCell.colSpan = 2;
		lastCell.textContent = "駐車後24時間最大　" + formBuf.maxRate24h + "円"|| "";
		lastCell.style.textAlign = "center";
		lastRow.appendChild(lastCell);
		table.appendChild(lastRow);
	}else if(formBuf.optionRadio == 1){
		const lastRow = document.createElement("tr");
		const lastCell = document.createElement("td");
		lastCell.colSpan = 2;
		lastCell.textContent = "当日最大　" + formBuf.maxRateDaily + "円"|| "";
		lastCell.style.textAlign = "center";
		lastRow.appendChild(lastCell);
		table.appendChild(lastRow);
	}else{
		
	}
	document.getElementById("tableContainer").appendChild(table);
}
