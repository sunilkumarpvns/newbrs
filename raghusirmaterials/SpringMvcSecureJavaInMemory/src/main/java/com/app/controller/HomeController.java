package com.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping("/all")
	public String showAll() {
		return "CommonPage";
	}
	
	@RequestMapping("/admin")
	public String showAdmin() {
		return "AdminPage";
	}
	
	@RequestMapping("/emp")
	public String showEmp() {
		return "EmployeePage";
	}
	@RequestMapping("/view")
	public String showView() {
		return "ViewPage";
	}
	
	@RequestMapping("/denied")
	public String showDenied() {
		return "AccessDenied";
	}
	
	
}



