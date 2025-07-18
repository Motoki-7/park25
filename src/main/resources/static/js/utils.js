function findParking(listStyle) {
	if ($('#id_name').val() == "" && $('#id_address1').val() == null) {
		$('#id_errorMsg').text("値を入力してください");
	} else {
		$('#id_errorMsg').text("");
		var url = "/rest";
		url += '?name=' + $('#id_name').val();
		if ($('#id_address1').val() == null) {
			url += '&address1=';
		} else {
			url += '&address1=' + $('#id_address1').val();
		}

		fetch(url)
			.then((response) => {
				if (!response.ok) throw new Error(`通信エラー: ${response.status}`);
				return response.json();
			})
			.then((result) => {
				console.log(result);
				listStyle(result);
			})
			.catch((error) => {
				throw new Error("Get失敗");
			});
	}

}

function findParkingAll(listStyle) {
	$('#id_errorMsg').text("");
	var url = "/rest?name=&address1=";

	fetch(url)
		.then((response) => {
			if (!response.ok) throw new Error(`通信エラー: ${response.status}`);
			return response.json();
		})
		.then((result) => {
			console.log(result);
			listStyle(result);
		})
		.catch((error) => {
			throw new Error("Get失敗");
		});
}

function deleteParking(id) {
	if (!confirm('本当に削除しますか？')) {
		return; // キャンセルなら処理中断
	}
	$('#id_errorMsg').text("");
	var url = "/rest";
	const data = { id: id };
	console.log("削除");
	var option = {
		method: 'DELETE',
		headers: {
			'Content-Type': 'application/json',
			'Accept': 'application/json'
		},
		body: JSON.stringify(data)
	};
	fetch(url, option)
		.then(response => {
			if (!response.ok) throw new Error('削除に失敗しました');
			const savedSelected = localStorage.getItem("adminSelected");
			if (savedSelected === "findParking") {
				findParking(makeAdminParkingList);
			} else if (savedSelected === "findParkingAll") {
				findParkingAll(makeAdminParkingList);
			}
		})
}

function makeAdminParkingList(parkingList) {
	$("#resultList").empty();
	var $list = $('<table border="1"></table>');
	$list.append('<tr><th>住所1</th><th>住所2</th><th>住所3</th><th>名前</th><th>台数</th><th>料金</th><th>更新日</th><th></th><th></th></tr>');

	$.each(parkingList, function(index, parking) {
		const row = `
					<tr>
						<td>${parking.address1}</td>
						<td>${parking.address2}</td>
						<td>${parking.address3}</td>
						<td>${parking.name}</td>
						<td>${parking.capacity}</td>
						<td>${parking.hourlyRate}</td>
						<td>${parking.updateDate}</td>
						<td>
							<form action="/EditParking/${parking.id}" method="get">
								<input type="submit" value="編集">
							</form>
						</td>
						<td>
							<input type="submit" class="class_delete" data-id="${parking.id}" value="削除">
						</td>
					</tr>
				`;

		$list.append(row);
	});
	$('#resultList').append($list);
}

function makeParkingList(parkingList) {
	$("#resultList").empty();
	var $list = $('<table border="1"></table>');
	$list.append('<tr><th>名前</th><th>住所</th><th>料金</th><th></th></tr>');

	$.each(parkingList, function(index, parking) {
		const row = `
					<tr>
						<td>${parking.name}</td>
						<td>${parking.address1}${parking.address2}${parking.address3}</td>
						<td>${parking.hourlyRate}</td>
						<td>
							<form action="/ParkingDetail/${parking.id}" method="get">
								<input type="submit" value="詳細">
							</form>
						</td>
					</tr>
				`;

		$list.append(row);
	});
	$('#resultList').append($list);
}

function createAddress1Pulldown(initValue) {
	const prefectures = [
		"北海道", "青森県", "岩手県", "宮城県", "秋田県", "山形県", "福島県",
		"茨城県", "栃木県", "群馬県", "埼玉県", "千葉県", "東京都", "神奈川県",
		"新潟県", "富山県", "石川県", "福井県", "山梨県", "長野県",
		"岐阜県", "静岡県", "愛知県", "三重県",
		"滋賀県", "京都府", "大阪府", "兵庫県", "奈良県", "和歌山県",
		"鳥取県", "島根県", "岡山県", "広島県", "山口県",
		"徳島県", "香川県", "愛媛県", "高知県",
		"福岡県", "佐賀県", "長崎県", "熊本県", "大分県", "宮崎県", "鹿児島県", "沖縄県"
	];

	const select = document.getElementById("id_address1");
	prefectures.forEach(pref => {
		const option = document.createElement("option");
		option.value = pref;
		option.textContent = pref;
		select.appendChild(option);
	});
	if(initValue){
		console.log("aaaaaa");
		select.value = initValue;
	}
}
