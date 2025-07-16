package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.form.EditParkingForm;
import com.example.demo.service.EditParkingService;

@Controller
public class EditParkingController {
	@Autowired
	EditParkingService service;
	

//	編集の確認とデータの更新
	//	ソースは仮
	@PostMapping("/EditParking/put")
	public String update(Model model, EditParkingForm form) {
		service.update(form);
		return "/HomeAdmin";
	}
}