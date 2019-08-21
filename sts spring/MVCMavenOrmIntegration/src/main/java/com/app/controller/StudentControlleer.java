package com.app.controller;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.model.Student;
import com.app.service.IStudentService;
import com.app.validator.StudentValidator;


@Controller
public class StudentControlleer 
{
	@Autowired
	private MessageSource ms;
	
	@Autowired
	private IStudentService service;
	@Autowired
	private StudentValidator validator;
	
	@GetMapping("/reg")
	public String showReg(ModelMap map) {
		map.addAttribute("student", new Student());
		return "StudentRegister";
	}

	//2. on click submit insert data to DB
	@PostMapping("/insert")
	public String saveEmp(@ModelAttribute Student student,Errors errors,ModelMap map,Locale locale) {
		validator.validate(student,errors);
		if(errors.hasErrors())
				{			
			map.addAttribute("student",student);
				}
		else 
		{
		Integer stdId=service.saveStudent(student);
		String msg=ms.getMessage("stdSuccess", new Object[] {stdId}, locale);
		map.addAttribute("message", msg);
		map.addAttribute("student",new Student());
		}
		return "StudentRegister";
	}
	

	//3. show Data at UI
	@GetMapping("/all")
	public String showData(ModelMap map) {
		List<Student> emps=service.getAllStudents();
		map.addAttribute("emps",emps);
		return "StudentData";
	}
	
	//4. delete employee By Id
	@GetMapping("/delete")
	public String delete(@RequestParam("stdId")Integer stdId,ModelMap map) {
		System.out.println("111111111111111");
		service.deleteStudentById(stdId);
		List<Student> emps=service.getAllStudents();
		map.addAttribute("emps",emps);
		map.addAttribute("message", "Student '"+stdId+"' Deleted");
		return "StudentData";
	}
}
