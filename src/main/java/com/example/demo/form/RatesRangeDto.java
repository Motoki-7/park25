package com.example.demo.form;

import lombok.Data;

@Data
public class RatesRangeDto {
	private int ratesId;
	private Integer rangeId;
	
	private Integer startTime;
	private Integer endTime;
	private Boolean monday;
	private Boolean tuesday;
	private Boolean wednesday;
	private Boolean thursday;
	private Boolean friday;
	private Boolean saturday;
	private Boolean sunday;
	private Boolean holiday;
	
	private Integer maxRateTimely; // 時間幅ごとの最大料金
	private int amount; // 料金：440円
	private int time;	// 単位時間：/２０分
}
