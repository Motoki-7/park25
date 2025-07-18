package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.form.EditParkingForm;
import com.example.demo.form.RegistParkingForm;
import com.example.demo.service.HomeAdminService;


@Controller
public class HomeAdminController {
	
	@Autowired
	HomeAdminService homeAdminService;
	
	
	//管理者ホーム画面
	@GetMapping("/HomeAdmin")
	public String showHomeAdmin(Model model) {
		
//		homeAdminService.selectAll();
		
		return "HomeAdmin";
	}
	
	//RegistParking.htmlに遷移
	@GetMapping("/RegistParking")
	public String moveToRegistParking(Model model) {
		model.addAttribute("form", new RegistParkingForm());
		return "RegistParking";
	}
	
	
	//EditParking.htmlに遷移
	@GetMapping("/EditParking/{id}")
	public String moveToEditParking(Model model, @PathVariable int id) {
		EditParkingForm form = EditParkingForm.fromEntity(homeAdminService.selectById(id).get(0));
		model.addAttribute("form", form);
		return "EditParking";
	}
}
