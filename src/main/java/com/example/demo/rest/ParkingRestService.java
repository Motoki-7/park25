package com.example.demo.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ParkinglotDao;
import com.example.demo.dao.RangeDao;
import com.example.demo.dao.RatesDao;
import com.example.demo.entity.ParkinglotEntity;
import com.example.demo.entity.RatesEntity;

@Service
public class ParkingRestService {
	
	@Autowired ParkinglotDao parkinglotDao;
	@Autowired RatesDao ratesDao;
	@Autowired RangeDao rangeDao;
	
	//条件検索
	public List<ParkinglotEntity> select(String name, String address1){
		List<ParkinglotEntity> parkinglotList;
		boolean isNameEmpty = (name == null || name.isEmpty());
	    boolean isAddressEmpty = (address1 == null || address1.isEmpty());
		
		//両方空欄の場合に全件表示
		if(isNameEmpty && isAddressEmpty) {
			parkinglotList = parkinglotDao.selectAll();
		}	
		//nameが空欄で、address1が入力された場合　→　address1で検索
		else if(isNameEmpty && !isAddressEmpty) {
			parkinglotList = parkinglotDao.selectByAddress1(address1);
		}
		//address1が空欄で、nameが入力された場合　→　nameで検索
		else if(!isNameEmpty && isAddressEmpty) {
			parkinglotList = parkinglotDao.selectByName(name);
		}
		//両方が入力された場合　→　両方で検索
		else {
			parkinglotList = parkinglotDao.selectByNameAndAddress1(name,address1);
		}
		
		return parkinglotList;
	}	
	
	//削除処理
	public void delete(int parkinglotId) {
		parkinglotDao.delete(parkinglotId);
		// IDリスト取得
		List<RatesEntity> ratesEntList = ratesDao.selectIdListByParkinglotId(parkinglotId);
		// 削除処理
		for (RatesEntity ratesEnt : ratesEntList) {
			if (ratesEnt.getRangeId() != null) {
				rangeDao.delete(ratesEnt.getRangeId());
			}
			ratesDao.delete(ratesEnt.getRatesId());
		}
	}
	
	
}
