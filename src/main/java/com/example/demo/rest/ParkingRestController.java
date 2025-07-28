package com.example.demo.rest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
	
	
	// ホーム画面：「検索」　name，address1 による(部分あり)検索処理
	@GetMapping("/rest")
	public List<ParkinglotEntity> select(
			@RequestParam(name="name",required=false) String name,
			@RequestParam(name="address1",required=false) String address1 ){
		return parkingRestService.select(name, address1);
	}
	
	// Adminホーム画面：「削除」parkinglotIdをキーに関連データも削除
	@DeleteMapping("/rest")
	public void delete(@RequestBody EditParkingForm form) {
		parkingRestService.delete(form.getId());
	}
	
	// 詳細画面：料金シミュレーション
	@GetMapping("/aaa")
	public List<String> calculation (
			@RequestParam("id") int id,
			@RequestParam("entryDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate entryDate,
			@RequestParam("exitDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate exitDate,
		    @RequestParam("entryTime") @DateTimeFormat(pattern = "HH:mm") LocalTime entryTime,
		    @RequestParam("exitTime") @DateTimeFormat(pattern = "HH:mm") LocalTime exitTime
	){
		List<String> res = new ArrayList<>();
		
		return res;
	}
}
