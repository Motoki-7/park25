package com.example.demo.entity;

import lombok.Data;

@Data
public class RatesEntity {
	private int ratesId;
	private int parkinglotId;
	private Integer rangeId;
	private int amount;
	private int time;
	private Integer maxRateTimely;
	private Integer maxRate24h;
	private Integer maxRateDaily;
}
