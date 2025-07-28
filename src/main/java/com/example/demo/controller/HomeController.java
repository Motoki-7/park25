package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.form.ParkingDetailForm;
import com.example.demo.service.HomeService;

@Controller
public class HomeController {
	
	@Autowired
	HomeService homeService;
	
	//ユーザーホーム画面
	@GetMapping("/")
	public String showHome(Model model) {
		return "Home";
		
	}
	
	//詳細画面に遷移
	@GetMapping("/ParkingDetail/{id}")
	public String moveToParkingDetail(Model model,@PathVariable int id) {
		ParkingDetailForm form = homeService.selectById(id);
		System.out.println("form情報：" + form);
		model.addAttribute("form",form);
		return "ParkingDetail";
	}
	
}
