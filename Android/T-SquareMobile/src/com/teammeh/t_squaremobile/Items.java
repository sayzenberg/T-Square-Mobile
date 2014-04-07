package com.teammeh.t_squaremobile;

public class Items {
	 private String title;
	 private String description;
	 
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
}
