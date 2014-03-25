package com.teammeh.t_squaremobile;

import org.json.JSONException;
import org.json.JSONObject;

public class Assignment {
	String assignmentId;
	String title;
	String openDate;
	String dueDate;
	String instructions;

	public Assignment(String assignmentId, String title, String openDate,
			String dueDate, String instructions) {
		this.assignmentId = assignmentId;
		this.title = title;
		this.openDate = openDate;
		this.dueDate = dueDate;
		this.instructions = instructions;
	}

	public Assignment(JSONObject obj) throws JSONException {
		this.assignmentId = obj.optString("assignmentId");
		this.title = obj.optString("title");
		if(obj.optJSONObject("openDate") != null) {
			this.openDate = obj.getJSONObject("openDate").optString("display");
		}
		if(obj.optJSONObject("dueDate") != null) {
			this.dueDate = obj.getJSONObject("dueDate").optString("display");
		}
		this.instructions = obj.optString("instructions");

	}

	public String getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(String assignmentId) {
		this.assignmentId = assignmentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOpenDate() {
		return openDate;
	}

	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}




}
