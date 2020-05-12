//package main.java;

public class Star {

	private String name;

	private int dob;

	//private int id;


	public Star(){

	}

	public Star(String name, int dob) {
		this.name = name;
		this.dob = dob;
		
	}
	public int getDob() {
		return dob;
	}

	public void setDob(int dob) {
		this.dob = dob;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Star Details - ");
		sb.append("Name:" + getName());
		sb.append(", ");
		sb.append("Birth Year:" + getDob());
		sb.append(".");
		
		return sb.toString();
	}
}
