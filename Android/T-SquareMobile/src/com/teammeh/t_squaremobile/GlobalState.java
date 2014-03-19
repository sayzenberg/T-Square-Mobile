package com.teammeh.t_squaremobile;

import android.app.Application;

public class GlobalState extends Application {

	private static String sessionName;
	private static String sessionId;
	
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
	
	
}
