package com.example.demo.form;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.entity.ParkinglotEntity;
import com.example.demo.entity.RangeEntity;
import com.example.demo.entity.RatesEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingDetailForm {
	private int id;
	private String address1;
	private String address2;
	private String address3;
	private String name;
	private int capacity;
	private int hourlyRate;
	private  LocalDate updateDate;
	
	// 料金情報
	private int baseFeeRadio; // 基本料金ブロックのセレクター [0, 1]=[終日固定,基本料金]
	private Integer optionRadio; // オプションブロックのセレクター [null, 0, 1]=[選択なし,24時間単位,24時切替]
	private Integer amountDaily; // 終日料金[￥]
	private Integer timeDailly; // 終日単位時間[分]
	private Integer maxRate24h; // 駐車後24時間ごとの最大料金
	private Integer maxRateDaily; // 日付締めでの最大料金
	private List<RatesRangeDto> dailyList;
	
	
	
	public ParkingDetailForm fromEntity(
			ParkinglotEntity parkinglotEnt, 
			List<RatesEntity> ratesEntList
	) {
		ParkingDetailForm form = new ParkingDetailForm();
		// parkinglot
		form.setId(parkinglotEnt.getId());
		form.setName(parkinglotEnt.getName());
		form.setAddress1(parkinglotEnt.getAddress1());
		form.setAddress2(parkinglotEnt.getAddress2());
		form.setAddress3(parkinglotEnt.getAddress3());
		form.setCapacity(parkinglotEnt.getCapacity());
		form.setHourlyRate(parkinglotEnt.getHourlyRate());
		form.setUpdateDate(parkinglotEnt.getUpdateDate());
		// rates
		RatesEntity ratesEnt = ratesEntList.get(0);
		form.setBaseFeeRadio(0); // 終日固定
		if (ratesEnt.getMaxRate24h() != null) {
			// 24時間単位での最大料金アリ
			form.setOptionRadio(0);
			form.setMaxRate24h(ratesEnt.getMaxRate24h());
		}
		else if (ratesEnt.getMaxRateDaily() != null) {
			// 当日最大料金アリ
			form.setOptionRadio(1);
			form.setMaxRateDaily(ratesEnt.getMaxRateDaily());
		}
		// else : optionRadio = null
		form.setAmountDaily(ratesEnt.getAmount());
		form.setTimeDailly(ratesEnt.getTime());
		List<RatesRangeDto> resultList = new ArrayList<>();
		RatesRangeDto dto = new RatesRangeDto();
		dto.setRatesId(ratesEnt.getRatesId());
		resultList.add(dto);
		form.setDailyList(resultList); // ratesIdが失われるためdailyList ０番要素に格納。
		return form;
	}
	// fromEntity 引数にすべてのEntityをもつ
	// 基本料金アリパターン
	public ParkingDetailForm fromEntity(
			ParkinglotEntity parkinglotEnt, 
			List<RatesEntity> ratesEntList,
			List<RangeEntity> rangeEntList
	) {
		ParkingDetailForm form = new ParkingDetailForm();
		// parkinglot
		form.setId(parkinglotEnt.getId());
		form.setName(parkinglotEnt.getName());
		form.setAddress1(parkinglotEnt.getAddress1());
		form.setAddress2(parkinglotEnt.getAddress2());
		form.setAddress3(parkinglotEnt.getAddress3());
		form.setCapacity(parkinglotEnt.getCapacity());
		form.setHourlyRate(parkinglotEnt.getHourlyRate());
		form.setUpdateDate(parkinglotEnt.getUpdateDate());
		// rates & range
		form.setBaseFeeRadio(1); // 基本料金
		if (ratesEntList.get(0).getMaxRate24h() != null) {
			// 24時間単位での最大料金アリ
			form.setOptionRadio(0);
			form.setMaxRate24h(ratesEntList.get(0).getMaxRate24h());
		}
		else if (ratesEntList.get(0).getMaxRateDaily() != null) {
			// 当日最大料金アリ
			form.setOptionRadio(1);
			form.setMaxRateDaily(ratesEntList.get(0).getMaxRateDaily());
		}
		List<RatesRangeDto> resultList = new ArrayList<>();
		for (int i = 0; i < ratesEntList.size(); i++) {
		    RatesEntity ratesEnt = ratesEntList.get(i);
		    RangeEntity rangeEnt = rangeEntList.get(i);
		    RatesRangeDto dto = new RatesRangeDto();
		    dto.setRatesId(ratesEnt.getRatesId());
		    dto.setRangeId(ratesEnt.getRangeId());
		    dto.setStartTime(rangeEnt.getStartTime());
		    dto.setEndTime(rangeEnt.getEndTime());
		    dto.setMonday(rangeEnt.isMonday());
		    dto.setTuesday(rangeEnt.isTuesday());
		    dto.setWednesday(rangeEnt.isWednesday());
		    dto.setThursday(rangeEnt.isThursday());
		    dto.setFriday(rangeEnt.isFriday());
		    dto.setSaturday(rangeEnt.isSaturday());
		    dto.setSunday(rangeEnt.isSunday());
		    dto.setHoliday(rangeEnt.isHoliday());
		    if (ratesEnt.getMaxRateTimely() != null) dto.setMaxRateTimely(ratesEnt.getMaxRateTimely());
		    dto.setAmount(ratesEnt.getAmount());
		    dto.setTime(ratesEnt.getTime());
		    resultList.add(dto);
		}
		form.setDailyList(resultList);
		return form;
	}
}
