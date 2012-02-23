package com.scrollwin.client;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VStack;

public class ScrollWinElement extends HStack {

	//HStack mainStack = new HStack();
	private VStack messageStack = new VStack();
	private VStack imageStack = new VStack();
	private Label userIdLabel = new Label();
	//Label userMessageLabel = new Label();
	private HTMLPane userMessageLabel = new HTMLPane();
	private Label userInfoLabel = new Label();
	private Img userImage;
	
	public ScrollWinElement(MessageContainer message, UserContainer user)
	{
		Integer kittenSelect = 48 + message.getMessageUserId();
		if(user.getAvatarURL().isEmpty())
			userImage = new Img("http://placekitten.com/" + kittenSelect + "/" + kittenSelect, 36, 36);
		else
			userImage = new Img(user.getAvatarURL(), 36, 36);
		
		userImage.setBorder("2px groove #808080");
		
		setBorder("1px solid #808080");
		setBackgroundColor("#E0E0E0");
		
		setWidth100();
		setHeight(40);
		messageStack.addMember(userMessageLabel);
		messageStack.addMember(userInfoLabel);
		messageStack.setWidth("94%");
		
        userMessageLabel.setAlign(Alignment.LEFT);
        userMessageLabel.setBackgroundColor("#C3D9FF"); // debug blue
        //userMessageLabel.setBackgroundColor("#E0E0E0"); 
        userMessageLabel.setPadding(5);
        userMessageLabel.setContents(message.getMessage());
        userMessageLabel.setHeight(20);
        userMessageLabel.setStyleName("chatText");
        userMessageLabel.setOverflow(Overflow.VISIBLE);
        
        userInfoLabel.setAlign(Alignment.LEFT);  
        userInfoLabel.setBackgroundColor("#B0B0B0"); // debug gray
        //userInfoLabel.setBackgroundColor("#E0E0E0");
        userInfoLabel.setPadding(3);
        userInfoLabel.setContents("Message seq " + message.getMessageSeqId() + "   Envoyé par " + 
        						  user.getNick() + "  id: " + message.getMessageUserId() + 
        						  "  à: " + message.getMessageDate());
        userInfoLabel.setHeight(8);
        userInfoLabel.setStyleName("chatInfo");
        
        
        LayoutSpacer spacer = new LayoutSpacer();
        spacer.setHeight(1);
        imageStack.setPadding(4);
		imageStack.setBackgroundColor("#E0E0E0");
        imageStack.setWidth("6%");
		imageStack.setAlign(Alignment.CENTER);
		imageStack.setAlign(VerticalAlignment.CENTER);

		imageStack.addMember(spacer);
		imageStack.addMember(userImage);
		
		addMember(imageStack);
		addMember(messageStack);
	}
}
