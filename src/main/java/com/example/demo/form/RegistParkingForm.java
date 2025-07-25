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
	private int baseFeeRadio;	// 基本料金ブロックのセレクター
	private int optionRadio; // オプションブロックのセレクター
	private int amountDaily; // 終日料金[￥]
	private int timeDailly;  // 終日単位時間[分]
	private int maxRate24h; // 駐車後24時間ごとの最大料金
	private int maxRateDaily; // 日付締めでの最大料金
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
		ent.setHourlyRate(this.hourlyRate);
		ent.setUpdateDate(this.updateDate);
		return ent;
	}

	public List<RatesEntity> toRatesEntityList() {
		List<RatesEntity> entList = new ArrayList<>();
		RatesEntity ent = new RatesEntity();

		// 基本料金が終日固定
		if (this.baseFeeRadio == 0) {
			// 料金
			ent.setAmount(this.amountDaily);
			ent.setTime(this.timeDailly);
			// オプション
			if (this.optionRadio == 0)
				ent.setMaxRate24h(this.maxRate24h);
			else if (this.optionRadio == 1)
				ent.setMaxRateDaily(this.maxRateDaily);
			entList.add(ent);
		}
		// 基本料金が時間別
		else if (this.baseFeeRadio == 1) {
			for (RatesRangeDto dto : this.dailyList) {
				// 料金
				ent.setAmount(dto.getAmount());
				ent.setTime(dto.getTime());
				ent.setMaxRateTimely(dto.getMaxRateTimely());
				// オプション
				if (this.optionRadio == 0)
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
		RangeEntity ent = new RangeEntity();
		for (RatesRangeDto dto : this.dailyList) {
			ent.setStartTime(dto.getStartTime());
			ent.setEndTime(dto.getEndTime());
			ent.setMonday(dto.getMonday());
			ent.setTuesday(dto.getTuesday);
			ent.setWednesday(dto.getWednesday);
			ent.setThursday(dto.getThursday);
			ent.setFriday(dto.getFriday);
			ent.setSaturday(dto.getSaturday);
			ent.setSunday(dto.getSunday);
			ent.setHoliday(dto.getHoliday);
			entList.add(ent);
		}
		return entList;
	}
}