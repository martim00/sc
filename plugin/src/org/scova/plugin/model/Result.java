package org.scova.plugin.model;

public class Result {
	private String name;

	public Result(String testName) {
		name = testName;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
