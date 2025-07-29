package com.example.demo.entity;

import lombok.Data;

@Data
public class RatesEntity {
	private int ratesId;
	private int parkinglotId;
	private Integer rangeId;
	private int amount; // 料金[円]
	private int time; // 料金の分あたりに対応した時間[分]
	private Integer maxRateTimely; // この時間幅での最大料金（終日固定の時はｎｕｌｌ）
	private Integer maxRate24h; // 駐車後24時間ごとの最大料金
	private Integer maxRateDaily; // 日付締めでの最大料金
}
