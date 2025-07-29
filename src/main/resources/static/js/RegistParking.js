$(document).ready(() => {
	//初回実行時
	createAddress1Pulldown();
	//	initBaseFeeRadioChange("[[${form.baseFeeRadio}]]");
	//	initOptionRadioChange("[[${form.optionRadio}]]");
});

function setOptionInputs(value) {
	const id_input0List = document.querySelectorAll(".id_option0");
	const id_input1List = document.querySelectorAll(".id_option1");
	const allInputs = [...id_input0List, ...id_input1List];

	// 一旦全て無効化
	allInputs.forEach(input => {
		input.disabled = true;
		input.style.opacity = 0.5;
	});

	// 指定のvalueに該当するクラスを有効化
	if (value === "0") {
		id_input0List.forEach(input => {
			input.disabled = false;
			input.style.opacity = 1;
		});
	} else if (value === "1") {
		id_input1List.forEach(input => {
			input.disabled = false;
			input.style.opacity = 1;
		});
	}
}
function optionRadioChange(selected) {
	const checkboxes = document.querySelectorAll(".optionCheckbox");
	if (!selected.checked) {
		// チェック外れた場合は全部無効化
		setOptionInputs(null);
		return;
	}
	// 他のチェックボックスはoff
	checkboxes.forEach(cb => {
		if (cb !== selected) cb.checked = false;
	});
	setOptionInputs(selected.value);
}

function initOptionRadioChange(selectedValue) {
	const checkboxes = document.querySelectorAll(".optionCheckbox");
	checkboxes.forEach(cb => {
		cb.checked = (cb.value === selectedValue);
	});
	setOptionInputs(selectedValue);
}
function initBaseFeeRadioChange(selected) {
	const id_basefee0List = document.querySelectorAll(".id_basefee0");
	const id_basefee1List = document.querySelectorAll(".id_basefee1");
	const allInputs = [
		...id_basefee0List,
		...id_basefee1List,
	];
	allInputs.forEach(input => {
		input.disabled = true;
		input.style.opacity = 0.5;
	});
	if (selected === "0") {
		id_basefee0List.forEach(input => {
			input.disabled = false;
			input.style.opacity = 1;
		});
	} else if (selected === "1") {
		id_basefee1List.forEach(input => {
			input.disabled = false;
			input.style.opacity = 1;
		});
	}
}
function baseFeeRadioChange(selected) {
	const id_basefee0List = document.querySelectorAll(".id_basefee0");
	const id_basefee1List = document.querySelectorAll(".id_basefee1");
	const allInputs = [
		...id_basefee0List,
		...id_basefee1List,
	];
	allInputs.forEach(input => {
		input.disabled = true;
		input.style.opacity = 0.5;
	});
	if (selected.value === "0") {
		id_basefee0List.forEach(input => {
			input.disabled = false;
			input.style.opacity = 1;
		});
	} else if (selected.value === "1") {
		id_basefee1List.forEach(input => {
			input.disabled = false;
			input.style.opacity = 1;
		});
	}
}
function addBaseFee(index) {
	const $list = $(`
					      <div class="baseFeeBlock">
					        <table>
					          <tr>
					            <th>月曜</th>
					            <th>火曜</th>
					            <th>水曜</th>
					            <th>木曜</th>
					            <th>金曜</th>
					            <th>土曜</th>
					            <th>日曜</th>
					            <th>祝日</th>
								<th></th>
					          </tr>
							  <tr>
							    <td><input class="id_basefee1" type="checkbox" name="dailyList[${index}].monday" value="true"></td>
							    <td><input class="id_basefee1" type="checkbox" name="dailyList[${index}].tuesday" value="true"></td>
							    <td><input class="id_basefee1" type="checkbox" name="dailyList[${index}].wednesday" value="true"></td>
							    <td><input class="id_basefee1" type="checkbox" name="dailyList[${index}].thursday" value="true"></td>
							    <td><input class="id_basefee1" type="checkbox" name="dailyList[${index}].friday" value="true"></td>
							    <td><input class="id_basefee1" type="checkbox" name="dailyList[${index}].saturday" value="true"></td>
							    <td><input class="id_basefee1" type="checkbox" name="dailyList[${index}].sunday" value="true"></td>
							    <td><input class="id_basefee1" type="checkbox" name="dailyList[${index}].holiday" value="true"></td>
							    <td><button type="button" class="id_basefee1 deleteBaseFeeBtn">削除</button></td>
							  </tr>
					        </table><br>
					        <div>
								<div class="time_selecter">
									<span>開始時刻:</span>
									<select class="id_basefee1 hour-select" name="dailyList[${index}].startTime"></select>
									<span>時</span>
									<span>　終了時刻:</span>
									<select class="id_basefee1 hour-select" name="dailyList[${index}].endTime"></select>
									<span>時</span>
								</div>
								<div class="fee-input-row">
						          <input type="number" class="id_basefee1" name="dailyList[${index}].time" min="0" required>
						          <span>分</span>
						          <input type="number" class="id_basefee1" name="dailyList[${index}].amount" min="0" required>
						          <span>円</span>
								  <span>最大</span>
						          <input type="number" class="id_basefee1" name="dailyList[${index}].maxRateTimely" min="0">
						          <span>円</span>
								 </div>
					        </div>
					      </div>
					    `);
	// 時間セレクトボックスにオプション追加
	$list.find(".hour-select").each(function() {
		for (let i = 0; i <= 23; i++) {
			$(this).append(`<option value="${i}">${i}</option>`);
		}
	});

	// 削除ボタンにイベントを追加
	$list.find(".deleteBaseFeeBtn").on("click", function() {
		$(this).closest(".baseFeeBlock").remove();
		reindexBaseFeeBlocks();
	});
	$("#baseFee").append($list);

	function reindexBaseFeeBlocks() {
		const blocks = document.querySelectorAll(".baseFeeBlock");
		blocks.forEach((block, newIndex) => {
			block.querySelectorAll("input, select").forEach(el => {
				if (el.name) {
					el.name = el.name.replace(/dailyList\[\d+\]/, `dailyList[${newIndex}]`);
				}
			});
		});
	}
}

