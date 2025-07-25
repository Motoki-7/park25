package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ParkinglotDao;
import com.example.demo.entity.ParkinglotEntity;
import com.example.demo.form.RatesListWrapper;

@Service
public class HomeAdminService {
	
	@Autowired
	ParkinglotDao parkinglotDao;
	
	//全件表示
	public List<ParkinglotEntity>selectAll(){
		List<ParkinglotEntity>parkinglotList = parkinglotDao.selectAll();
		return parkinglotList;
	}
	
	// 
	public RatesListWrapper selectRatesRangeList(int parkinglotId) {
		RatesListWrapper ratesList;
		
		return ratesList;
	}
	
	
	//詳細表示
	public ParkinglotEntity selectByParkinglotId(int id){
		List<ParkinglotEntity>resultList = parkinglotDao.selectById(id);
		ParkinglotEntity ent = resultList.get(0);
		return ent;
	}
}
