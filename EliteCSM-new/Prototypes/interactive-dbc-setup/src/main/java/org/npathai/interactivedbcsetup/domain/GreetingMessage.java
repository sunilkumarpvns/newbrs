package org.npathai.interactivedbcsetup.domain;

public class GreetingMessage {

	private String content;
	private int count;
	
	public GreetingMessage() {
	}

	public GreetingMessage(String content) {
		this.content = content;
	}
	
	public GreetingMessage(String content, int count) {
		this.content = content;
		this.count = count;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
