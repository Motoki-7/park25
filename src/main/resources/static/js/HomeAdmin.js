$(document).ready(() => {
	//初回実行時
	findParkingAll(makeAdminParkingList);
	createAddress1Pulldown();
	//アクション
	$('#id_search').click(()=>findParking(makeAdminParkingList));
	$('#id_searchAll').click(()=>findParkingAll(makeAdminParkingList));
	$('#resultList').on("click", ".class_delete", function () {
		if (!confirm('本当に削除しますか？')) {
			return; // キャンセルなら処理中断
		}
		var url = "/rest";
		console.log("削除");

		var data = {
			'id': $(this).data('id')
		}
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
			})
	});
});