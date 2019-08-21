package com.app.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.app.model.Student;
@Component
public class StudentValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return Student.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors)
	{
          Student e=(Student)target;
		
		if(!Pattern.compile("[A-Za-z]{2,6}").matcher(e.getStdName()).matches()) {
			errors.rejectValue("stdName", null, "Enter 2-6 only chars");
		}
		if(e.getStdSal()==null||e.getStdSal()<=0) {
			errors.rejectValue("stdSal",null,"enter valid sal");			
		}
		if(!Pattern.compile("[A-Za-z]{2,6}").matcher(e.getStdPwd()).matches()) 
		{		
			errors.rejectValue("stdPwd",null,"enter valid one");			
		}
		if(e.getStdGen()==null || "".equals(e.getStdGen())) {
			errors.rejectValue("stdGen", null, "Choos one Gender");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "stdAddr", null, "Please Enter Address");
		if(e.getStdLang()==null || e.getStdLang().isEmpty()) {
			errors.rejectValue("stdLang", null, "Choos one Language");
		}
		if(e.getStdCntry()==null || "".equals(e.getStdCntry())){
			errors.rejectValue("stdCntry", null, "Choos one Country");
		}		
	}

}
