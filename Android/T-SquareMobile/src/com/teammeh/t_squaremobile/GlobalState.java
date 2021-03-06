package com.teammeh.t_squaremobile;

import java.util.ArrayList;

import android.app.Application;
import android.content.SharedPreferences;

public class GlobalState extends Application {

	private static String sessionName;
	private static String sessionId;
	private static ArrayList<Course> classes;
	private static ArrayList<Course> activeClasses;
	private static String userId;
	
	public static String getSessionName() {
		return sessionName;
	}
	public static void setSessionName(String sessionName) {
		GlobalState.sessionName = sessionName;
	}
	public static String getSessionId() {
		return sessionId;
	}
	public static void setSessionId(String sessionId) {
		GlobalState.sessionId = sessionId;
	}
	public static ArrayList<Course> getClasses() {
		return classes;
	}
	public static void setClasses(ArrayList<Course> classes) {
		GlobalState.classes = classes;
		GlobalState.activeClasses = new ArrayList<Course>();
		for(Course course : classes) {
			if(course.active) activeClasses.add(course);
		}
	}
	public static ArrayList<Course> getActiveClasses() {
		return activeClasses;
	}
	public static void setActiveClasses(ArrayList<Course> activeClasses) {
		GlobalState.activeClasses = activeClasses;
	}
	public static String getUserId() {
		return userId;
	}
	public static void setUserId(String userId) {
		GlobalState.userId = userId;
	}
	
	
	
	
	
}
