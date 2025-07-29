package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ParkinglotDao;
import com.example.demo.dao.RangeDao;
import com.example.demo.dao.RatesDao;
import com.example.demo.entity.ParkinglotEntity;
import com.example.demo.entity.RangeEntity;
import com.example.demo.entity.RatesEntity;
import com.example.demo.form.RegistParkingForm;

@Service
public class RegistParkingService {
	@Autowired ParkinglotDao parkinglotDao;
	@Autowired RatesDao ratesDao;
	@Autowired RangeDao rangeDao;
	
	
	public void insert(RegistParkingForm form) {
		ParkinglotEntity parkinglotEnt = form.toParkinglotEntity();
		int parkinglotId = parkinglotDao.insert(parkinglotEnt);
		
		List<RatesEntity> ratesEntList = form.toRatesEntityList();
		// 終日固定ならrangeはnullに
		Integer rangeId;
		if (form.getBaseFeeRadio() == 0) {
			rangeId = null;
			RatesEntity ratesEnt = ratesEntList.get(0);
			ratesDao.insert(ratesEnt, parkinglotId, rangeId);
		}
		else if (form.getBaseFeeRadio() == 1) {
			List<RangeEntity> rangeEntList = form.toRangeEntityList();
			for (int i=0; i<form.getDailyList().size(); i++) {
				RatesEntity ratesEnt = ratesEntList.get(i);
				RangeEntity rangeEnt = rangeEntList.get(i);
				rangeId = rangeDao.insert(rangeEnt);
				ratesDao.insert(ratesEnt, parkinglotId, rangeId);
			}
		}
	}
}