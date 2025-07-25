package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ParkinglotDao;
import com.example.demo.entity.ParkinglotEntity;
import com.example.demo.form.RegistParkingForm;

@Service
public class RegistParkingService {
	@Autowired
	ParkinglotDao dao;
	
	public void insert(RegistParkingForm form) {
		ParkinglotEntity ent = form.toParkinglotEntity();
		dao.insert(ent);
	}
}
