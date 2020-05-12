//package main.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Movie {

	private String id;

	private String title;

	private int year;

	private String director;

	private ArrayList<String> genres;

	private ArrayList<String> stars;

	public Movie(){
		genres = new ArrayList<String>();
		stars = new ArrayList<String>();
	}

	public Movie(String id, String title, int year, String director) {
		this.id = id;
		this.title = title;
		this.year = year;
		this.director = director;

		genres = new ArrayList<String>();
		stars = new ArrayList<String>();
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getTitle() { return title; }

	public void setTitle(String title) { this.title = title; }

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public ArrayList<String> getGenres() {
		return (ArrayList<String>) genres;
	}

	public ArrayList<String> getStars() { return (ArrayList<String>) stars; }

	public void addGenres(String genre) {
		if(!genres.contains(genre))
		{ genres.add(genre);}
	}

	public void addStars(String star ) {
		if(!stars.contains(star)){
			stars.add(star); }
	}
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Movie Details - ");
		sb.append("ID:" + getId());
		sb.append(", ");
		sb.append("Year:" + getYear());
		sb.append(", ");
		sb.append("Title:" + getTitle());
		sb.append(", ");
		sb.append("Director:" + getDirector());
		sb.append(", ");
		sb.append("Genres:" + getGenres());
		sb.append(", ");
		sb.append("Stars:" + getStars());
		sb.append(".");
		
		return sb.toString();
	}
}
