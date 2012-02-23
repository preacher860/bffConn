package com.scrollwin.client;
import java.util.ArrayList;

import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.tile.TileRecord;
import com.smartgwt.client.widgets.viewer.DetailViewerField;


public class UserTileDisplay extends TileGrid {
	private ArrayList<UserContainer> knownUsers = new ArrayList<UserContainer>();
	
	public UserTileDisplay(){
		setTileWidth(48);
		setTileHeight(60);
		setHeight(200);
		setWidth(180);
		setMargin(5);
		setShowAllRecords(true);
		setAutoFetchData(false);
		//setAutoHeight();
		setOverflow(Overflow.VISIBLE);
		setTileMargin(2);

		DetailViewerField pictureField = new DetailViewerField("picture");
		pictureField.setType("image");
		pictureField.setImageURLPrefix("");
		pictureField.setImageWidth(40);
		pictureField.setImageHeight(40);


		DetailViewerField nameField = new DetailViewerField("name");

		setFields(pictureField, nameField);
	}
	
	public void UpdateOnlineUsers(ArrayList<UserContainer> users){
		// Eventually detect if the users have changed to minimize refreshes
		// (and thus flickers) to the tilegrid
		//invalidateCache();
		setData(UserData.getNewRecords(users));
		//knownUsers = cloneList(users);
	}
	
	private ArrayList<UserContainer> cloneList(ArrayList<UserContainer> users) {
	    ArrayList<UserContainer> clonedList = new ArrayList<UserContainer>(users.size());
	    for (UserContainer currentUser : users) {
	        clonedList.add(new UserContainer(currentUser));
	    }
	    return clonedList;
	}
}

class UserData {
	public static UserRecord[] getNewRecords(ArrayList<UserContainer> users) {
		int numOfOnline = 0;
		int recIndex = 0;
		
		// This sucks, but the records table must have the right size so we need to know
		// how many guys online before populating.  User manager will make this nicer eventually
		for(UserContainer currentUser:users) 
			if(currentUser.getOnlineStatus())
				numOfOnline++;
		
		UserRecord [] records = new UserRecord[numOfOnline];
		for(UserContainer currentUser:users) 
			if(currentUser.getOnlineStatus()){
				records[recIndex] = new UserRecord(currentUser.getNick(), currentUser.getAvatarURL());
				recIndex++;
			}
		
		return records;
	}
}

class UserRecord extends TileRecord {

	public UserRecord() {
	}

	public UserRecord(String name, String picture) {
		setName(name);
		setPicture(picture);
	}

	public void setName(String name) {
		setAttribute("name", name);
	}
	
	public void setPicture(String picture) {
		setAttribute("picture", picture);
	}
}
