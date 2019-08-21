package com.app.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.dao.IStudentDao;
import com.app.model.Student;
import com.app.service.IStudentService;

@Service
public class StudentServiceImpl  implements IStudentService
{
	@Autowired
	private IStudentDao dao;

	@Transactional
	public Integer saveStudent(Student e) {
		return dao.saveStudent(e);
	}

	@Transactional
	public void updateStudent(Student e) {
		dao.updateStudent(e);		
	}

	@Transactional
	public void deleteStudentById(Integer stdId) {
		dao.deleteStudentById(stdId);		
	}

    @Transactional
	public Student getStudentById(Integer stdId) {
		return dao.getStudentById(stdId);

	}

	@Transactional
	public List<Student> getAllStudents() {
		return dao.getAllStudents();

	}
	
}
