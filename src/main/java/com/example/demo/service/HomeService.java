package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ParkinglotDao;
import com.example.demo.dao.RangeDao;
import com.example.demo.dao.RatesDao;
import com.example.demo.entity.ParkinglotEntity;
import com.example.demo.entity.RangeEntity;
import com.example.demo.entity.RatesEntity;
import com.example.demo.form.ParkingDetailForm;

@Service
public class HomeService {
	
	@Autowired ParkinglotDao parkinglotDao;
	@Autowired RatesDao ratesDao;
	@Autowired RangeDao rangeDao;
	
	//全件表示
//	public List<ParkinglotEntity> selectAll(){
//		List<ParkinglotEntity> parkinglotList = parkinglotDao.selectAll();
//		
//		return parkinglotList;
//	}
	
	//詳細表示
	public ParkingDetailForm selectById(int parkinglotId){
		ParkingDetailForm form = new ParkingDetailForm();
		// parkinglot, rates データをDao経由取得
		List<ParkinglotEntity> resultParkinglotList = parkinglotDao.selectById(parkinglotId);
		ParkinglotEntity parkinglotEnt = resultParkinglotList.get(0);
		List<RatesEntity> resultRatesEntList = ratesDao.selectByParkinglotId(parkinglotId);
		// ratesからrangeが存在するか確認、なければｆｒｏｍＥｎｔｉｔｙ叩いてリターン
		for(RatesEntity ent : resultRatesEntList) {
			if(ent.getRangeId() == null) return form.fromEntity(parkinglotEnt, resultRatesEntList);
		}
		// range データをDao経由取得
		List<RangeEntity> resultRangeEntList = new ArrayList<RangeEntity>();
		for (RatesEntity ratesEnt : resultRatesEntList) {
			RangeEntity rangeEnt = rangeDao.selectById((Integer)ratesEnt.getRangeId()).get(0);
			resultRangeEntList.add(rangeEnt);
		}
		return form.fromEntity(parkinglotEnt, resultRatesEntList, resultRangeEntList);
	}
	
}
