$(document).ready(() => {
	//初回実行時
	const savedName = localStorage.getItem("userSearchName");
	const savedAddress1 = localStorage.getItem("userSearchAddress1");
	const savedSelected = localStorage.getItem("userSelected");
	createAddress1Pulldown();
	$('#id_name').val(savedName);
	$('#id_address1').val(savedAddress1);
	if(savedSelected === "findParking"){
		findParking(makeParkingList);
	}else if(savedSelected === "findParkingAll"){
		findParkingAll(makeParkingList);
	}
	//アクション
	$('#id_search').click(() => {
		findParking(makeParkingList);
		localStorage.setItem("userSelected", "findParking");
	});
	$('#id_searchAll').click(() => {
		findParkingAll(makeParkingList);
		localStorage.setItem("userSelected", "findParkingAll");
	});
	// 入力変更をlocalStorageに保存
	$('#id_name').on('input', function () {
		localStorage.setItem("userSearchName", $(this).val());
	});

	$('#id_address1').on('change', function () {
		localStorage.setItem("userSearchAddress1", $(this).val());
	});
});