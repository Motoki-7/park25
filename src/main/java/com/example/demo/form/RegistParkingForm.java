package com.example.demo.form;

import java.time.LocalDate;

import com.example.demo.entity.ParkinglotEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistParkingForm {
	private int id;
	private String address1;
	private String address2;
	private String address3;
	private String name;
	private int capacity;
	private int hourlyRate;
	private  LocalDate updateDate;
	
	public ParkinglotEntity toEntity() {
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