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
public class RegistParkingForm {
	// 駐車場情報
	private int id; //parkinglotId
	private String address1;
	private String address2;
	private String address3;
	private String name;
	private int capacity;
	private int hourlyRate;
	private  LocalDate updateDate;

	// 料金情報
	private int baseFeeRadio;	// 基本料金ブロックのセレクター [0, 1]=[終日固定,基本料金]
	private Integer optionRadio; // オプションブロックのセレクター [null, 0, 1]=[選択なし,24時間単位,24時切替]
	private Integer amountDaily; // 終日料金[￥]
	private Integer timeDailly;  // 終日単位時間[分]
	private Integer maxRate24h; // 駐車後24時間ごとの最大料金
	private Integer maxRateDaily; // 日付締めでの最大料金
	private List<RatesRangeDto> dailyList;

	
	
	public ParkinglotEntity toParkinglotEntity() {
		ParkinglotEntity ent = new ParkinglotEntity();
		/*Regist は id がAUTO_INCREMENT のため View で含めずに Controller へ
		ent.setId(this.id); */
		ent.setAddress1(this.address1);
		ent.setAddress2(this.address2);
		ent.setAddress3(this.address3);
		ent.setName(this.name);
		ent.setCapacity(this.capacity);
//		ent.setHourlyRate(this.hourlyRate);
		ent.setUpdateDate(this.updateDate);
		return ent;
	}
	
	
	public List<RatesEntity> toRatesEntityList() {
		List<RatesEntity> entList = new ArrayList<>();

		// 基本料金が終日固定
		if (this.baseFeeRadio == 0) {
			RatesEntity ent = new RatesEntity();
			// 料金
			ent.setAmount(this.amountDaily);
			ent.setTime(this.timeDailly);
			// オプション
			if(this.optionRadio == null) {}
			else if (this.optionRadio == 0)
				ent.setMaxRate24h(this.maxRate24h);
			else if (this.optionRadio == 1)
				ent.setMaxRateDaily(this.maxRateDaily);
			entList.add(ent);
		}
		// 基本料金が時間別
		else if (this.baseFeeRadio == 1) {
			for (RatesRangeDto dto : this.dailyList) {
				RatesEntity ent = new RatesEntity();
				// 料金
				ent.setAmount(dto.getAmount());
				ent.setTime(dto.getTime());
				ent.setMaxRateTimely(dto.getMaxRateTimely());
				// オプション
				if(this.optionRadio == null) {}
				else if (this.optionRadio == 0)
					ent.setMaxRate24h(this.maxRate24h);
				else if (this.optionRadio == 1)
					ent.setMaxRateDaily(this.maxRateDaily);
				entList.add(ent);
			}
		}
		return entList;
	}
	
	
	public List<RangeEntity> toRangeEntityList() {
		List<RangeEntity> entList = new ArrayList<>();
		for (RatesRangeDto dto : this.dailyList) {
			RangeEntity ent = new RangeEntity();
			ent.setStartTime(dto.getStartTime());
			ent.setEndTime(dto.getEndTime());
			ent.setMonday(dto.isMonday());
			ent.setTuesday(dto.isTuesday());
			ent.setWednesday(dto.isWednesday());
			ent.setThursday(dto.isThursday());
			ent.setFriday(dto.isFriday());
			ent.setSaturday(dto.isSaturday());
			ent.setSunday(dto.isSunday());
			ent.setHoliday(dto.isHoliday());
			System.out.println("Display ent");
			System.out.println(ent);
			entList.add(ent);
		}
		System.out.println("Display entList");
		System.out.println(entList);
		return entList;
	}
}