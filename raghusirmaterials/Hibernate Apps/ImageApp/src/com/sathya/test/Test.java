package com.sathya.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Blob;

import org.hibernate.Hibernate;
import org.hibernate.engine.jdbc.LobCreator;

import com.sathya.dao.EmpDaoFactory;
import com.sathya.dao.IEmpDao;
import com.sathya.entity.Employee;
import com.sathya.util.HibernateUtil;

public class Test {
	public static void main(String[] args)throws Exception {
		IEmpDao  dao = EmpDaoFactory.getInstance();
		
		Employee  e =new  Employee();
		e.setEmpno(1234);
		
		//read  binary data from  image file
		FileInputStream  fis =new  FileInputStream("D:\\Gosling.jpg");
		byte[ ]   bytes=new  byte[fis.available()];
		fis.read(bytes);
		fis.close();
		//convert  binary data  to  Blob  object
		LobCreator  creator=Hibernate.getLobCreator(HibernateUtil.getSessionFactory().openSession());
		Blob  blob =creator.createBlob(bytes);
		
		//set  Blob object  to  pojo object
		e.setImage(blob);
		
		//call  saveEmp()
		dao.saveEmp(e);
		
		
		//call  readEmp()
		Employee  e1 = dao.readEmp(1234);
		
		Blob  blb = e1.getImage();
		
		int length=(int)blb.length();
		byte[ ]  b = blb.getBytes(1, length);
		
		FileOutputStream  fos = new  FileOutputStream("F:\\gosling.jpg");
		fos.write(b);
		
		fos.close();

	}

}
