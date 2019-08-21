package com.app.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.app.model.Employee;

@Component
public class EmployeeValidator implements Validator{

	@Override
	public boolean supports(Class<?> cls) {
		return Employee.class.equals(cls);
	}

	@Override
	public void validate(Object target, Errors errors) {
		//object -> Model class
		
		//ValidationUtils.rejectIfEmptyOrWhitespace(errors, "empName", null, "Name can not be empty!!");
		
		//new way of errors check
		Employee e=(Employee)target;
		
		if(!Pattern.compile("[A-Za-z]{2,6}").matcher(e.getEmpName()).matches()) {
			//errors.rejectValue("empName", null, "Enter 2-6 only chars");
			errors.rejectValue("empName", "empNameError");
		}
		
		if(e.getEmpSal()==null || e.getEmpSal() <=0) {
			//errors.rejectValue("empSal", null, "Please enter valid salary");
			errors.rejectValue("empSal", "empSalError");
		}
		
		if(!Pattern.compile("[A-Za-z0-9]{3,6}").matcher(e.getEmpPwd()).matches()) {
			errors.rejectValue("empPwd", null, "Please enter valid Password");
		}
		
		if(e.getEmpGen()==null || "".equals(e.getEmpGen())) {
			errors.rejectValue("empGen", null, "Choos one Gender");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "empAddr", null, "Please Enter Address");
		if(e.getEmpLang()==null || e.getEmpLang().isEmpty()) {
			errors.rejectValue("empLang", null, "Choos one Language");
		}
		if(e.getEmpCntry()==null || "".equals(e.getEmpCntry())){
			errors.rejectValue("empCntry", null, "Choos one Country");
		}
		
	}
	
}
