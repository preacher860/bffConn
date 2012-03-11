package com.scrollwin.client;

public class UserContainer {

	private Integer myId;
	private String  myNick;
	private String  myName;
	private String  myAvatarURL;
	private boolean myOnlineStatus;
	private int 	myMessages;
	private int 	myDeletedMessages;
	private int		myEditedMessages;
	private int 	myStarsSent;
	private int		myStarsRcvd;
	
	public UserContainer(Integer Id, String Nick, String Name, String AvatarURL, boolean online, int messages, int deleted, int edited, int starsSent, int starsRcvd)
	{
		myId = Id;
		myNick = Nick;
		myName = Name;
		myAvatarURL = AvatarURL;
		myOnlineStatus = online;
		myMessages = messages;
		myDeletedMessages = deleted;
		myEditedMessages = edited;
		myStarsSent = starsSent;
		myStarsRcvd = starsRcvd;
	}
	
	public UserContainer(UserContainer user)
	{
		myId = user.getId();
		myNick = user.getNick();
		myName = user.getName();
		myAvatarURL = user.getAvatarURL();
		myOnlineStatus = user.getOnlineStatus();
		myMessages = user.getMessages();
		myDeletedMessages = user.getDeletedMessages();
		myEditedMessages = user.getEditedMessages();
		myStarsSent = user.getStarsSent();
		myStarsRcvd = user.getStarsRcvd();
	}
	
	public boolean equals(UserContainer user)
	{
		// Equality excludes statistics. Not so nice but so much simpler...
		if(myId.equals(user.getId()) &&
		   (myNick.compareTo(user.getNick()) == 0) &&
		   (myName.compareTo(user.getName()) == 0) &&
		   (myAvatarURL.compareTo(user.getAvatarURL()) == 0) &&
		    myOnlineStatus == user.getOnlineStatus())
			return true;
		
		return false;
	}
	
	public Integer getId() {
		return myId;
	}
	public void setId(Integer id) {
		myId = id;
	}
	public String getNick() {
		return myNick;
	}
	public void setNick(String nick) {
		myNick = nick;
	}
	public String getName() {
		return myName;
	}
	public void setName(String name) {
		myName = name;
	}
	public String getAvatarURL() {
		return myAvatarURL;
	}
	public void setAvatarURL(String avatarURL) {
		myAvatarURL = avatarURL;
	}
	
	public boolean getOnlineStatus() {
		return myOnlineStatus;
	}
	
	public void setOnlineStatus(boolean status) {
		myOnlineStatus = status;
	}

	public int getMessages() {
		return myMessages;
	}

	public void setMessages(int myMessages) {
		this.myMessages = myMessages;
	}

	public int getDeletedMessages() {
		return myDeletedMessages;
	}

	public void setDeletedMessages(int myDeletedMessages) {
		this.myDeletedMessages = myDeletedMessages;
	}

	public int getEditedMessages() {
		return myEditedMessages;
	}

	public void setEditedMessages(int myEditedMessages) {
		this.myEditedMessages = myEditedMessages;
	}
	
	public int getStarsSent() {
		return myStarsSent;
	}

	public int getStarsRcvd() {
		return myStarsRcvd;
	}

	public void setStarsSent(int myStarsSent) {
		this.myStarsSent = myStarsSent;
	}

	public void setStarsRcvd(int myStarsRcvd) {
		this.myStarsRcvd = myStarsRcvd;
	}
}