function rangeValidation() {
	const isSelected = document.getElementById("baseFeeRadioButton0").checked;
	if (isSelected) {
	    return true;
	}
	const rows = document.getElementsByClassName("baseFeeBlock");
	let array724 = [];
	for (let i = 0; i < 7; i++) {
		const row = [];
		for (let j = 0; j < 24; j++) {
			row.push(false);
		}
		array724.push(row);
	}
	let array724error = [];
	for (let i = 0; i < 7; i++) {
		const row = [];
		for (let j = 0; j < 24; j++) {
			row.push(false);
		}
		array724error.push(row);
	}
	const days = ["monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"];
	const daysJ = ["月曜", "火曜", "水曜", "木曜", "金曜", "土曜", "日曜"];
	for (let i = 0; i < rows.length; i++) {
		const row = rows[i];
		const startTime = row.querySelector('select[name$=".startTime"]');
		const endTime = row.querySelector('select[name$=".endTime"]');
		let startTimeValue = Number(startTime?.value);
		let endTimeValue = Number(endTime?.value);
		if (startTimeValue >= endTimeValue) {
			endTimeValue += 24;
		}
		days.forEach((day, dayIndex) => {
			const checkbox = row.querySelector(`input[name$=".${day}"]`);
			if (checkbox?.checked) {
				for (let j = startTimeValue; j < endTimeValue; j++) {
					if (j < 24) {
						if (array724[dayIndex][j]) {
							array724error[dayIndex][j] = true;
						} else {
							array724[dayIndex][j] = true;
						}
					} else {
						let dayIndexbuf = dayIndex + 1;
						if (dayIndexbuf > 6) {
							dayIndexbuf = 0;
						}
						if (array724[dayIndexbuf][j - 24]) {
							array724error[dayIndexbuf][j - 24] = true;
						} else {
							array724[dayIndexbuf][j - 24] = true;
						}
					}
				}
			}
		});
	}
	let result = [];
	daysJ.forEach((day, dayIndex) => {
		let start = null;
		for (let i = 0; i < array724error[dayIndex].length; i++) {
			if (array724error[dayIndex][i]) {
				if (start === null) {
					start = i;
				}
			} else {
				if (start !== null) {
					result.push(`${day} ${start}時から${i}時`);
					start = null;
				}
			}
		}

		// 最後が true で終わる場合の処理
		if (start !== null) {
			result.push(`${day} ${start}時から${array724error[dayIndex].length - 24}時`);
		}
	});
	if (result.length !== 0) {
		result.unshift(`期間が重複しています。`);
	}

	let result2 = [];
	daysJ.forEach((day, dayIndex) => {
		let start = null;
		for (let i = 0; i < array724[dayIndex].length; i++) {
			if (!array724[dayIndex][i]) {
				if (start === null) {
					start = i;
				}
			} else {
				if (start !== null) {
					result2.push(`${day} ${start}時から${i}時`);
					start = null;
				}
			}
		}

		// 最後が true で終わる場合の処理
		if (start !== null) {
			result2.push(`${day} ${start}時から${array724[dayIndex].length - 24}時`);
		}
	});
	if (result2.length !== 0) {
		result2.unshift(`未設定の期間があります。`);
	}
	const outputDivUsed = document.getElementById("outputRangeValidationUsed");
	const outputDivNotSet = document.getElementById("outputRangeValidationNotSet");
	outputDivUsed.innerHTML = result.map(line => `<div>${line}</div>`).join("");
	outputDivNotSet.innerHTML = result2.map(line => `<div>${line}</div>`).join("");
	if (result.length == 0 && result2.length == 0) {
		return true;
	}
	return false;
}

function baseFeeCheckboxesValidation() {
	const isSelected = document.getElementById("baseFeeRadioButton0").checked;
	if (isSelected) {
	    return true;
	}
	const blocks = document.querySelectorAll(".baseFeeBlock");
	let isValid = true;
	$('#outputBaseFeeCheckboxesValidation').text("");
	for (const block of blocks) {
		const checkboxes = block.querySelectorAll('input[type="checkbox"]');
		const isChecked = Array.from(checkboxes).some(cb => cb.checked);

		if (!isChecked) {
			$('#outputBaseFeeCheckboxesValidation').text("曜日が設定されていない項目があります。");
			isValid = false;
			break; // ← これが now 有効
		}
	}

	return isValid;
}
