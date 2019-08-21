package com.app.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
		//1. create one reg exp
		Pattern p=Pattern.compile("[A-Z]{3,8}");
		String inp="ABC";
		//2. compare input with reg exp
		Matcher m=p.matcher(inp);
		//3. check /execute
		boolean b=m.matches();
		System.out.println(b);
	}
}




