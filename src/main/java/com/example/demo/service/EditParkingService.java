package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ParkinglotDao;
import com.example.demo.entity.ParkinglotEntity;
import com.example.demo.form.EditParkingForm;

@Service
public class EditParkingService {
	@Autowired
	ParkinglotDao dao;
	
	
	public void update(EditParkingForm form) {
		ParkinglotEntity ent = form.toEntity();
		dao.update(ent);
	}
}