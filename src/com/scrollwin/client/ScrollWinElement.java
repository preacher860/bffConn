package com.scrollwin.client;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.HTMLFlow;
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
	//Label userMessagePane = new Label();
	private HTMLPane userMessagePane = new HTMLPane();
	private Label userInfoLabel = new Label();
	private Img userImage;
	private int seqId = 0;
	private userCallbackInterface myUserCallbackInterface;
	private String myMessageOriginatingUser;
	
	public ScrollWinElement(MessageContainer message, UserContainer user, UserContainer myself, userCallbackInterface cb)
	{
		myUserCallbackInterface = cb;
		myMessageOriginatingUser = user.getNick();
		
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
		messageStack.addMember(userMessagePane);
		messageStack.addMember(userInfoLabel);
		messageStack.setWidth("94%");
		
        userMessagePane.setAlign(Alignment.LEFT);
        if(isMessageForLoggedUser(message, myself))
        	userMessagePane.setBackgroundColor("#88FB9E"); 
        else
        	userMessagePane.setBackgroundColor("#C3D9FF"); // debug blue
        //userMessagePane.setBackgroundColor("#E0E0E0"); 
        userMessagePane.setPadding(5);
        userMessagePane.setContents(message.getMessage());
        userMessagePane.setHeight(20);
        userMessagePane.setMaxWidth(80);
        userMessagePane.setStyleName("chatText");
        userMessagePane.setOverflow(Overflow.VISIBLE);
        
        userInfoLabel.setAlign(Alignment.LEFT);  
        userInfoLabel.setBackgroundColor("#B0B0B0"); // debug gray
        //userInfoLabel.setBackgroundColor("#E0E0E0");
        userInfoLabel.setPadding(3);
        String infoLabelContents = "Message " + message.getMessageSeqId() + "   envoyé par " + 
				  					user.getNick() + "  le " + message.getMessageDate() + 
				  					" à " + message.getMessageTime();
        if(message.getMessageLocal().contentEquals("") != true)
        	infoLabelContents += " depuis " + message.getMessageLocal();
        userInfoLabel.setContents(infoLabelContents);
        						  
        userInfoLabel.setHeight(8);
        userInfoLabel.setStyleName("chatInfo");
        seqId = message.getMessageSeqId();
        
        LayoutSpacer spacer = new LayoutSpacer();
        spacer.setHeight(1);
        imageStack.setPadding(4);
		imageStack.setBackgroundColor("#E0E0E0");
        imageStack.setWidth("6%");
		imageStack.setAlign(Alignment.CENTER);
		imageStack.setAlign(VerticalAlignment.CENTER);
		imageStack.addMember(spacer);
		imageStack.addMember(userImage);
		imageStack.setPrompt(user.getNick());
		imageStack.setHoverStyle("tooltipStyle");
		
		imageStack.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				myUserCallbackInterface.avatarClicked(myMessageOriginatingUser);
			}
		});
		
		addMember(imageStack);
		addMember(messageStack);
	}
	
	public void adjustForContents()
	{
		System.out.println("Adjusting for contents: " + seqId);
		userMessagePane.adjustForContent(true);
	}
	
	public boolean isMessageForLoggedUser(MessageContainer message, UserContainer myself)
	{
		String atUserNick = "@" + myself.getNick();
		
		return message.getMessage().contains(atUserNick);
	}
}
