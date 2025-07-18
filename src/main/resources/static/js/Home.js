$(document).ready(() => {
	//初回実行時
	makeAdminParkingList(JSON.parse(localStorage.getItem("parkingResult")));
	createAddress1Pulldown();
	//localStorageから復元
	const savedName = localStorage.getItem("userSearchName");
	const savedAddress1 = localStorage.getItem("userSearchAddress1");
	$('#id_name').val(savedName);
	$('#id_address1').val(savedAddress1);

	console.log(savedName);
	console.log(savedAddress1);
	//アクション
	$('#id_search').click(()=>findParking(makeParkingList));
	$('#id_searchAll').click(()=>findParkingAll(makeParkingList));
	// 入力変更をlocalStorageに保存
	$('#id_name').on('input', function () {
		localStorage.setItem("userSearchName", $(this).val());
	});

	$('#id_address1').on('change', function () {
		localStorage.setItem("userSearchAddress1", $(this).val());
	});
});