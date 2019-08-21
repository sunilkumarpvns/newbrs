package com.app.service;

import java.util.List;

import com.app.model.Student;

public interface IStudentService
{
	public Integer saveStudent(Student e);
	public void updateStudent(Student e);
	public void deleteStudentById(Integer stdId);
	
	public Student getStudentById(Integer stdId);
	public List<Student> getAllStudents();
}
