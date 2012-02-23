package com.scrollwin.client;

import java.util.ArrayList;

public class UserManager {
	
	private ArrayList<UserContainer> myUserList = new ArrayList<UserContainer>();
	private boolean userListReceived = false;
	
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
}
