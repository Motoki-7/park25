package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.service.HomeAdminService;

import ch.qos.logback.core.model.Model;

@Controller
public class HomeAdminController {
	
	@Autowired
	HomeAdminService homeAdminService;
	
	
	//管理者ホーム画面
	@GetMapping("/HomeAdmin")
	public String showHomeAdmin(Model model) {
		
		homeAdminService.selectAll();
		
		return "HomeAdmin";
	}
	
	//RegistParking.htmlに遷移
	@GetMapping("")
	public String moveToRegistParking(Model model) {
		return "RegistParking";
	}
	
	
	//EditParking.htmlに遷移
	@GetMapping("/EditParking/{id}")
	public String moveToEditParking(Model model, @PathVariable int id) {
		homeAdminService.selectOne(id);
		
		return "EditParking";
	}
	
	//削除
//	@RequestMapping("/del/{id}")
//	public String
//	
}
