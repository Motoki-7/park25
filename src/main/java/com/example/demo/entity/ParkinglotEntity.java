package com.example.demo.entity;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ParkinglotEntity {
	private int id;
	private String address1;
	private String address2;
	private String address3;
	private String name;
	private int capacity;
	private int hourlyRate;
	private LocalDate updateDate;
	
}
