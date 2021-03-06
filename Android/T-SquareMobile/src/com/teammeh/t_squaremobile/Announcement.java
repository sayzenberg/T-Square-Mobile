package com.teammeh.t_squaremobile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Announcement {
	String id;
	String body;
	String title;
	String author;
	String date;
	
	public Announcement(String id, String body, String title, String author, String date) {
		this.id = id;
//		this.body = body.replaceAll("\\<.*?>","");
		Document doc = Jsoup.parseBodyFragment(body);
		Element bodyElement = doc.body();
		this.body = bodyElement.text();
		this.title = title;
		this.author = author;
		this.date = date;
	}
	
	public Announcement(JSONObject obj) {
		this.id = obj.optString("id");
		this.body = obj.optString("body");
		if(body != "") {
			Document doc = Jsoup.parseBodyFragment(body);
			Element bodyElement = doc.body();
			this.body = bodyElement.text();
		}
		this.title = obj.optString("title");
		this.author = obj.optString("createdByDisplayName");
		SimpleDateFormat formatter = new SimpleDateFormat("LLLL dd, yyyy h:mm a");
		formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
		String epoch = obj.optString("createdOn");
		if(epoch != "") {
			Long epochNum = Long.parseLong(epoch);
			Date epochDate = new Date(epochNum);
			this.date = formatter.format(epochDate);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	
	
	
}
