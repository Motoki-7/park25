package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ParkingDetailController {
	@GetMapping("/Home")
	public String moveToHome() {
		return "redirect:/Home";
	}
}
