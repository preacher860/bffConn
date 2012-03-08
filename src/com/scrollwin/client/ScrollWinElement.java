package com.scrollwin.client;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.HoverEvent;
import com.smartgwt.client.widgets.events.HoverHandler;
import com.smartgwt.client.widgets.events.MouseOutEvent;
import com.smartgwt.client.widgets.events.MouseOutHandler;
import com.smartgwt.client.widgets.events.MouseOverEvent;
import com.smartgwt.client.widgets.events.MouseOverHandler;
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

	private VStack messageStack = new VStack();
	private VStack imageStack = new VStack();
	private HStack infoPane	= new HStack();
	private HStack iconPane = new HStack();
	private HTMLPane userMessagePane = new HTMLPane();
	private Label userInfoLabel = new Label();
	private Img userImage;
	private Img starIcon = new Img("stargray.png", 16, 16);
	private Img deleteIcon = new Img("deletegray.png", 16, 16);
	private int seqId = 0;
	private userCallbackInterface myUserCallbackInterface;
	private String myMessageOriginatingUser;
	private MessageContainer myMessage = null;
	
	public ScrollWinElement(MessageContainer message, UserContainer user, UserContainer myself, userCallbackInterface cb)
	{
		myUserCallbackInterface = cb;
		myMessageOriginatingUser = user.getNick();
		myMessage = message;
		
		// If message hidden (deleted), no need to perform all the stuff, return immediatly
		if (myMessage.isMessageDeleted()){
			hide();
			return;
		}
		
		Integer kittenSelect = 48 + message.getMessageUserId();
		if(user.getAvatarURL().isEmpty())
			userImage = new Img("http://placekitten.com/" + kittenSelect + "/" + kittenSelect, 36, 36);
		else
			userImage = new Img(user.getAvatarURL(), 36, 36);
		
		userImage.setBorder("2px groove #808080");
		
		setBorder("1px solid #808080");
		setBackgroundColor("#E0E0E0");
		setHoverStyle("messageBoxHover");
		
		setWidth100();
		setHeight(40);
		
		iconPane.setBackgroundColor("#B0B0B0");
		iconPane.setHeight(18);
		iconPane.setWidth("40%");
		iconPane.addMember(starIcon);
		iconPane.addMember(deleteIcon);
		iconPane.setOpacity(0);
		iconPane.setShowHover(true);
		
		
		infoPane.setBackgroundColor("#B0B0B0");
		infoPane.addMember(userInfoLabel);
		infoPane.addMember(iconPane);
		infoPane.setWidth100();
		infoPane.setHeight("40%");
		
		messageStack.addMember(userMessagePane);
		messageStack.addMember(infoPane);
		messageStack.setWidth("94%");
		
        userMessagePane.setAlign(Alignment.LEFT);
        if(isMessageForLoggedUser(message, myself))
        	userMessagePane.setBackgroundColor("#88FB9E"); 
        else
        	userMessagePane.setBackgroundColor("#C3D9FF"); // debug blue
        //userMessagePane.setBackgroundColor("#E0E0E0"); 
        userMessagePane.setPadding(5);
        userMessagePane.setContents(message.getMessage());
        userMessagePane.setHeight("60%");
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
        	infoLabelContents += " - " + message.getMessageLocal();
        userInfoLabel.setContents(infoLabelContents);
        						  
        userInfoLabel.setWidth("60%");
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
		
		starIcon.setShowRollOver(true);
		deleteIcon.setShowRollOver(true);
		imageStack.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				myUserCallbackInterface.avatarClicked(myMessageOriginatingUser);
			}
		});
		
		starIcon.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				myUserCallbackInterface.starClicked(myMessage.getMessageSeqId());
				
			}});
		
		deleteIcon.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				myUserCallbackInterface.deleteClicked(myMessage.getMessageSeqId());
				
			}});

		addMouseOverHandler (new MouseOverHandler(){
			@Override
			public void onMouseOver(MouseOverEvent event) {
				iconPane.setOpacity(100);
			}
		});
		
		addMouseOutHandler(new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				iconPane.setOpacity(0);
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
	
	public MessageContainer getMessage(){
		return myMessage;
	}
	
	public void updateMessage(MessageContainer message){
		myMessage = message;
		if (myMessage.isMessageDeleted())
			hide();
	}
}
