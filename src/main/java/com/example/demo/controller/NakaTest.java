package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NakaTest {
	@PostMapping("/nakatest")
	public String nakaTest() {
		return "naka_test";
		
	}
	@PostMapping("/nakatest2")
	public String nakaTest2() {
		return "naka_test2";
		
	}
}
