package com.teammeh.t_squaremobile;

import org.json.JSONObject;

public class Course implements java.io.Serializable {

	public String className;
	public String classId;
	public boolean active;
	
	public Course(String className, String classId, boolean active) {
		this.className = className;
		this.classId = classId;
		this.active = active;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	
	
}
