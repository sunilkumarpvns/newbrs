package com.app.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.app.dao.IStudentDao;
import com.app.model.Student;
@Repository
public class StudentDaoImpl implements IStudentDao
{
	@Autowired
	private SessionFactory sf;
	@Override
	public Integer saveStudent(Student e) {
		return (Integer)sf.getCurrentSession().save(e);
	}

	@Override
	public void updateStudent(Student e) {
		sf.getCurrentSession().update(e);
	}

	@Override
	public void deleteStudentById(Integer stdId) {
		sf.getCurrentSession().delete(new Student(stdId));
	}

	@Override
	public Student getStudentById(Integer stdId) {
		return sf.getCurrentSession().get(Student.class, stdId);

	}

	@Override
	public List<Student> getAllStudents() {
		return sf.getCurrentSession().createQuery("from "+Student.class.getName()).list();

	}
	
}
