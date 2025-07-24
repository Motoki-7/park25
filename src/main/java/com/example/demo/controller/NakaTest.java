package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NakaTest {
	@GetMapping("/nakatest")
	public String nakaTest() {
		return "naka_test";
		
	}
}
