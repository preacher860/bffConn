package com.scrollwin.client;
import java.util.ArrayList;

import com.google.gwt.user.client.DOM;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.BkgndRepeat;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.tile.TileRecord;
import com.smartgwt.client.widgets.tile.events.RecordClickEvent;
import com.smartgwt.client.widgets.tile.events.RecordClickHandler;
import com.smartgwt.client.widgets.viewer.DetailViewerField;


public class UserTileDisplay extends VStack {
	private TileGrid usersGrid = new TileGrid();
	private Label titleLabel = new Label();
	private ArrayList<UserContainer> knownUsers = new ArrayList<UserContainer>();
	private userCallbackInterface myUserCallbackInterface;
	int myUserDbVersion = 0;
	
	public UserTileDisplay(userCallbackInterface callbackInterface){
		myUserCallbackInterface = callbackInterface;
				
		//setHeight(210);
		setHeight(100);
		
		setWidth(216);
	
		//setMargin(5);
		
		setOverflow(Overflow.VISIBLE);
		setBackgroundColor("#ffffff");
		//setEdgeImage("borders/sharpframe_10.png");
        //setEdgeSize(6); 
        //setBackgroundImage("backgrounds/lgrey127.gif");
        //setBackgroundRepeat(BkgndRepeat.REPEAT);
		setShowEdges(true);
		setEdgeSize(3);
		setShowShadow(true);
		setShadowSoftness(3);
		setShadowOffset(4);
		//setShadowDepth(2);
		
		
		usersGrid.setWidth100();
		usersGrid.setHeight(100);
		usersGrid.setOverflow(Overflow.VISIBLE);
		usersGrid.setTileWidth(60);
		usersGrid.setTileHeight(62);
		usersGrid.setTilesPerLine(3);
		usersGrid.setShowAllRecords(true);
		usersGrid.setAutoFetchData(false);
		usersGrid.setAnimateTileChange(true);
		usersGrid.setTileMargin(2);
		usersGrid.setShowEdges(false);
		
		titleLabel.setWidth100();
		titleLabel.setHeight(30);
		titleLabel.setContents("<b>En ligne<b>");
		//titleLabel.setAlign(Alignment.CENTER);
		titleLabel.setPadding(5);
		titleLabel.setStyleName("myTitleBox");
		titleLabel.setBackgroundImage("titleback30.png");
		titleLabel.setBackgroundRepeat(BkgndRepeat.REPEAT_X);
				
		DetailViewerField pictureField = new DetailViewerField("picture");
		pictureField.setType("image");
		pictureField.setImageURLPrefix("");
		pictureField.setImageWidth(40);
		pictureField.setImageHeight(40);
		
		DetailViewerField nameField = new DetailViewerField("name");
		
		usersGrid.setFields(pictureField, nameField);
		
		addMember(titleLabel);
		addMember(usersGrid);
		usersGrid.addRecordClickHandler(new RecordClickHandler() {  
			@Override
			public void onRecordClick(RecordClickEvent event) {
				myUserCallbackInterface.avatarClicked(event.getRecord().getAttribute("name"));
				usersGrid.deselectAllRecords();
			}  
        }); 
	}
	
	public void UpdateOnlineUsers(UserManager userManager){
		if(userManager.getDbVersion() != myUserDbVersion)
		{
			if(userManager.getOnlineUsers() != null)
				usersGrid.setData(UserData.getNewRecords(userManager.getOnlineUsers()));
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
