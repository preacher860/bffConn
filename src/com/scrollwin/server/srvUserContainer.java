package com.scrollwin.server;

public class srvUserContainer {
	private Integer id;
	private String  myNick;
	private String  myName;
	private String  myAvatarURL;
	private String  myPasswordHash;
	private int 	activityTimeout;
	private int 	myNumOfMessages;
	private int 	myNumOfDeletedMessages;
	
	public srvUserContainer() {
		id = 0;
		activityTimeout = 0;
		myNick = "dummy";
		myName = "dummy";
		myPasswordHash = "";
		myAvatarURL = "dummy";
	}
	
	public srvUserContainer(Integer Id, int ActivityTimeout) {
		id = Id;
		activityTimeout = ActivityTimeout;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getActivityTimeout() {
		return activityTimeout;
	}

	public void setActivityTimeout(int activityTimeout) {
		this.activityTimeout = activityTimeout;
	}

	public String getNick() {
		return myNick;
	}

	public void setNick(String myNick) {
		this.myNick = myNick;
	}

	public String getName() {
		return myName;
	}

	public void setName(String myName) {
		this.myName = myName;
	}

	public String getAvatarURL() {
		return myAvatarURL;
	}

	public void setAvatarURL(String myAvatarURL) {
		this.myAvatarURL = myAvatarURL;
	}
	
	public boolean getActiveStatus() 
	{
		if (activityTimeout > 0)
			return true;
		else
			return false;
	}

	public String getPasswordHash() {
		return myPasswordHash;
	}

	public void setPasswordHash(String myPasswordHash) {
		this.myPasswordHash = myPasswordHash;
	}

	public int getNumOfMessages() {
		return myNumOfMessages;
	}

	public void setNumOfMessages(int myNumOfMessages) {
		this.myNumOfMessages = myNumOfMessages;
	}

	public int getNumOfDeletedMessages() {
		return myNumOfDeletedMessages;
	}

	public void setNumOfDeletedMessages(int myNumOfDeletedMessages) {
		this.myNumOfDeletedMessages = myNumOfDeletedMessages;
	}
	
}
