package com.example.demo.entity;

import lombok.Data;

@Data
public class RangeEntity {
	private int rangeId;
	private Integer startTime;
	private Integer endTime;
	private boolean monday;
	private boolean tuesday;
	private boolean wednesday;
	private boolean thursday;
	private boolean friday;
	private boolean saturday;
	private boolean sunday;
	private boolean holiday;
}
