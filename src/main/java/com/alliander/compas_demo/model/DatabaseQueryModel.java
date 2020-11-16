package com.alliander.compas_demo.model;

public class DatabaseQueryModel {

	private String query;
	private String command;
	private String databaseResponse;

	public String getQuery() {
	  	return query;
	}
  
	public void setQuery(String query) {
	  	this.query = query;
	}
  
	public String getDatabaseResponse() {
	  	return databaseResponse;
	}
  
	public void setDatabaseResponse(String databaseResponse) {
	 	this.databaseResponse = databaseResponse;
	}
	
	public void setCommand(String command) {
		this.command = command;
	}
  
	public String getCommand() {
		return command;
	}
}
