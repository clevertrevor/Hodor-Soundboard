package com.blogspot.foreapps.hodorsoundboard;

/**
 * Class that keeps track of each song file for MainActivity
 * content: file location, name 
 *
 */
public class Song {
	
	private String name;
	
	public Song(String name){
		this.name = name;
	}
	
	/*
	 * Gets the name of the song
	 */
	public String getName(){
		return name;
	}
	

}
