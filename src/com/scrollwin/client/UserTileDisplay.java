package com.scrollwin.client;
import java.util.ArrayList;

import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.tile.TileRecord;
import com.smartgwt.client.widgets.tile.events.RecordClickEvent;
import com.smartgwt.client.widgets.tile.events.RecordClickHandler;
import com.smartgwt.client.widgets.viewer.DetailViewerField;


public class UserTileDisplay extends TileGrid {
	private ArrayList<UserContainer> knownUsers = new ArrayList<UserContainer>();
	private userCallbackInterface myUserCallbackInterface;
	int myUserDbVersion = 0;
	
	public UserTileDisplay(userCallbackInterface callbackInterface){
		myUserCallbackInterface = callbackInterface;
		
		setTileWidth(60);
		setTileHeight(62);
		setHeight(200);
		setWidth(216);
		setMargin(5);
		setTilesPerLine(3);
		setShowAllRecords(true);
		setAutoFetchData(false);
		setOverflow(Overflow.VISIBLE);
		setAnimateTileChange(true);
		setTileMargin(2);
		
		DetailViewerField pictureField = new DetailViewerField("picture");
		pictureField.setType("image");
		pictureField.setImageURLPrefix("");
		pictureField.setImageWidth(40);
		pictureField.setImageHeight(40);
		//pictureField.setCellStyle("userPicTile");
		
		DetailViewerField nameField = new DetailViewerField("name");
		//nameField.setCellStyle("userTile");

		setFields(pictureField, nameField);
		
		addRecordClickHandler(new RecordClickHandler() {  
			@Override
			public void onRecordClick(RecordClickEvent event) {
				myUserCallbackInterface.avatarClicked(event.getRecord().getAttribute("name"));
				deselectAllRecords();
			}  
        }); 
	}
	
	public void UpdateOnlineUsers(UserManager userManager){
		if(userManager.getDbVersion() != myUserDbVersion)
		{
			if(userManager.getOnlineUsers() != null)
				setData(UserData.getNewRecords(userManager.getOnlineUsers()));
			myUserDbVersion = userManager.getDbVersion(); 
		}
	}
}

class UserData {
	public static UserRecord[] getNewRecords(ArrayList<UserContainer> users) {
		int recIndex = 0;

		UserRecord [] records = new UserRecord[users.size()];
		for(UserContainer currentUser:users) { 
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
