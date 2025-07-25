package com.example.demo.form;

import lombok.Data;

@Data
public class RatesRangeDto {
	private int ratesId;
	private int rangeId;
	
	private int startTime;
	private int endTime;
	private boolean monday;
	private boolean tuesday;
	private boolean wednesday;
	private boolean thursday;
	private boolean friday;
	private boolean saturday;
	private boolean sunday;
	private boolean holiday;
	
	private int maxRateTimely; // 時間幅ごとの最大料金
	private int amount; // 料金：440円
	private int time;	// 単位時間：/２０分
}
