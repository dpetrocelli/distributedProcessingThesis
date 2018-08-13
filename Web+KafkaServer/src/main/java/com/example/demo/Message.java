package com.example.demo;

public class Message {
	String name;
	int part;
	int qParts;
	byte[] data;
	String paramsEncoding;
	
	
	
	
	public Message(String name, int part, int qParts, byte[] data, String paramsEncoding) {
		super();
		this.name = name;
		this.part = part;
		this.qParts = qParts;
		this.data = data;
		this.paramsEncoding = paramsEncoding;
		
	}
	
	

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPart() {
		return part;
	}
	public void setPart(int part) {
		this.part = part;
	}
	public int getqParts() {
		return qParts;
	}
	public void setqParts(int qParts) {
		this.qParts = qParts;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getParamsEncoding() {
		return paramsEncoding;
	}
	public void setParamsEncoding(String paramsEncoding) {
		this.paramsEncoding = paramsEncoding;
	}
	
	

}
