package com.example.demo.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.ParkinglotEntity;
import com.example.demo.form.EditParkingForm;

@RestController
public class ParkingRestController {
	
	@Autowired
	ParkingRestService parkingRestService;
	
	
	@GetMapping("/rest")
	public List<ParkinglotEntity> select(
			@RequestParam(name="name",required=false) String name,
			@RequestParam(name="address1",required=false) String address1 ){
		return parkingRestService.select(name, address1);
	}
	
	// 削除：parkinglotIdをキーに関連データも削除
	@DeleteMapping("/rest")
	public void delete(@RequestBody EditParkingForm form) {
		parkingRestService.delete(form.getId());
	}
}
