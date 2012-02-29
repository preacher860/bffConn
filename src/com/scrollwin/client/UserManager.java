package com.scrollwin.client;

import java.util.ArrayList;

public class UserManager {
	
	private ArrayList<UserContainer> myUserList = new ArrayList<UserContainer>();
	private boolean userListReceived = false;
	private int myDbVersion = 0;
	
	public void setUser(UserContainer user){
		// Scan the list to see if we're adding or editing a user
		for(UserContainer currentUser:myUserList){
			if(currentUser.getId() == user.getId()){
				currentUser = user;
				return;
			}
		}
		// User not found, add it
		myUserList.add(user);
	}
	
	public UserContainer getUser(Integer userId){
		for(UserContainer currentUser:myUserList)
			if(currentUser.getId().intValue() == userId.intValue()) 
				return currentUser;

		return new UserContainer(0, "dummy", "dummy", "dummy", false);
	}
	
	public void setUserList(ArrayList<UserContainer> userList){
		// Check if there is any change from what we had before
		if(userList.size() != myUserList.size())
			myDbVersion++;
		else
			for(int userIndex = 0; userIndex < userList.size(); userIndex++)
				if(myUserList.get(userIndex).equals(userList.get(userIndex)) == false) {
					myDbVersion++;
					break;
				}
		myUserList = userList;		
		userListReceived = true;
	}
	
	// Make this nicer eventually so we can iterate through users without 
	// directly retrieving the internal list object
	public ArrayList<UserContainer> getUserList(){
		return myUserList;
	}
	
	public boolean getUserListReceived() {
		return userListReceived;
	}
	
	public ArrayList<UserContainer> getOnlineUsers()
	{
		ArrayList<UserContainer> onlineUsersList = new ArrayList<UserContainer>();
		
		for(UserContainer currentUser:myUserList)
			if(currentUser.getOnlineStatus() == true) 
				onlineUsersList.add(currentUser);
		return onlineUsersList;
	}
	
	public int getDbVersion() {
		return myDbVersion;
	}

	public void setDbVersion(int dbVersion) {
		this.myDbVersion = myDbVersion;
	}
}
