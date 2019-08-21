package com.app.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test {

	public static void main(String[] args) {
		BCryptPasswordEncoder enc=new BCryptPasswordEncoder();
		String str=enc.encode("sam");
		System.out.println(str);
		
		
	}
}
