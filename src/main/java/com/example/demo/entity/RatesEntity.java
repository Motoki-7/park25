package com.example.demo.entity;

import lombok.Data;

@Data
public class RatesEntity {
	private int ratesId;
	private int parkinglotId;
	private int rangeId;
	private int amount;
	private int time;
	private int maxRateTimely;
	private int maxRate24h;
	private int maxRateDaily;
}
