package com.scrollwin.client;

import java.util.ArrayList;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.HoverEvent;
import com.smartgwt.client.widgets.events.HoverHandler;
import com.smartgwt.client.widgets.events.MouseOutEvent;
import com.smartgwt.client.widgets.events.MouseOutHandler;
import com.smartgwt.client.widgets.events.MouseOverEvent;
import com.smartgwt.client.widgets.events.MouseOverHandler;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
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
	private Img starOverIcon = new Img("stargray_Over.png", 16, 16);
	private Img deleteIcon = new Img("deletegray.png", 16, 16);
	private Img deleteOverIcon = new Img("deletegray_Over.png", 16, 16);
	private Label starLabel = new Label("Étoiler");
	private Label deleteLabel = new Label("Effacer");
	private HStack starStack = new HStack();
	private HStack deleteStack = new HStack();
	private int seqId = 0;
	private userCallbackInterface myUserCallbackInterface;
	private String myMessageOriginatingUser;
	private MessageContainer myMessage = null;
	private boolean starred = false;
	
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
		
		setupStarred(myMessage);
		
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
		
		
		starLabel.setAutoWidth();
		starLabel.setHoverStyle("tooltipStyle");
		deleteLabel.setAutoWidth();
		starOverIcon.setShowHover(true);
		deleteOverIcon.hide();
		LayoutSpacer iconSpacer = new LayoutSpacer();
		iconSpacer.setWidth(3);
		starStack.setWidth(60);
		starStack.addMember(starIcon);
		starStack.addMember(starOverIcon);
		starStack.addMember(iconSpacer);
		starStack.addMember(starLabel);
		if(!starred){
			starStack.setOpacity(0);
			starOverIcon.hide();
		} else {
			starOverIcon.show();
			starIcon.hide();
		}
		
		deleteStack.setWidth(60);
		deleteStack.addMember(deleteIcon);
		deleteStack.addMember(deleteOverIcon);
		deleteStack.addMember(iconSpacer);
		deleteStack.addMember(deleteLabel);
		deleteStack.setOpacity(0);
		
		iconPane.setBackgroundColor("#B0B0B0");
		iconPane.setHeight(18);
		iconPane.setWidth("40%");
		iconPane.addMember(starStack);
		iconPane.addMember(deleteStack);
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
        	userMessagePane.setBackgroundColor("#C3D9FF"); 
        userMessagePane.setPadding(5);
        userMessagePane.setContents(message.getMessage());
        userMessagePane.setHeight("60%");
        userMessagePane.setMaxWidth(80);
        userMessagePane.setStyleName("chatText");
        userMessagePane.setOverflow(Overflow.VISIBLE);
                
        userInfoLabel.setAlign(Alignment.LEFT);  
        userInfoLabel.setBackgroundColor("#B0B0B0"); 
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
		
		//starIcon.setShowRollOver(true);
		starStack.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				myUserCallbackInterface.starClicked(myMessage.getMessageSeqId());
				
			}});
		
		starStack.addMouseOverHandler(new MouseOverHandler (){
			@Override
			public void onMouseOver(MouseOverEvent event) {
				if(!starred){
					starOverIcon.show();
					starIcon.hide();
				}
			}});
		
		starStack.addMouseOutHandler(new MouseOutHandler (){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				if(!starred){
					starOverIcon.hide();
					starIcon.show();
				}
			}});
		
		// May only delete own messages
		if(myMessage.getMessageUserId() == RuntimeData.getInstance().getUserId()) {
			//deleteIcon.setShowRollOver(true);
			deleteStack.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					myUserCallbackInterface.deleteClicked(myMessage.getMessageSeqId());
					
				}});
			deleteStack.addMouseOverHandler(new MouseOverHandler (){
				@Override
				public void onMouseOver(MouseOverEvent event) {
					deleteOverIcon.show();
					deleteIcon.hide();
				}});
			
			deleteStack.addMouseOutHandler(new MouseOutHandler (){
				@Override
				public void onMouseOut(MouseOutEvent event) {
					deleteOverIcon.hide();
					deleteIcon.show();
				}});
		}
		
		imageStack.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				myUserCallbackInterface.avatarClicked(myMessageOriginatingUser);
			}
		});

		addMouseOverHandler (new MouseOverHandler(){
			@Override
			public void onMouseOver(MouseOverEvent event) {
				starStack.setOpacity(100);
				deleteStack.setOpacity(100);
			}
		});
		
		addMouseOutHandler(new MouseOutHandler(){
			@Override
			public void onMouseOut(MouseOutEvent event) {
				if(!starred)
					starStack.setOpacity(0);
				deleteStack.setOpacity(0);
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
		
		setupStarred(myMessage);
	}
	
	private void setupStarred(MessageContainer message)
	{
		if(myMessage.getMessageStars().length() > 0){
			String prompt = "";
			ArrayList<String> nickList= UserManager.getInstance().idListToArray(myMessage.getMessageStars()); 
			for(String nick:nickList)
				prompt += nick + "<br>";
			starStack.setPrompt(prompt);
			starStack.setShowHover(true);
			starStack.setHoverStyle("tooltipStyle");
			starLabel.setContents("x" + nickList.size());
			starLabel.setHoverStyle("tooltipStyle");
			starStack.setOpacity(100);
			starIcon.hide();
			starOverIcon.show();
			starred = true;
		} else{
			starStack.setPrompt("");
			starStack.setShowHover(false);
			starLabel.setContents("Étoiler");
			starStack.setOpacity(0);
			starIcon.show();
			starOverIcon.hide();
			starred = false;
		}
	}
}
