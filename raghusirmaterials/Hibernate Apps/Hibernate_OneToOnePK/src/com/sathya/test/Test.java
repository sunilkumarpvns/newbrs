package com.sathya.test;

import com.sathya.dao.OTODao;
import com.sathya.dao.OTODaoImpl;

public class Test {

	public static void main(String[] args) {
		OTODao  dao =new  OTODaoImpl();
		dao.savePassportWithPerson();
	}
}
