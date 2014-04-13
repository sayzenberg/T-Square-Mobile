package com.teammeh.t_squaremobile;

public class Items {
	 private String title;
	 private String description;
	 private String date;
	 
	    public Items(String title, String description, int day, int month, int year) {
	        super();
	        this.title = title;
	        this.description = description;
	        String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
	        this.date = "Due Date: " + months[month] + " " + day + ", " + year;
	    }
	    
	    public Items(String title, String description) {
	        super();
	        this.title = title;
	        this.description = description;
	    }
	    
	    public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
		
		public String getDescription() {
			return description;
		}

		public void setDescription(String decription) {
			this.description = title;
		}
		
		public String getDate() {
			return date;
		}
		
		public void setDate(int day, int month, int year) {
			String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
	        this.date = "Due Date: " + months[month] + " " + day + ", " + year;
		}
}

