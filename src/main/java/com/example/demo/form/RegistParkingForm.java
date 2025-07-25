package com.example.demo.form;

import java.time.LocalDate;
import java.util.List;

import com.example.demo.entity.ParkinglotEntity;

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
	private Integer optionRadio; // オプションブロックのセレクター
	private Integer amountDaily; // 終日料金[￥]
	private Integer timeDailly;  // 終日単位時間[分]
	private Integer maxRate24h; // 駐車後24時間ごとの最大料金
	private Integer maxRateDaily; // 日付締めでの最大料金
	private List<RatesRangeDto> dailyList;
	
	public ParkinglotEntity toParkinglotEntity() {
		ParkinglotEntity ent = new ParkinglotEntity();
//		Regist は id がAUTO_INCREMENT のため View で含めずに Controller へ
//		ent.setId(this.id);
		ent.setAddress1(this.address1);
		ent.setAddress2(this.address2);
		ent.setAddress3(this.address3);
		ent.setName(this.name);
		ent.setCapacity(this.capacity);
		ent.setHourlyRate(this.hourlyRate);
		ent.setUpdateDate(this.updateDate);
		return ent;
	}
	
	
}