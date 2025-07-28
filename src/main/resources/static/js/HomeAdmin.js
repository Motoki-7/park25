$(document).ready(() => {
	//初回実行時
	const savedName = localStorage.getItem("adminSearchName");
	const savedAddress1 = localStorage.getItem("adminSearchAddress1");
	const savedSelected = localStorage.getItem("adminSelected");
	createAddress1Pulldown();
	$('#id_name').val(savedName);
	$('#id_address1').val(savedAddress1);
	if(savedSelected === "findParking"){
		findParking(makeAdminParkingList);
	}else if(savedSelected === "findParkingAll"){
		findParkingAll(makeAdminParkingList);
	}
	//アクション
	$('#id_search').click(() => {
		findParking(makeAdminParkingList);
		localStorage.setItem("adminSelected", "findParking");
	});
	$('#id_searchAll').click(() => {
		findParkingAll(makeAdminParkingList);
		localStorage.setItem("adminSelected", "findParkingAll");
	});
	$('#resultList').on("click", ".class_delete", function () {
		const id = $(this).data('id');
		deleteParking(id);
	});
	// 入力変更をlocalStorageに保存
	$('#id_name').on('input', function () {
		localStorage.setItem("adminSearchName", $(this).val());
	});

	$('#id_address1').on('change', function () {
		localStorage.setItem("adminSearchAddress1", $(this).val());
	});
	
	
	// ▼▲ボタン押下時のイベント（ソート切り替え）
	$('#resultList').on('click','.sortBtnAdmin',function(){
		
		const key = $(this).data('key');
		const currentKey = localStorage.getItem("sortAsc") === "true";
		
		const newAsc = (key === currentKey) ? !currentAsc : true;
		
		
		localStorage.setItem("sortKey",key);
		localStorage.setItem("sortAsc",newAsc);
		
		
		const savedSelected = localStorage.getItem("adminSelected");
		
		if(savedSelected === "findParking"){
			findParking(makeAdminParkingList);
			
		}else if (savedSelected === "findParkingAll"){
			
			findParkingAll(makeAdminParkingList);
			
		}	
	});
	
	
});