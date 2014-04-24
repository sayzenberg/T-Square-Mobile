package com.teammeh.t_squaremobile;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Assignment {
	String assignmentId;
	String title;
	String openDate;
	String openDateEpoch;
	String dueDate;
	String dueDateEpoch;
	String instructions;

	public Assignment(String assignmentId, String title, String openDate, 
			String openDateEpoch, String dueDate, String dueDateEpoch, String instructions) {
		this.assignmentId = assignmentId;
		this.title = title;
		this.openDate = openDate;
		this.openDateEpoch = openDateEpoch;
		this.dueDate = dueDate;
		this.dueDateEpoch = dueDateEpoch;
		Document doc = Jsoup.parseBodyFragment(instructions);
		Element bodyElement = doc.body();
		this.instructions = bodyElement.text();

	}

	public Assignment(JSONObject obj) throws JSONException {
		this.assignmentId = obj.optString("assignmentId");
		this.title = obj.optString("title");
		if(obj.optJSONObject("openDate") != null) {
			this.openDate = obj.getJSONObject("openDate").optString("display");
			this.openDateEpoch = obj.getJSONObject("openDate").optString("time");
		}
		if(obj.optJSONObject("dueDate") != null) {
			this.dueDate = obj.getJSONObject("dueDate").optString("display");
			this.dueDateEpoch = obj.getJSONObject("dueDate").optString("time");
		}
		this.instructions = obj.optString("instructions");
		if(this.instructions != "") {
			Document doc = Jsoup.parseBodyFragment(instructions);
			Element bodyElement = doc.body();
			this.instructions = bodyElement.text();
		}

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

	public String getOpenDateEpoch() {
		return openDateEpoch;
	}

	public void setOpenDateEpoch(String openDateEpoch) {
		this.openDateEpoch = openDateEpoch;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getDueDateEpoch() {
		return dueDateEpoch;
	}

	public void setDueDateEpoch(String dueDateEpoch) {
		this.dueDateEpoch = dueDateEpoch;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	




}
