package com.bezkoder.spring.files.upload.db.controller;

public class Student {
	
	private String name;
	private String clas;
	private int mark;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClas() {
		return clas;
	}
	public void setClas(String clas) {
		this.clas = clas;
	}
	public int getMark() {
		return mark;
	}
	public void setMark(int mark) {
		this.mark = mark;
	}
	@Override
	public String toString() {
		return "Student [name=" + name + ", clas=" + clas + ", mark=" + mark + "]";
	}

}
