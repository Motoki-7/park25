package com.example.demo.form;

import com.example.demo.entity.RangeEntity;
import com.example.demo.entity.RatesEntity;

import lombok.Data;

@Data
public class RatesRangeDto {
	private RatesEntity rates;
	private RangeEntity range;
}
