package com.scrollwin.server;

public class ServerConfig {
	private String databaseName;
	private String databaseUser;
	private String databasePwd;
	private String sqlServerIp;
	private String sqlServerPort;
	
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getDatabaseUser() {
		return databaseUser;
	}
	public void setDatabaseUser(String databaseUser) {
		this.databaseUser = databaseUser;
	}
	public String getDatabasePwd() {
		return databasePwd;
	}
	public void setDatabasePwd(String databasePwd) {
		this.databasePwd = databasePwd;
	}
	public String getSqlServerIp() {
		return sqlServerIp;
	}
	public void setSqlServerIp(String sqlServerIp) {
		this.sqlServerIp = sqlServerIp;
	}
	public String getSqlServerPort() {
		return sqlServerPort;
	}
	public void setSqlServerPort(String sqlServerPort) {
		this.sqlServerPort = sqlServerPort;
	}
}
