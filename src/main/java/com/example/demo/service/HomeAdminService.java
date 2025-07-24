package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ParkinglotDao;
import com.example.demo.entity.ParkinglotEntity;

@Service
public class HomeAdminService {
	
	@Autowired
	ParkinglotDao parkinglotDao;
	
	//全件表示
	public List<ParkinglotEntity>selectAll(){
		
		List<ParkinglotEntity>parkinglotList = parkinglotDao.selectAll();
		
		return parkinglotList;
	}
	
	//詳細表示
	public List<ParkinglotEntity>selectById(int id){
		
		List<ParkinglotEntity>parkinglotList = parkinglotDao.selectById(id);
		
		return parkinglotList;
	}
}
