package com.scrollwin.client;

public class UserContainer {

	private Integer myId;
	private String  myNick;
	private String  myName;
	private String  myAvatarURL;
	private boolean myOnlineStatus;
	
	public UserContainer(Integer Id, String Nick, String Name, String AvatarURL, boolean online)
	{
		myId = Id;
		myNick = Nick;
		myName = Name;
		myAvatarURL = AvatarURL;
		myOnlineStatus = online;
	}
	
	public UserContainer(UserContainer user)
	{
		myId = user.getId();
		myNick = user.getNick();
		myName = user.getName();
		myAvatarURL = user.getAvatarURL();
		myOnlineStatus = user.getOnlineStatus();
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
}
