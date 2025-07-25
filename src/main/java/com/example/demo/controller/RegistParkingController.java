package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.form.RatesListWrapper;
import com.example.demo.form.RegistParkingForm;
import com.example.demo.service.RegistParkingService;

@Controller
public class RegistParkingController {
	@Autowired
	RegistParkingService service;
	
	
	@PostMapping("/RegistParking/post")
	public String insert(Model model, RegistParkingForm form, RatesListWrapper wrapper) {
		service.insert(form, wrapper);
		return "redirect:/HomeAdmin";
	}
}