package com.teammeh.t_squaremobile;

public class Course {

	public String className;
	public String classId;
	
	public Course(String className, String classId) {
		this.className = className;
		this.classId = classId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}
	
}
