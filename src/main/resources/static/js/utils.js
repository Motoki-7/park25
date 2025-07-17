function findParking() {
	var url = "/demourl";
	url += '?name=' + $('#id_name').val();
	url += '&address1=' + $('#id_address1').val();
	var result = [
		{
			"id": 1,
			"address1": "東京都",
			"address2": "品川区",
			"address3": "西五反田2丁目",
			"name": "park本社駐車場",
			"capacity": 100,
			"hourlyRate": 500,
			"updateDate": "2025-07-15"
		},
		{
			"id": 2,
			"address1": "東京都",
			"address2": "渋谷区",
			"address3": "道玄坂1丁目",
			"name": "渋谷センター駐車場",
			"capacity": 60,
			"hourlyRate": 600,
			"updateDate": "2025-07-14"
		},
		{
			"id": 3,
			"address1": "神奈川県",
			"address2": "横浜市西区",
			"address3": "みなとみらい3丁目",
			"name": "横浜みなと駐車場",
			"capacity": 120,
			"hourlyRate": 550,
			"updateDate": "2025-07-13"
		},
		{
			"id": 4,
			"address1": "大阪府",
			"address2": "大阪市北区",
			"address3": "梅田1丁目",
			"name": "梅田パーキング",
			"capacity": 80,
			"hourlyRate": 400,
			"updateDate": "2025-07-10"
		},
		{
			"id": 5,
			"address1": "愛知県",
			"address2": "名古屋市中村区",
			"address3": "名駅3丁目",
			"name": "名駅前駐車場",
			"capacity": 90,
			"hourlyRate": 450,
			"updateDate": "2025-07-12"
		}
	];
	makeParkingList(result);
	console.log(url);
	console.log("実行");

	fetch(url)
		.then((response) => {
			if (!response.ok) throw new Error(`通信エラー: ${response.status}`);
			return response.json();
		})
		.then((result) => {
			console.log(result);
			//makeItemList(result);
		})
		.catch((error) => {
			throw new Error("Get失敗");
		});
}

function findParkingAll() {
	var url = "/demourl?name=&address1=null";
	var result = [
		{
			"id": 1,
			"address1": "東京都",
			"address2": "品川区",
			"address3": "西五反田2丁目",
			"name": "park本社駐車場",
			"capacity": 100,
			"hourlyRate": 500,
			"updateDate": "2025-07-15"
		},
		{
			"id": 2,
			"address1": "東京都",
			"address2": "渋谷区",
			"address3": "道玄坂1丁目",
			"name": "渋谷センター駐車場",
			"capacity": 60,
			"hourlyRate": 600,
			"updateDate": "2025-07-14"
		},
		{
			"id": 3,
			"address1": "神奈川県",
			"address2": "横浜市西区",
			"address3": "みなとみらい3丁目",
			"name": "横浜みなと駐車場",
			"capacity": 120,
			"hourlyRate": 550,
			"updateDate": "2025-07-13"
		},
		{
			"id": 4,
			"address1": "大阪府",
			"address2": "大阪市北区",
			"address3": "梅田1丁目",
			"name": "梅田パーキング",
			"capacity": 80,
			"hourlyRate": 400,
			"updateDate": "2025-07-10"
		},
		{
			"id": 5,
			"address1": "愛知県",
			"address2": "名古屋市中村区",
			"address3": "名駅3丁目",
			"name": "名駅前駐車場",
			"capacity": 90,
			"hourlyRate": 450,
			"updateDate": "2025-07-12"
		}
	];
	makeParkingList(result);
	console.log(url);
	console.log("実行");

	fetch(url)
		.then((response) => {
			if (!response.ok) throw new Error(`通信エラー: ${response.status}`);
			return response.json();
		})
		.then((result) => {
			console.log(result);
			//makeItemList(result);
		})
		.catch((error) => {
			throw new Error("Get失敗");
		});
}

function makeParkingList(parkingList) {
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

function createAddress1Pulldown(){
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
}
